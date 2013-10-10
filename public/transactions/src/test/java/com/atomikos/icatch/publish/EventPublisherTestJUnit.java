package com.atomikos.icatch.publish;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.event.EventListener;

public class EventPublisherTestJUnit {
	
	private EventListener mock;
	private Serializable event;
	
	@Before
	public void setUp() throws Exception {
		event = new Integer(0);
		mock = Mockito.mock(EventListener.class);
		EventPublisher.registerEventListener(mock);
	}

	@Test
	public void testPublishNullEventDoesNotThrow() {
		EventPublisher.publish(null);
	}

	@Test
	public void testPublishNullEventDoesNotNotifyListeners() {
		EventPublisher.publish(null);
		Mockito.verify(mock,Mockito.times(0)).eventOccurred((Serializable) Mockito.any());
	}
	
	@Test
	public void testPublishNotifiesListener() {
		EventPublisher.publish(event);
		Mockito.verify(mock,Mockito.times(1)).eventOccurred((Serializable) Mockito.any());
	}
	
	
	@Test
	public void testPublishNotifiesListenerWithSameEvent() {
		EventPublisher.publish(event);
		Mockito.verify(mock,Mockito.times(1)).eventOccurred(event);
	}

	
}
