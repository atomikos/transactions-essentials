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
