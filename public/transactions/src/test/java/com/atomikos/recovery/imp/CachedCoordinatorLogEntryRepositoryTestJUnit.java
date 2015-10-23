package com.atomikos.recovery.imp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.TxState;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.ParticipantLogEntry;

public class CachedCoordinatorLogEntryRepositoryTestJUnit {
	
	String tid="TID";
	private InMemoryCoordinatorLogEntryRepository inMemoryCoordinatorLogEntryRepository = new InMemoryCoordinatorLogEntryRepository();
	private CoordinatorLogEntryRepository backupCoordinatorLogEntryRepository = Mockito.mock(CoordinatorLogEntryRepository.class);
	CachedCoordinatorLogEntryRepository sut;
	CoordinatorLogEntry coordinatorLogEntry;
	@Before
	public void configure() {
		sut = new CachedCoordinatorLogEntryRepository(inMemoryCoordinatorLogEntryRepository,backupCoordinatorLogEntryRepository);
	}
	
	@Test
	public void testGetReturnsCachedEntry() throws Exception {
		givenExistingCoordinatorLogEntry();
		sut.put(tid, coordinatorLogEntry);
		thenGetAccessesCache();
	}
	
	

	@Test
	public void testGetUsesCache() throws Exception {
		givenExistingCoordinatorLogEntry();
		sut.put(tid, coordinatorLogEntry);
		thenGetDoesNotAccessBackup();
	}
	@Test
	public void testPutUpdatesBothRepositories() throws Exception {
		givenExistingCoordinatorLogEntry();
		sut.put(tid, coordinatorLogEntry);
		thenPutUpdatesBothCoordinatorLogEntryRepositories();
	}
	
	@Test
	public void testFailingPutOnBackupDoesNotUpdateInMemoryRepository() throws Exception {
		givenExistingCoordinatorLogEntry();
		whenPutFailsOnBackup();
		thenInMemoryRepositoryWasNotUpdated();
	}
	
	@Test
	public void testFailingPutImpliesGetOnBackupRepositoryOnNextGet() throws Exception {
		givenExistingCoordinatorLogEntry();
		whenPutFailsOnBackup();
		thenGetAccessesBackup();
	}
	
	private void thenGetAccessesBackup() {
		sut.get(tid);
		Mockito.verify(backupCoordinatorLogEntryRepository,Mockito.times(1)).get(Mockito.anyString());
		
	}

	private void thenGetAccessesCache() {
		CoordinatorLogEntry retrieved = sut.get(tid);
		Assert.assertEquals(coordinatorLogEntry,retrieved);
	}
	
	private void thenGetDoesNotAccessBackup() {
		sut.get(tid);
		Mockito.verify(backupCoordinatorLogEntryRepository,Mockito.never()).get(Mockito.anyString());
	}

	private void givenExistingCoordinatorLogEntry() {
		ParticipantLogEntry[] participantLogEntries = new ParticipantLogEntry[1];
		participantLogEntries[0] = new ParticipantLogEntry(tid, "participantUri", 0, "description", TxState.IN_DOUBT);
		coordinatorLogEntry = new CoordinatorLogEntry(tid, participantLogEntries);
		
	}


	
	
	
	private void thenPutUpdatesBothCoordinatorLogEntryRepositories() throws Exception {
		CoordinatorLogEntry retrieved = sut.get(tid);
		Assert.assertEquals(coordinatorLogEntry,retrieved);
		Mockito.verify(backupCoordinatorLogEntryRepository,Mockito.times(1)).put(Mockito.eq(tid), Mockito.any(CoordinatorLogEntry.class));
		
	}

	

	private void thenInMemoryRepositoryWasNotUpdated() {
		CoordinatorLogEntry retrieved = sut.get(tid);
		Assert.assertNull(retrieved);
	}

	private void whenPutFailsOnBackup() throws Exception {
		Mockito.doThrow(new RuntimeException()).when(backupCoordinatorLogEntryRepository).put(tid, coordinatorLogEntry);
		
		try {
			sut.put(tid, coordinatorLogEntry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
