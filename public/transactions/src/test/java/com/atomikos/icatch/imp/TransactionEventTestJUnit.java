/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

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
import com.atomikos.publish.EventPublisher;

public class TransactionEventTestJUnit {

	private static final String ID = "id";
	private CoordinatorImp coordinator;
	private EventListener eventListenerMock;

	private CoordinatorImp createCoordinator() {
		return new CoordinatorImp ( ID,  ID , null ,  false , 1000 , false , true );
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
		coordinator.addParticipant(createHeuristicCommitParticipant(new HeurMixedException()));
		coordinator.addParticipant(createHeuristicCommitParticipant(new HeurMixedException()));
		coordinator.prepare();
		coordinator.commit(false);
		Mockito.verify(eventListenerMock, Mockito.times(1)).eventOccurred(Mockito.any(TransactionHeuristicEvent.class));
	}
	
	@Test
	public void testHeuristicTransactionEventWithHeurHazard() throws Exception {
		coordinator = createCoordinator();
		EventPublisher.registerEventListener(eventListenerMock);
		coordinator.addParticipant(createHeuristicCommitParticipant(new HeurHazardException()));
		coordinator.addParticipant(createHeuristicCommitParticipant(new HeurHazardException()));
		coordinator.prepare();
		coordinator.commit(false);
		Mockito.verify(eventListenerMock, Mockito.times(1)).eventOccurred(Mockito.any(TransactionHeuristicEvent.class));
	}
	
	@Test
	public void testHeuristicTransactionEventWithHeurAborted() throws Exception {
		coordinator = createCoordinator();
		EventPublisher.registerEventListener(eventListenerMock);
		coordinator.addParticipant(createHeuristicCommitParticipant(new HeurRollbackException()));
		coordinator.addParticipant(createHeuristicCommitParticipant(new HeurRollbackException()));
		coordinator.prepare();
		coordinator.commit(false);
		Mockito.verify(eventListenerMock, Mockito.times(1)).eventOccurred(Mockito.any(TransactionHeuristicEvent.class));
	}

	
	@Test
	public void testHeuristicTransactionEventWithHeurCommitted() throws Exception {
		coordinator = createCoordinator();
		EventPublisher.registerEventListener(eventListenerMock);
		coordinator.addParticipant(createHeuristicRollbackParticipant(new HeurCommitException()));
		coordinator.addParticipant(createHeuristicRollbackParticipant(new HeurCommitException()));
		coordinator.prepare();
		coordinator.rollback();
		Mockito.verify(eventListenerMock, Mockito.times(1)).eventOccurred(Mockito.any(TransactionHeuristicEvent.class));
	}
	
	private Participant createHeuristicCommitParticipant(
			Exception heurException) throws Exception {
		Participant ret = Mockito.mock(Participant.class);
		Mockito.doThrow(heurException).when(ret).commit(false);
		return ret;
	}
	
	private Participant createHeuristicRollbackParticipant(
			Exception heurException) throws Exception {
		Participant ret = Mockito.mock(Participant.class);
		Mockito.doThrow(heurException).when(ret).rollback();
		return ret;
	}
	
}
