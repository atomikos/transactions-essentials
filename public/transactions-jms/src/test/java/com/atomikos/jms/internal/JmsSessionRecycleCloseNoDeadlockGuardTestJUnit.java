/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.internal;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.XAConnection;
import javax.jms.XASession;
import javax.transaction.xa.XAResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.XPooledConnection;
import com.atomikos.datasource.pool.XPooledConnectionEventListener;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.datasource.xa.XID;
import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.jms.SessionCreationMode;
import com.atomikos.recovery.TxState;

// cf case 231540 (recycle-vs-close lock inversion investigated; NOT a reachable deadlock - by design)
/**
 * Guard for the recycle-vs-close lock-ordering invariant on a pooled JMS connection
 * (case 231540). There is a real lock inversion in shape between two monitors:
 * <ul>
 *   <li><b>M_sessions</b> - the {@code sessions} list monitor in
 *       {@link AtomikosJmsConnectionProxy}: {@code recycleSession()} holds it and calls
 *       {@code proxy.recycle()} -&gt; {@link AtomikosJmsXaSessionProxy#recycle()}
 *       {@code synchronized(this)} (order M_sessions -&gt; M_x);</li>
 *   <li><b>M_x</b> - a session-proxy monitor: {@code close()} runs under the
 *       {@code synchronized invoke()} and its {@code onTerminated} fires can take
 *       {@code synchronized(sessions)} (order M_x -&gt; M_sessions).</li>
 * </ul>
 *
 * <p>The AB-BA nevertheless cannot form, because the two preconditions are disjoint on
 * the SAME session:
 * <ol>
 *   <li>{@code recycleSession()} only selects a session that is enlisted (BranchEnlisted)
 *       or ended (BranchEnded) - never NotInBranch;</li>
 *   <li>{@code close()} only reaches M_sessions (via {@code onTerminated}) when the session
 *       becomes <i>terminated</i>, i.e. it was NotInBranch. An enlisted session's close goes
 *       to BranchEnded (not terminated), so it never fires the terminated chain and never
 *       takes M_sessions.</li>
 * </ol>
 * So a recycle-selectable session's close never grabs M_sessions, and a session whose close
 * does is not recycle-selectable (an empirical 4000-iteration race reproduced no deadlock).
 *
 * <p>This test locks in the two production facts that make the states disjoint, so a future
 * change to the session state machine that broke the disjointness - and thereby re-opened the
 * cycle - would fail here rather than ship silently. Boundaries (ADR-031): the vendor JMS
 * driver is mocked; every Atomikos object in the cycle (the pooled connection, both proxies,
 * the real {@link XATransactionalResource} and its {@code SessionHandleState}) is real. The
 * JTA context is a JTA-marked {@link CompositeTransaction} handed out thread-independently so
 * {@code recycleSession()}'s selection predicate matches.
 */
public class JmsSessionRecycleCloseNoDeadlockGuardTestJUnit {

    private static final String FORCE_XA_MODE_FLAG = "com.atomikos.icatch.feature.226870";
    private static final String RESOURCE_NAME = "recycleCloseDeadlockResource";

    /** Real resource so enlistment (notifyBeforeUse) builds a real branch, as in production. */
    private static final class FixtureResource extends XATransactionalResource {
        FixtureResource() {
            super(RESOURCE_NAME);
        }

        @Override
        protected XAResource refreshXAConnection() {
            return mock(XAResource.class);
        }

        @Override
        protected XID createXid(String tid) {
            return new XID(tid, "branch", RESOURCE_NAME);
        }
    }

    private CompositeTransactionManager previousCtm;
    private CompositeTransaction ct;

    @Before
    public void setUp() throws Exception {
        Configuration.getConfigProperties().setProperty(FORCE_XA_MODE_FLAG, "false");

        // A JTA-marked composite transaction, thread-independent so recycleSession() selects our session.
        ct = mock(CompositeTransaction.class);
        CompositeCoordinator coordinator = mock(CompositeCoordinator.class);
        when(coordinator.getCoordinatorId()).thenReturn("tid-231540");
        when(ct.getCompositeCoordinator()).thenReturn(coordinator);
        when(ct.getTid()).thenReturn("tid-231540");
        when(ct.getLineage()).thenReturn(null);
        when(ct.isSerial()).thenReturn(false);
        when(ct.isSameTransaction(any(CompositeTransaction.class))).thenReturn(true);
        when(ct.getTimeout()).thenReturn(30000L);
        when(ct.getState()).thenReturn(TxState.ACTIVE);
        // makes TransactionManagerImp.isJtaTransaction(ct) == true
        when(ct.getProperty(TransactionManagerImp.JTA_PROPERTY_NAME)).thenReturn("true");

        CompositeTransactionManager ctm = mock(CompositeTransactionManager.class);
        when(ctm.getCompositeTransaction()).thenReturn(ct);

        previousCtm = Configuration.getCompositeTransactionManager();
        Configuration.installCompositeTransactionManager(ctm);
    }

    @After
    public void tearDown() {
        Configuration.getConfigProperties().setProperty(FORCE_XA_MODE_FLAG, "false");
        Configuration.installCompositeTransactionManager(previousCtm);
    }

    /**
     * Proves the two production facts that make the recycle/close states disjoint:
     * <ol>
     *   <li><b>RECYCLE really takes M_x.</b> After an XA session is enlisted, a second
     *       {@code createSession()} in the same tx must be satisfied by {@code recycleSession()}
     *       (which calls {@code proxy.recycle()} -&gt; {@code synchronized(this)} = M_x), NOT by a
     *       fresh {@code createXASession()} - asserted via one vendor {@code createXASession()}
     *       for two {@code createSession()} calls.</li>
     *   <li><b>CLOSE of the enlisted session does NOT fire onTerminated.</b> Because the session
     *       is BranchEnlisted, {@code notifySessionClosed()} moves it to BranchEnded (not
     *       Terminated), so the terminated chain that would take M_sessions never fires -
     *       asserted via the pool termination callback never being invoked.</li>
     * </ol>
     * If a future change made close() of a recycle-selectable session fire the terminated chain,
     * fact (2) would fail here - flagging that the recycle-vs-close cycle had been re-opened.
     */
    @Test
    public void recycleSelectsEnlistedSessionButCloseDoesNotFireTerminatedEvent() throws Exception {
        XAConnection xaConnection = mock(XAConnection.class);
        XASession vendorSession = mock(XASession.class);
        when(vendorSession.getXAResource()).thenReturn(mock(XAResource.class));
        when(xaConnection.createXASession()).thenReturn(vendorSession);
        MessageProducer vendorProducer = mock(MessageProducer.class);
        when(vendorSession.createProducer(any(Destination.class))).thenReturn(vendorProducer);

        ConnectionPoolProperties props = mock(ConnectionPoolProperties.class);
        when(props.getLocalTransactionMode()).thenReturn(false); // -> XA sessions

        AtomikosPooledJmsConnection pooled = new AtomikosPooledJmsConnection(
                SessionCreationMode.PRE_6_0, xaConnection, new FixtureResource(), props);

        final AtomicInteger poolTerminations = new AtomicInteger(0);
        pooled.registerXPooledConnectionEventListener(new XPooledConnectionEventListener<Connection>() {
            public void onXPooledConnectionTerminated(XPooledConnection<Connection> c) {
                poolTerminations.incrementAndGet();
            }
        });

        Connection conn = pooled.createConnectionProxy();
        Session sessionX = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = sessionX.createProducer(mock(Destination.class));
        producer.send(mock(Message.class)); // enlist -> BranchEnlisted, active in ct

        // (1) second createSession in the same tx must RECYCLE the enlisted session (takes M_x)
        conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        verify(xaConnection, times(1)).createXASession();

        // (2) closing the still-enlisted session must NOT fire the pool termination chain (no M_sessions grab)
        sessionX.close();
        org.junit.Assert.assertEquals(
                "closing a session still enlisted in the tx must not announce pool availability",
                0, poolTerminations.get());
    }

}
