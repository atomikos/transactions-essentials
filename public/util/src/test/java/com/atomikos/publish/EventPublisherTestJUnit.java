/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
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
import com.atomikos.icatch.event.transaction.ParticipantHeuristicEvent;

public class EventPublisherTestJUnit {
	
	private EventListener mock;
	private Event event;
	
	@Before
	public void setUp() throws Exception {
		event = new ParticipantHeuristicEvent("id", null, null);
		mock = Mockito.mock(EventListener.class);
		EventPublisher.INSTANCE.registerEventListener(mock);
	}

	@Test
	public void testPublishNullEventDoesNotThrow() {
		EventPublisher.INSTANCE.publish(null);
	}

	@Test
	public void testPublishNullEventDoesNotNotifyListeners() {
		EventPublisher.INSTANCE.publish(null);
		Mockito.verify(mock,Mockito.times(0)).eventOccurred((Event) Mockito.any());
	}
	
	@Test
	public void testPublishNotifiesListener() {
		EventPublisher.INSTANCE.publish(event);
		Mockito.verify(mock,Mockito.times(1)).eventOccurred((Event) Mockito.any());
	}
	
	
	@Test
	public void testPublishNotifiesListenerWithSameEvent() {
		EventPublisher.INSTANCE.publish(event);
		Mockito.verify(mock,Mockito.times(1)).eventOccurred(event);
	}

	
}
