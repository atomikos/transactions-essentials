package com.atomikos.icatch.imp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.event.EventListener;
import com.atomikos.icatch.event.transaction.TransactionAbortedEvent;
import com.atomikos.icatch.event.transaction.TransactionCommittedEvent;
import com.atomikos.icatch.event.transaction.TransactionCreatedEvent;
import com.atomikos.icatch.event.transaction.TransactionHeuristicEvent;
import com.atomikos.icatch.event.transaction.TransactionReadOnlyEvent;
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
	
	@Test
	public void testHeuristicTransactionEventWithHeurMixed() throws Exception {
		coordinator = createCoordinator();
		EventPublisher.registerEventListener(eventListenerMock);
		coordinator.addParticipant(createHeuristicCommitParticipant(new HeurMixedException(null)));
		coordinator.addParticipant(createHeuristicCommitParticipant(new HeurMixedException(null)));
		coordinator.prepare();
		coordinator.commit(false);
		Mockito.verify(eventListenerMock, Mockito.times(1)).eventOccurred(Mockito.any(TransactionHeuristicEvent.class));
	}
	
	@Test
	public void testHeuristicTransactionEventWithHeurHazard() throws Exception {
		coordinator = createCoordinator();
		EventPublisher.registerEventListener(eventListenerMock);
		coordinator.addParticipant(createHeuristicCommitParticipant(new HeurHazardException(null)));
		coordinator.addParticipant(createHeuristicCommitParticipant(new HeurHazardException(null)));
		coordinator.prepare();
		coordinator.commit(false);
		Mockito.verify(eventListenerMock, Mockito.times(1)).eventOccurred(Mockito.any(TransactionHeuristicEvent.class));
	}
	
	@Test
	public void testHeuristicTransactionEventWithHeurAborted() throws Exception {
		coordinator = createCoordinator();
		EventPublisher.registerEventListener(eventListenerMock);
		coordinator.addParticipant(createHeuristicCommitParticipant(new HeurRollbackException(null)));
		coordinator.addParticipant(createHeuristicCommitParticipant(new HeurRollbackException(null)));
		coordinator.prepare();
		coordinator.commit(false);
		Mockito.verify(eventListenerMock, Mockito.times(1)).eventOccurred(Mockito.any(TransactionHeuristicEvent.class));
	}

	
	@Test
	public void testHeuristicTransactionEventWithHeurCommitted() throws Exception {
		coordinator = createCoordinator();
		EventPublisher.registerEventListener(eventListenerMock);
		coordinator.addParticipant(createHeuristicRollbackParticipant(new HeurCommitException(null)));
		coordinator.addParticipant(createHeuristicRollbackParticipant(new HeurCommitException(null)));
		coordinator.prepare();
		coordinator.rollback();
		Mockito.verify(eventListenerMock, Mockito.times(1)).eventOccurred(Mockito.any(TransactionHeuristicEvent.class));
	}
	
	private Participant createHeuristicCommitParticipant(
			Exception heurException) throws Exception {
		Participant ret = Mockito.mock(Participant.class);
		Mockito.when(ret.commit(false)).thenThrow(heurException);
		return ret;
	}
	
	private Participant createHeuristicRollbackParticipant(
			Exception heurException) throws Exception {
		Participant ret = Mockito.mock(Participant.class);
		Mockito.when(ret.rollback()).thenThrow(heurException);
		return ret;
	}
	
}
