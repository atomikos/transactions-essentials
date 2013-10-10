package com.atomikos.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public class EventPublisherTestJUnit {
	
	private EventPublisher publisher;
	private EventListener mock;
	private Serializable event;
	
	@Before
	public void setUp() throws Exception {
		event = new Integer(0);
		publisher = new EventPublisher();
		mock = Mockito.mock(EventListener.class);
		publisher.registerEventListener(mock);
	}

	@Test
	public void testPublishNullEventDoesNotThrow() {
		publisher.publish(null);
	}

	@Test
	public void testPublishNullEventDoesNotNotifyListeners() {
		publisher.publish(null);
		Mockito.verify(mock,Mockito.times(0)).eventOccurred((Serializable) Mockito.any());
	}
	
	@Test
	public void testPublishNotifiesListener() {
		publisher.publish(event);
		Mockito.verify(mock,Mockito.times(1)).eventOccurred((Serializable) Mockito.any());
	}
	
	
	@Test
	public void testPublishNotifiesListenerWithSameEvent() {
		publisher.publish(event);
		Mockito.verify(mock,Mockito.times(1)).eventOccurred(event);
	}

	@Test
	public void testUnregisterEventListenerWorks() {
		publisher.unregisterEventListener(mock);
		publisher.publish(event);
		Mockito.verify(mock,Mockito.times(0)).eventOccurred((Serializable) Mockito.any());
	}
	
	public static class EventPublisher {
		
		private static Logger LOGGER = LoggerFactory.createLogger(EventPublisher.class);
		
		private Set<EventListener> listeners = new HashSet<EventListener>();
		
		public void publish(Serializable event) {
			if (event != null) {
				notifyAllListeners(event);
			}
		}

		public void unregisterEventListener(EventListener listener) {
			listeners.remove(listener);
		}

		private void notifyAllListeners(Serializable event) {
			for(EventListener listener : listeners) {				
				try {
					listener.eventOccurred(event);
				} catch (Exception e) {
					LOGGER.logWarning("Error notifying listener", e);
				}
			}
		}

		void registerEventListener(EventListener listener) {
			listeners.add(listener);
		}
	
	}
	
	public static interface EventListener {

		void eventOccurred(Serializable event);
		
	}
}
