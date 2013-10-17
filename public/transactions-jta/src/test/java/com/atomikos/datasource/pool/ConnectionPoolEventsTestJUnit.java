package com.atomikos.datasource.pool;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.datasource.pool.event.ConnectionPoolExhaustedEvent;
import com.atomikos.icatch.event.EventListener;
import com.atomikos.icatch.publish.EventPublisher;

public class ConnectionPoolEventsTestJUnit {

	private ConnectionPool pool;
	private EventListener listener;
	
	@Before
	public void setUp() throws Exception {
		ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
		XPooledConnection xpc = Mockito.mock(XPooledConnection.class);
		Mockito.when(cf.createPooledConnection()).thenReturn(xpc);
		ConnectionPoolProperties cpp = Mockito.mock(ConnectionPoolProperties.class);
		Mockito.when(cpp.getMaxPoolSize()).thenReturn(0);
		pool = new ConnectionPool(cf, cpp);
		listener = Mockito.mock(EventListener.class);
		EventPublisher.registerEventListener(listener);
	}
	
	@After
	public void tearDown() throws Exception {
		if (pool != null) pool.destroy();
	}

	@Test
	public void testConnectionPoolExhaustedEvent() throws Exception {
		try {
			pool.borrowConnection(null);
		} catch (PoolExhaustedException ok) {}
		Mockito.verify(listener, Mockito.times(1)).eventOccurred(Mockito.any(ConnectionPoolExhaustedEvent.class));
	}

}
