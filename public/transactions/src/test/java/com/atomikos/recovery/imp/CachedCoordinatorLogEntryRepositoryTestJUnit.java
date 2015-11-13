package com.atomikos.recovery.imp;

import java.util.Collections;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.TxState;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.LogWriteException;
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
		doInit();
	}

	private void doInit() {
		Properties properties = new Properties();
		properties.put("com.atomikos.icatch.checkpoint_interval", "1");
		ConfigProperties configProperties =new ConfigProperties(properties);		
		sut.init(configProperties);
		inMemoryCoordinatorLogEntryRepository.init(configProperties);
		
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
	public void testSuccessfulGetClearsStaleCache() throws Exception {
		testFailingPutImpliesGetOnBackupRepositoryOnNextGet();
		Mockito.reset(backupCoordinatorLogEntryRepository);
		thenGetDoesNotAccessBackup();	
	}
	
	
	@Test
	public void testPutUpdatesBothRepositories() throws Exception {
		givenExistingCoordinatorLogEntry();
		sut.put(tid, coordinatorLogEntry);
		thenPutWasCalledOnBothCoordinatorLogEntryRepositories();
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
	
	@Test
	public void testSucceedingSecondPutOnBackupImpliesNoGetOnBackupRepositoryOnNextGet() throws Exception {
		givenExistingCoordinatorLogEntry();
		whenPutFailsOnBackup();
		whenPutSucceedsOnBackup();
		thenGetDoesNotAccessBackup();
	}

	@Test
	public void testIfStaleIsEmptyThenRetrievalDoesNotAccessBackup() throws Exception {
		givenExistingCoordinatorLogEntry();
		sut.put(tid, coordinatorLogEntry);
		thenFindCommittigParticipantsDoesNotAccessBackup();
	}

	
	@Test
	public void testCommittingParticipantRefreshesInMemoryCache() throws Exception {
		givenExistingCoordinatorLogEntry();
		whenPutFailsOnBackup();
		thenFindCommittingParticipantsRefreshesInMemoryCache();
	}
	
	@Test
	public void testInitPopulatesCacheFromBackup() throws Exception {
		givenExistingCoordinatorLogEntryInBackup();
		whenInit();
		thenCacheWasPopulatedFromBackup();
	}
	
	@Test
	public void testClose() throws Exception {
		givenExistingCoordinatorLogEntry();
		sut.put(tid, coordinatorLogEntry);

		whenClose();
		thenCloseInvokedOnBackup();
		thenCloseInvokedOnInMemory();
	}
	
	@Test
	public void testWriteCheckpointOnBackup() throws Exception {
		
		givenExistingCoordinatorLogEntry();
		whenPutSucceedsOnBackup();
		
		givenExistingCoordinatorLogEntry();
		whenPutSucceedsOnBackup();
		
		thenCheckpointWasTriggeredOnBackup();
	}

	private void thenCheckpointWasTriggeredOnBackup() throws LogWriteException {
		Mockito.verify(backupCoordinatorLogEntryRepository,Mockito.times(1)).writeCheckpoint(Mockito.anyCollection());
	}
	
	@Test
	public void testFailingPutOnBackupTriggersWriteCheckpoint() throws Exception{
		givenExistingCoordinatorLogEntry();
		whenPutFailsOnBackup();
		thenCheckpointWasTriggeredOnBackup();
	}

	
	
	private void thenCloseInvokedOnInMemory() {
		Assert.assertTrue(inMemoryCoordinatorLogEntryRepository.isClosed());
	}

	private void thenCloseInvokedOnBackup() {
		Mockito.verify(backupCoordinatorLogEntryRepository).close();
		
		
	}

	private void whenClose() {
		sut.close();
	}

	


	private void thenCacheWasPopulatedFromBackup() {
		Assert.assertEquals(coordinatorLogEntry, inMemoryCoordinatorLogEntryRepository.get(tid));
	}

	private void whenInit() {
		sut.close();
		doInit();
	}

	private void givenExistingCoordinatorLogEntryInBackup() {
		givenExistingCoordinatorLogEntry();
		Mockito.when(backupCoordinatorLogEntryRepository.getAllCoordinatorLogEntries()).thenReturn(Collections.singleton(coordinatorLogEntry));
	}
	
	private void thenFindCommittingParticipantsRefreshesInMemoryCache() {
		sut.findAllCommittingParticipants();
		Mockito.verify(backupCoordinatorLogEntryRepository, Mockito.never()).findAllCommittingParticipants();
	}

	private void thenFindCommittigParticipantsDoesNotAccessBackup() {
		sut.findAllCommittingParticipants();
		Mockito.verify(backupCoordinatorLogEntryRepository, Mockito.never()).findAllCommittingParticipants();
		
	}

	

	private void whenPutSucceedsOnBackup() throws IllegalArgumentException, LogWriteException {
        Mockito.reset(backupCoordinatorLogEntryRepository);
        sut.put(tid, coordinatorLogEntry);
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

	
	private void thenPutWasCalledOnBothCoordinatorLogEntryRepositories() throws Exception {
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
