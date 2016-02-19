/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
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
		pool = new ConnectionPool(cf, cpp);
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
