/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.datasource.pool.event.ConnectionPoolExhaustedEvent;
import com.atomikos.datasource.pool.event.PooledConnectionCreatedEvent;
import com.atomikos.datasource.pool.event.PooledConnectionDestroyedEvent;
import com.atomikos.datasource.pool.event.PooledConnectionReapedEvent;
import com.atomikos.icatch.event.Event;
import com.atomikos.icatch.event.EventListener;
import com.atomikos.publish.EventPublisher;

public class ConnectionPoolEventsTestJUnit {

	private ConnectionPool pool;
	private TestEventListener listener;
	private ConnectionPoolProperties cpp;
	private XPooledConnection xpc;


	
	@Before
	public void setUp() throws Exception {
		ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
		Reapable connProxy = Mockito.mock(Reapable.class);
		xpc = Mockito.mock(XPooledConnection.class);
		Mockito.when(cf.createPooledConnection()).thenReturn(xpc);
		Mockito.when(xpc.createConnectionProxy()).thenReturn(connProxy);
		Mockito.when(xpc.isAvailable()).thenReturn(true);
		cpp = Mockito.mock(ConnectionPoolProperties.class);
		Mockito.when(cpp.getMaxPoolSize()).thenReturn(1);
		Mockito.when(cpp.getReapTimeout()).thenReturn(1);
		pool = new ConnectionPoolWithSynchronizedValidation(cf, cpp);
		listener = new TestEventListener();
		EventPublisher.registerEventListener(listener);
	}
	
	@After
	public void tearDown() throws Exception {
		if (pool != null) pool.destroy();
	}


	@Test
	public void testConnectionPoolExhaustedEvent() throws Exception {
		Mockito.when(cpp.getMaxPoolSize()).thenReturn(0);
		try {
			pool.borrowConnection();
		} catch (PoolExhaustedException ok) {}
		Assert.assertTrue(listener.event instanceof ConnectionPoolExhaustedEvent);
	}

	@Test
	public void testPooledConnectionCreatedEvent() throws Exception {
		pool.borrowConnection();
		Assert.assertTrue(listener.event instanceof PooledConnectionCreatedEvent);
	}
	
	@Test
	public void testPooledConnectionDestroyedEvent() throws Exception {
		pool.borrowConnection();
		pool.destroy();
		Assert.assertTrue(listener.event instanceof PooledConnectionDestroyedEvent);
	}
	
	@Test
	public void testPooledConnectionReapedEvent() throws Exception {
		pool.borrowConnection();
		Mockito.when(xpc.isAvailable()).thenReturn(false);
		Mockito.when(xpc.getLastTimeAcquired()).thenReturn(0L);
		pool.reapPool();
		Assert.assertTrue(listener.event instanceof PooledConnectionReapedEvent);
	}
	
	private static class TestEventListener implements EventListener {

		private Event event;

		@Override
		public void eventOccurred(Event event) {
			this.event = event;
		}
		
	}
	
}
