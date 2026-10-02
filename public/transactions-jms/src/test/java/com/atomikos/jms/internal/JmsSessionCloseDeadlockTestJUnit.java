/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.XAConnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.XPooledConnection;
import com.atomikos.datasource.pool.XPooledConnectionEventListener;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.jms.SessionCreationMode;

// cf case 230921 (engineering fix; reported via support case 230664, GitHub #227)
/**
 * Concurrent close of two sessions of the SAME pooled connection must not deadlock.
 *
 * The session proxy takes a monitor on itself in its synchronized invoke() and holds
 * it across close() -> destroy(). destroy() then calls owner.onTerminated() ->
 * AtomikosJmsConnectionProxy.closeAllPendingSessions(), which closes the OTHER sessions
 * and so grabs their proxy monitors -- all while still holding this session's monitor.
 * Two threads each closing a different session therefore hold their own monitor and
 * reach for the other's: a lock-ordering deadlock that blocks the pool from replacing
 * the broken connection (reported by S&P Global, thread dump on case 230664).
 *
 * Deterministic reproduction: closeAllPendingSessions() only fires once the connection
 * proxy is closed (isAvailable() gate), so thread 1 closes the CONNECTION (setting that
 * flag) and thread 2 closes a session directly. A CyclicBarrier placed in each vendor
 * session's close() -- reached inside destroy() AFTER the monitor is taken and BEFORE
 * the cross-session close -- forces both threads to hold their own monitor before either
 * reaches for the other's, making the fatal interleaving certain rather than timing-
 * dependent. The deadlock is then observed actively via ThreadMXBean.findDeadlockedThreads()
 * (no self-hang, no long timeout); the worker threads are daemons so a RED run never
 * leaks non-daemon threads into the suite.
 *
 * RED until the fix; GREEN once destroy() no longer drives a cross-session close while
 * holding a session monitor.
 */
public class JmsSessionCloseDeadlockTestJUnit {

	private static final String FORCE_XA_MODE_FLAG = "com.atomikos.icatch.feature.226870";

	@Before
	public void setUp() {
		Configuration.getConfigProperties().setProperty(FORCE_XA_MODE_FLAG, "false");
	}

	@After
	public void tearDown() {
		Configuration.getConfigProperties().setProperty(FORCE_XA_MODE_FLAG, "false");
	}

	@Test
	public void concurrentCloseOfTwoSessionsOnOnePooledConnectionMustNotDeadlock() throws Exception {
		// given a pooled connection with two borrowed non-XA sessions over mocked vendor sessions
		XAConnection xaConnection = mock(XAConnection.class);
		Session vendorA = mock(Session.class);
		Session vendorB = mock(Session.class);
		when(xaConnection.createSession(anyBoolean(), anyInt())).thenReturn(vendorA, vendorB);
		ConnectionPoolProperties props = mock(ConnectionPoolProperties.class);
		when(props.getLocalTransactionMode()).thenReturn(true); // non-XA sessions: no JTA context needed

		AtomikosPooledJmsConnection pooled = new AtomikosPooledJmsConnection(
				SessionCreationMode.PRE_6_0, xaConnection, mock(XATransactionalResource.class), props);

		// count how often the pool is told the connection is available. Announcing it more
		// than once under concurrent close would re-announce an in-use connection -> double-borrow.
		final AtomicInteger terminationCallbacks = new AtomicInteger(0);
		pooled.registerXPooledConnectionEventListener(new XPooledConnectionEventListener<Connection>() {
			public void onXPooledConnectionTerminated(XPooledConnection<Connection> c) {
				terminationCallbacks.incrementAndGet();
			}
		});

		Connection conn = pooled.createConnectionProxy();
		conn.createSession(false, Session.AUTO_ACKNOWLEDGE);                        // session 1 (vendorA)
		final Session session2 = conn.createSession(false, Session.AUTO_ACKNOWLEDGE); // session 2 (vendorB)

		// barrier: both threads park in their vendor close() (inside destroy(), holding their own
		// session monitor) before either advances to the cross-session close. one-shot per vendor
		// session so the idempotent second close does not re-enter it.
		final CyclicBarrier bothHoldTheirMonitor = new CyclicBarrier(2);
		parkOnceInClose(vendorA, bothHoldTheirMonitor);
		parkOnceInClose(vendorB, bothHoldTheirMonitor);

		// thread 1 closes the connection (sets closed -> onTerminated will fire closeAllPendingSessions)
		Thread closeConnection = daemon("close-connection", () -> {
			try { conn.close(); } catch (Exception ignore) { /* deadlock is what we detect, not exceptions */ }
		});
		// thread 2 closes the other session directly (models the vendor's error-callback thread
		// closing concurrently while the application thread closes the connection)
		Thread closeSession = daemon("close-session", () -> {
			try { session2.close(); } catch (Exception ignore) { }
		});
		closeConnection.start();
		closeSession.start();

		// actively ask the JVM whether the two workers form a deadlock cycle, polling briefly;
		// on the fixed code they simply finish and we stop early.
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long[] deadlocked = null;
		for (int i = 0; i < 50; i++) {
			Thread.sleep(100);
			deadlocked = bean.findDeadlockedThreads();
			if (deadlocked != null) {
				break;
			}
			if (!closeConnection.isAlive() && !closeSession.isAlive()) {
				break; // fixed path: both closes completed without deadlock
			}
		}

		if (deadlocked != null) {
			fail("Concurrent close of two sessions on one pooled connection deadlocked:\n"
					+ describe(bean, deadlocked));
		}

		closeConnection.join(2000);
		closeSession.join(2000);
		assertFalse("close-connection thread did not finish - stuck without a detected cycle",
				closeConnection.isAlive());
		assertFalse("close-session thread did not finish - stuck without a detected cycle",
				closeSession.isAlive());

		// ADR-084: termination is single-shot per borrow. More than one announcement would
		// re-announce an already-reissued connection to the pool -> double-borrow. This guards
		// against a fix that merely removes the deadlock while leaving both threads announcing.
		assertEquals("connection availability must be announced exactly once under concurrent close",
				1, terminationCallbacks.get());
	}

	private void parkOnceInClose(Session vendorSession, CyclicBarrier barrier) throws JMSException {
		final AtomicBoolean parked = new AtomicBoolean(false);
		doAnswer(invocation -> {
			if (parked.compareAndSet(false, true)) {
				try {
					barrier.await(5, TimeUnit.SECONDS);
				} catch (Exception e) {
					// barrier timeout/interrupt: let the close proceed; the deadlock check still runs
				}
			}
			return null;
		}).when(vendorSession).close();
	}

	private Thread daemon(String name, Runnable body) {
		Thread t = new Thread(body, name);
		t.setDaemon(true);
		return t;
	}

	private String describe(ThreadMXBean bean, long[] deadlockedIds) {
		StringBuilder sb = new StringBuilder();
		for (ThreadInfo info : bean.getThreadInfo(deadlockedIds)) {
			if (info == null) {
				continue;
			}
			sb.append("  ").append(info.getThreadName())
			  .append(" is ").append(info.getThreadState())
			  .append(" waiting on ").append(info.getLockName())
			  .append(" held by ").append(info.getLockOwnerName())
			  .append("\n");
		}
		return sb.toString();
	}

}
