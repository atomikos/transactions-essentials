package com.atomikos.recovery.imp;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.atomikos.icatch.TxState;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.ParticipantLogEntry;

public class CachedCoordinatorLogEntryRepositoryIntegrationTest {
	CachedCoordinatorLogEntryRepository sut;
	private InMemoryCoordinatorLogEntryRepository inMemoryCoordinatorLogEntryRepository;
	private CoordinatorLogEntryRepository backupCoordinatorLogEntryRepository;
	
	@Rule
	public TestName name= new TestName();

	String tid = "TID";
	@Before
	public void configure() {
		ConfigProperties configProperties = getConfigProperties();
		inMemoryCoordinatorLogEntryRepository = new InMemoryCoordinatorLogEntryRepository();
		inMemoryCoordinatorLogEntryRepository.init(configProperties);
		backupCoordinatorLogEntryRepository = new FileSystemCoordinatorLogEntryRepository();
		backupCoordinatorLogEntryRepository.init(configProperties);;
		sut = new CachedCoordinatorLogEntryRepository(inMemoryCoordinatorLogEntryRepository,backupCoordinatorLogEntryRepository);
				
		sut.init(configProperties);
		
	
	}
	
	@Test
	public void testMutliThreaded() throws Exception {
		long start = System.currentTimeMillis();
		
		Thread[] threads = new Thread[50];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(
					
					
			new Runnable() {
				public void run() {
					
					CoordinatorLogEntry[] coordinatorLogEntries = create(500);
					
					for (CoordinatorLogEntry coordinatorLogEntry : coordinatorLogEntries) {
						try {
							sut.put(coordinatorLogEntry.coordinatorId,
									coordinatorLogEntry);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (LogWriteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			threads[i].start();
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
		
		System.out.println("Tps "+(System.currentTimeMillis()-start));
		sut.close();
	}

	private CoordinatorLogEntry[] create(int nb) {
		CoordinatorLogEntry[] coordinatorLogEntries = new CoordinatorLogEntry[nb];
		for (int i = 0; i < coordinatorLogEntries.length; i++) {
			coordinatorLogEntries[i] = createCoordinatorLogEntryWithParticipantsInState(i,
					TxState.HEUR_MIXED, TxState.COMMITTING);
		}
		return coordinatorLogEntries;
	}
	private CoordinatorLogEntry createCoordinatorLogEntryWithParticipantsInState(int k,
			TxState... states) {
		String name=Thread.currentThread().getName();
		ParticipantLogEntry[] participantDetails = new ParticipantLogEntry[states.length];
		for (int i = 0; i < participantDetails.length; i++) {
			participantDetails[i] = new ParticipantLogEntry(tid+name, "uri"+i+k+System.nanoTime(), i,
					"description"+i+"-"+k+"-"+System.nanoTime(), states[i]);
		}
		return new CoordinatorLogEntry(tid+name, participantDetails);
	}
	
	
	private ConfigProperties getConfigProperties() {
		Properties properties = new Properties();
		properties.put("com.atomikos.icatch.checkpoint_interval", "50000");
		properties.put("com.atomikos.icatch.log_base_dir", ClassLoader.getSystemClassLoader().getResource(".").getFile().replaceAll("%20", " "));
		properties.put("com.atomikos.icatch.log_base_name", name.getMethodName());
		ConfigProperties configProperties =new ConfigProperties(properties);
		return configProperties;
	}

}
