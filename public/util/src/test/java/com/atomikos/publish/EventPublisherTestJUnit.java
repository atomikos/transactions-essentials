/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.publish;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.event.Event;
import com.atomikos.icatch.event.EventListener;
import com.atomikos.icatch.event.transaction.TransactionCommittedEvent;

public class EventPublisherTestJUnit {
	
	private EventListener mock;
	private Event event;
	
	@Before
	public void setUp() throws Exception {
		event = new TransactionCommittedEvent("id");
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
		Mockito.verify(mock,Mockito.times(0)).eventOccurred((Event) Mockito.any());
	}
	
	@Test
	public void testPublishNotifiesListener() {
		EventPublisher.publish(event);
		Mockito.verify(mock,Mockito.times(1)).eventOccurred((Event) Mockito.any());
	}
	
	
	@Test
	public void testPublishNotifiesListenerWithSameEvent() {
		EventPublisher.publish(event);
		Mockito.verify(mock,Mockito.times(1)).eventOccurred(event);
	}

	
}
