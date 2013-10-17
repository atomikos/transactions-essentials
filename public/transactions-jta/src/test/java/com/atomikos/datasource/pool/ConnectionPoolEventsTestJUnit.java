package com.atomikos.datasource.pool;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.datasource.pool.event.ConnectionPoolExhaustedEvent;
import com.atomikos.datasource.pool.event.PooledConnectionCreatedEvent;
import com.atomikos.datasource.pool.event.PooledConnectionDestroyedEvent;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.event.Event;
import com.atomikos.icatch.event.EventListener;
import com.atomikos.icatch.publish.EventPublisher;

public class ConnectionPoolEventsTestJUnit {

	private ConnectionPool pool;
	private TestEventListener listener;
	private ConnectionPoolProperties cpp;


	
	@Before
	public void setUp() throws Exception {
		ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
		Reapable connProxy = Mockito.mock(Reapable.class);
		XPooledConnection xpc = Mockito.mock(XPooledConnection.class);
		Mockito.when(cf.createPooledConnection()).thenReturn(xpc);
		Mockito.when(xpc.createConnectionProxy(Mockito.any(HeuristicMessage.class))).thenReturn(connProxy);
		Mockito.when(xpc.isAvailable()).thenReturn(true);
		cpp = Mockito.mock(ConnectionPoolProperties.class);
		Mockito.when(cpp.getMaxPoolSize()).thenReturn(1);
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
			pool.borrowConnection(null);
		} catch (PoolExhaustedException ok) {}
		Assert.assertTrue(listener.event instanceof ConnectionPoolExhaustedEvent);
	}

	@Test
	public void testPooledConnectionCreatedEvent() throws Exception {
		pool.borrowConnection(null);
		Assert.assertTrue(listener.event instanceof PooledConnectionCreatedEvent);
	}
	
	@Test
	public void testPooledConnectionDestroyedEvent() throws Exception {
		pool.borrowConnection(null);
		pool.destroy();
		Assert.assertTrue(listener.event instanceof PooledConnectionDestroyedEvent);
	}
	
	private static class TestEventListener implements EventListener {

		private Event event;

		@Override
		public void eventOccurred(Event event) {
			this.event = event;
		}
		
	}
	
}
