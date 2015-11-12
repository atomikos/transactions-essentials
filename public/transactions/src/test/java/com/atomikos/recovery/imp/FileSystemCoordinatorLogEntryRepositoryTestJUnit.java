package com.atomikos.recovery.imp;

import java.util.Collection;
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

public class FileSystemCoordinatorLogEntryRepositoryTestJUnit {
	private static final String tid = "TID";
	@Rule
	public TestName name= new TestName();
	 
	CoordinatorLogEntry coordinatorLogEntry;
	ConfigProperties configProperties;
	FileSystemCoordinatorLogEntryRepository sut = new FileSystemCoordinatorLogEntryRepository();
	Collection<CoordinatorLogEntry> coordinatorLogEntries;
	@Before
	public void initFileSystemCoordinatorLogEntryRepository() throws Exception {
		Properties properties = new Properties();
		properties.put("com.atomikos.icatch.log_base_dir", ClassLoader.getSystemClassLoader().getResource(".").getFile());
		properties.put("com.atomikos.icatch.log_base_name", name.getMethodName());
		configProperties =new ConfigProperties(properties);
		sut.init(configProperties);
	}
	
	@Test
	public void testReadingNotExistentFileDoesNotThrow() throws Exception {
		Assert.assertTrue(sut.getAllCoordinatorLogEntries().isEmpty());
	}

	
	
	@Test
	public void testWrittenFileCanBeRead() throws Exception {
		givenCoordinatorLogEntryWrittenToFile();
		givenReInitedRepository();
		whenGetAllCoordinatorLogEntries();
		thenCoordinatorLogEntryWasReadFromFile();
	}

	
	
	private void whenGetAllCoordinatorLogEntries() {
		coordinatorLogEntries = sut.getAllCoordinatorLogEntries();
		
	}


	private void givenReInitedRepository() {
		sut.close();
		sut.init(configProperties);
		
	}


	private void givenCoordinatorLogEntryWrittenToFile() throws IllegalArgumentException, LogWriteException {
		givenCoordinatorLogEntry();
		sut.put(tid, coordinatorLogEntry);
	}


	private void thenCoordinatorLogEntryWasReadFromFile() {
		Assert.assertFalse(sut.getAllCoordinatorLogEntries().isEmpty());
		//TODO functional comparaison
		
	}


	


	private void givenCoordinatorLogEntry() {
			ParticipantLogEntry[] participantLogEntries = new ParticipantLogEntry[1];
			participantLogEntries[0] = new ParticipantLogEntry(tid, "participantUri", 0, "description", TxState.IN_DOUBT);
			coordinatorLogEntry = new CoordinatorLogEntry(tid, participantLogEntries);
	}


}
