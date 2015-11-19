package com.atomikos.recovery.imp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.atomikos.icatch.TxState;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.ParticipantLogEntry;
import com.atomikos.util.VersionedFile;

public class CachedCoordinatorLogEntryRepositoryIntegrationTest {
	private static final Integer CHECKPOINT_INTERVAL = 5000;
	private static final int NB_OF_THREADS = 50;
	private static final int NUMBER_OF_ENTRIES_PER_THREAD = 500;
	CachedCoordinatorLogEntryRepository sut;
	private InMemoryCoordinatorLogEntryRepository inMemoryCoordinatorLogEntryRepository;
	private FileSystemCoordinatorLogEntryRepository backupCoordinatorLogEntryRepository;
	
	@Rule
	public TestName name= new TestName();

	String tid = "TID";
	@Before
	public void configure() throws IOException {
		cleanupRecursively();
		ConfigProperties configProperties = getConfigProperties();
		inMemoryCoordinatorLogEntryRepository = new InMemoryCoordinatorLogEntryRepository();
		inMemoryCoordinatorLogEntryRepository.init(configProperties);
		backupCoordinatorLogEntryRepository = new FileSystemCoordinatorLogEntryRepository();
		backupCoordinatorLogEntryRepository.init(configProperties);;
		sut = new CachedCoordinatorLogEntryRepository(inMemoryCoordinatorLogEntryRepository,backupCoordinatorLogEntryRepository);
				
		sut.init(configProperties);
		
	}

	private void cleanupRecursively() {
		File directory= new File(baseDir());
		if (directory.exists()) {
			for (File file : directory.listFiles()) {
				file.delete();
			}
		} else {
			directory.mkdirs();	
		}
	}
	
	private void putAll(CoordinatorLogEntry[] coordinatorLogEntries) {
		for (CoordinatorLogEntry coordinatorLogEntry : coordinatorLogEntries) {
			try {
				sut.put(coordinatorLogEntry.coordinatorId,coordinatorLogEntry);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LogWriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	@Test
	public void testCreateUniqueCommittingEntries() throws Exception {
		Runnable r = new Runnable() {
			public void run() {
				CoordinatorLogEntry[] coordinatorLogEntries = createUniqueCommittingEntries(NUMBER_OF_ENTRIES_PER_THREAD);
				putAll(coordinatorLogEntries);
			}
		};
		
		int numberOfLinesFoundInFile = perform(r);
		
		Assert.assertEquals(NB_OF_THREADS*NUMBER_OF_ENTRIES_PER_THREAD, numberOfLinesFoundInFile);
	}
	
	@Test
	public void testCreateUniqueTerminatedEntries() throws Exception {
		Runnable r = new Runnable() {
			public void run() {
				CoordinatorLogEntry[] coordinatorLogEntries = createUniqueTerminatedEntries(NUMBER_OF_ENTRIES_PER_THREAD);
				putAll(coordinatorLogEntries);
			}
		};
		
		int numberOfLinesFoundInFile = perform(r);
		
		Assert.assertTrue(CHECKPOINT_INTERVAL>=numberOfLinesFoundInFile);
	}

	private int perform(Runnable r) throws InterruptedException,
			FileNotFoundException, IOException {
		Thread[] threads = new Thread[NB_OF_THREADS];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(r);
			threads[i].start();
		}
		
		
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
		
		sut.close();
		VersionedFile file = new VersionedFile(baseDir()+"/", baseName(), ".log");
		FileInputStream in= file.openLastValidVersionForReading();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		int numberOfLinesFoundInFile=0;
		while((reader.readLine()) != null) {
			numberOfLinesFoundInFile++;
		}
		return numberOfLinesFoundInFile;
	}

	private CoordinatorLogEntry[] createUniqueCommittingEntries(int nb) {
		String threadName=Thread.currentThread().getName();
		CoordinatorLogEntry[] coordinatorLogEntries = new CoordinatorLogEntry[nb];
		for (int i = 0; i < coordinatorLogEntries.length; i++) {
			String coordinatorId = threadName+"_"+i;
			coordinatorLogEntries[i] = createCoordinatorLogEntryWithParticipantsInState(coordinatorId,TxState.COMMITTING, TxState.COMMITTING);
		}
		return coordinatorLogEntries;
	}
	
	private CoordinatorLogEntry[] createUniqueTerminatedEntries(int nb) {
		String threadName=Thread.currentThread().getName();
		CoordinatorLogEntry[] coordinatorLogEntries = new CoordinatorLogEntry[nb];
		for (int i = 0; i < coordinatorLogEntries.length; i++) {
			String coordinatorId = threadName+"_"+i;
				coordinatorLogEntries[i] = createCoordinatorLogEntryWithParticipantsInState(coordinatorId,TxState.TERMINATED, TxState.TERMINATED);
		}
		return coordinatorLogEntries;
	}
	private CoordinatorLogEntry createCoordinatorLogEntryWithParticipantsInState(String coordinatorId,
			TxState... states) {
		
		ParticipantLogEntry[] participantDetails = new ParticipantLogEntry[states.length];
		
		for (int i = 0; i < participantDetails.length; i++) {
			participantDetails[i] = new ParticipantLogEntry(coordinatorId, "uri"+i+System.nanoTime(), i,
					"description"+i+"-"+"-"+System.nanoTime(), states[i]);
		}
		return new CoordinatorLogEntry(coordinatorId, participantDetails);
	}
	
	
	private ConfigProperties getConfigProperties() {
		Properties properties = new Properties();
		properties.put("com.atomikos.icatch.checkpoint_interval", String.valueOf(CHECKPOINT_INTERVAL));
		properties.put("com.atomikos.icatch.log_base_dir", baseDir()+"/");
		properties.put("com.atomikos.icatch.log_base_name", baseName());
		ConfigProperties configProperties =new ConfigProperties(properties);
		return configProperties;
	}

	private String baseName() {
		return name.getMethodName();
	}

	private String baseDir() {
		//target/test-classes+"/"+ name of test
		return ClassLoader.getSystemClassLoader().getResource(".").getFile().replaceAll("%20", " ")+"/"+name.getMethodName();
	}

}
