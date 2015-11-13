package com.atomikos.recovery.imp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.ParticipantLogEntry;
import com.atomikos.util.VersionedFile;

public class FileSystemCoordinatorLogEntryRepository implements
		CoordinatorLogEntryRepository {

	private VersionedFile file;
	private FileChannel rwChannel =null;
	
	
	@Override
	public void init(ConfigProperties configProperties) {
		String baseDir = configProperties.getLogBaseDir();
		String baseName = configProperties.getLogBaseName();
		file = new VersionedFile(baseDir, baseName, ".log");
	
	}

	private Serializer serializer = new Serializer(); 
	int position = 0;
	@Override
	public void put(String id, CoordinatorLogEntry coordinatorLogEntry)
			throws IllegalArgumentException, LogWriteException {
		
		if (rwChannel==null) {
			rwChannel = initializeOutput();
		}
		
		
		write(coordinatorLogEntry);

	}
	private void write(CoordinatorLogEntry coordinatorLogEntry) {
		try {
			String str = serializer.toJSON(coordinatorLogEntry);
			byte[] buffer = str.getBytes();
			MappedByteBuffer wrBuf = rwChannel.map(FileChannel.MapMode.READ_WRITE, position, buffer.length );
			wrBuf.put(buffer);
			if(coordinatorLogEntry.shouldSync()) {
				wrBuf.force();
			}
			position+=buffer.length;
		
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private FileChannel initializeOutput() {
		FileChannel rwChannel =null;
		try {
			rwChannel=file.openNewVersionForNioWriting();
			position=0;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rwChannel;
	}

	@Override
	public void remove(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public CoordinatorLogEntry get(String coordinatorId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ParticipantLogEntry> findAllCommittingParticipants() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		try {
			rwChannel.close();
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	@Override
	public Collection<CoordinatorLogEntry> getAllCoordinatorLogEntries() {
		Map<String,CoordinatorLogEntry> coordinatorLogEntries = new HashMap<String,CoordinatorLogEntry>();
		// TODO Open File for reading
		//perform a checkpoint
		//read file
		try {
			String line;
			FileInputStream fis = file.openLastValidVersionForReading();
		    InputStreamReader isr = new InputStreamReader(fis);
		    BufferedReader br = new BufferedReader(isr);
		
		    while ((line = br.readLine()) != null) {
		    	CoordinatorLogEntry coordinatorLogEntry = deserialize(line);
		    	coordinatorLogEntries.put(coordinatorLogEntry.coordinatorId, coordinatorLogEntry);
		    }
		    br.close();
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return coordinatorLogEntries.values();
	}

	private	Deserializer deserializer = new Deserializer();
	private CoordinatorLogEntry deserialize(String line) {
		return deserializer.fromJSON(line);
	}
	
	protected void closeOutput() throws IllegalStateException {
		
		// try to close the previous output stream, if any.
		try {
			if (file != null) {
				file.close();
			}
		} catch (IOException e) {
	
			throw new IllegalStateException("Error closing previous output", e);
		}
		
	}

	@Override
	public void writeCheckpoint(
			Collection<CoordinatorLogEntry> checkpointContent) throws IllegalStateException, IOException {
		closeOutput();
		
		rwChannel = file.openNewVersionForNioWriting();
		for (CoordinatorLogEntry coordinatorLogEntry : checkpointContent) {
			write(coordinatorLogEntry);
		}
		file.discardBackupVersion();
		position=0;
	}

}
