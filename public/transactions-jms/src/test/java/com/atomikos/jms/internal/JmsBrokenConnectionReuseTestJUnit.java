/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.XAConnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.jms.SessionCreationMode;

// cf case 230781 (engineering follow-up to support case 230664)
/**
 * Two paths by which a physically-broken JMS connection could be recognised:
 *
 * 1. SYNCHRONOUS (works today) - a JMSException thrown through a proxied call
 *    marks the pooled connection erroneous, so borrow validation rejects it and
 *    it is destroyed and replaced rather than reused.
 * 2. ASYNCHRONOUS (the gap, case 230664) - the provider signals the break only
 *    via ExceptionListener.onException. Atomikos registers no ExceptionListener,
 *    and the application's own listener (e.g. Spring CachingConnectionFactory)
 *    passes straight through the proxy to the vendor connection, so Atomikos is
 *    never in the listener path: the break is never observed and the broken
 *    connection is handed back out of the pool and reused.
 *
 * The second test is RED until the fix: Atomikos must register its own listener
 * on the physical connection and intercept the proxy's set/getExceptionListener
 * so an application-supplied listener is chained under Atomikos's (both observe
 * the break, neither clobbers the other). It is the regression test that detects
 * the bug and turns GREEN with the fix.
 */
public class JmsBrokenConnectionReuseTestJUnit {

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
	public void aSynchronousExceptionMarksTheConnectionErroneousSoItIsDestroyedNotReused() throws Exception {
		// given a pooled connection whose next session creation fails synchronously
		XAConnection xaConnection = mock(XAConnection.class);
		when(xaConnection.createXASession()).thenThrow(new JMSException("simulated broken connection"));
		ConnectionPoolProperties props = mock(ConnectionPoolProperties.class);
		when(props.getLocalTransactionMode()).thenReturn(false);
		AtomikosPooledJmsConnection pooled = new AtomikosPooledJmsConnection(
				SessionCreationMode.PRE_6_0, xaConnection, mock(XATransactionalResource.class), props);

		// a fresh pooled connection is healthy and passes borrow validation
		assertFalse(pooled.isErroneous());
		pooled.testUnderlyingConnection(); // does not throw

		// when a proxied call throws a JMSException synchronously
		Connection proxy = pooled.createConnectionProxy();
		try {
			proxy.createSession(false, Session.AUTO_ACKNOWLEDGE);
			fail("expected the simulated JMSException to propagate");
		} catch (JMSException expected) {
			// the synchronous failure is what Atomikos observes
		}

		// then the connection is marked erroneous and borrow validation rejects it
		assertTrue("a synchronous JMSException must mark the connection erroneous", pooled.isErroneous());
		try {
			pooled.testUnderlyingConnection();
			fail("expected borrow validation to reject the erroneous connection");
		} catch (CreateConnectionException expected) {
			// this is the destroy-and-replace path - the only one Atomikos currently has
		}
	}

	@Test
	public void aBreakSignalledOnlyAsynchronouslyMustAlsoMarkTheConnectionErroneous() throws Exception {
		// given a healthy pooled connection over a physical XAConnection
		XAConnection xaConnection = mock(XAConnection.class);
		ConnectionPoolProperties props = mock(ConnectionPoolProperties.class);
		when(props.getLocalTransactionMode()).thenReturn(false);
		AtomikosPooledJmsConnection pooled = new AtomikosPooledJmsConnection(
				SessionCreationMode.PRE_6_0, xaConnection, mock(XATransactionalResource.class), props);
		assertFalse(pooled.isErroneous());

		// Atomikos must register its own listener on the physical connection so it can
		// observe a break that surfaces ONLY asynchronously (case 230664: MQ fires
		// ExceptionListener.onException; no synchronous JMSException reaches the proxy).
		ArgumentCaptor<ExceptionListener> captor = ArgumentCaptor.forClass(ExceptionListener.class);
		verify(xaConnection, times(1)).setExceptionListener(captor.capture());
		ExceptionListener atomikosListener = captor.getValue();

		// and the application (e.g. Spring CachingConnectionFactory) registers its own
		// listener via the borrowed connection - this must be chained, NOT passed straight
		// through to the vendor connection where it would clobber Atomikos's listener.
		Connection proxy = pooled.createConnectionProxy();
		ExceptionListener appListener = mock(ExceptionListener.class);
		proxy.setExceptionListener(appListener);
		verify(xaConnection, times(1)).setExceptionListener(any(ExceptionListener.class)); // still only Atomikos's
		assertSame("the application's listener must be retrievable, not the internal one",
				appListener, proxy.getExceptionListener());

		// when the provider signals the broken connection asynchronously
		JMSException broken = new JMSException("simulated MQRC_CONNECTION_BROKEN (2009)");
		atomikosListener.onException(broken);

		// then the pooled connection is erroneous and borrow validation rejects it,
		// so it is destroyed and replaced instead of being handed back out of the pool
		assertTrue("an asynchronous break must mark the connection erroneous", pooled.isErroneous());
		try {
			pooled.testUnderlyingConnection();
			fail("expected borrow validation to reject the connection broken asynchronously");
		} catch (CreateConnectionException expected) {
			// the destroy-and-replace path we want the fix to reach for async breaks too
		}

		// and the application's own listener still received the notification (chaining preserved)
		verify(appListener).onException(broken);
	}

}
