package com.atomikos.icatch.imp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.event.EventListener;
import com.atomikos.icatch.event.TransactionAbortedEvent;
import com.atomikos.icatch.event.TransactionCommittedEvent;
import com.atomikos.icatch.event.TransactionCreatedEvent;
import com.atomikos.icatch.event.TransactionReadOnlyEvent;
import com.atomikos.icatch.publish.EventPublisher;

public class TransactionEventTestJUnit {

	private static final String ID = "id";
	private CoordinatorImp coordinator;
	private EventListener eventListenerMock;

	private CoordinatorImp createCoordinator() {
		return new CoordinatorImp ( ID , null ,  false , 1000 , false , true );
	}
	
	@Before
	public void setUp() throws Exception {	
		eventListenerMock = Mockito.mock(EventListener.class);
	}

	@Test
	public void testTansactionCreatedEvent() throws Exception {
		EventPublisher.registerEventListener(eventListenerMock);
		coordinator = createCoordinator();
		Mockito.verify(eventListenerMock, Mockito.times(1)).eventOccurred(Mockito.any(TransactionCreatedEvent.class));
		coordinator.rollback();
	}
	
	@Test
	public void testTransactionCommittedEvent() throws Exception {
		coordinator = createCoordinator();
		EventPublisher.registerEventListener(eventListenerMock);
		coordinator.commit(true);
		Mockito.verify(eventListenerMock, Mockito.times(1)).eventOccurred(Mockito.any(TransactionCommittedEvent.class));
	}

	@Test
	public void testTransactionAbortedEvent() throws Exception {
		coordinator = createCoordinator();
		EventPublisher.registerEventListener(eventListenerMock);
		coordinator.rollback();
		Mockito.verify(eventListenerMock, Mockito.times(1)).eventOccurred(Mockito.any(TransactionAbortedEvent.class));
	}
	
	@Test
	public void testTransactionReadOnlyEvent() throws Exception {
		coordinator = createCoordinator();
		EventPublisher.registerEventListener(eventListenerMock);
		coordinator.addParticipant(new ReadOnlyParticipant());
		coordinator.addParticipant(new ReadOnlyParticipant());
		coordinator.prepare();
		Mockito.verify(eventListenerMock, Mockito.times(1)).eventOccurred(Mockito.any(TransactionReadOnlyEvent.class));
	}
	
}
