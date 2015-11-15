package com.atomikos.recovery.imp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.junit.Ignore;
import org.junit.Test;

import com.atomikos.icatch.TxState;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.ParticipantLogEntry;

public class SerializeTestJUnit {

	private static final int NB_ITER = 10000;
	String tid = "TID";

	Serializer serializer = new Serializer();
	
	@Test
	public void serializeUsingJson() throws Exception {
		CoordinatorLogEntry[] coordinatorLogEntries = create();
		RandomAccessFile raf = new RandomAccessFile("textfile.txt", "rw");
		FileChannel rwChannel = raf.getChannel();
		long start = System.currentTimeMillis();
		
		for (int i = 0; i < coordinatorLogEntries.length; i++) {
			
			String str = serializer.toJSON(coordinatorLogEntries[i]);
			byte[] buffer = str.getBytes();
			
			ByteBuffer buff = ByteBuffer.allocateDirect(buffer.length);
			 
			buff.put(buffer);
			buff.rewind();
			rwChannel.write(buff);
			if (i  % 2 == 0 ) {
				rwChannel.force(false);
			}
		}
		
		System.out.println("serializeUsingJson : " + (System.currentTimeMillis() - start));
		System.out.println("serializeUsingJson size : " + rwChannel.size());
		rwChannel.close();
		raf.close();
	}


	

	private CoordinatorLogEntry[] create() {
		CoordinatorLogEntry[] coordinatorLogEntries = new CoordinatorLogEntry[NB_ITER];
		for (int i = 0; i < coordinatorLogEntries.length; i++) {
			coordinatorLogEntries[i] = createCoordinatorLogEntryWithParticipantsInState(i,
					TxState.HEUR_MIXED, TxState.COMMITTING);
		}
		return coordinatorLogEntries;
	}

	private CoordinatorLogEntry createCoordinatorLogEntryWithParticipantsInState(int k,
			TxState... states) {
		ParticipantLogEntry[] participantDetails = new ParticipantLogEntry[states.length];
		for (int i = 0; i < participantDetails.length; i++) {
			participantDetails[i] = new ParticipantLogEntry(tid+k, "uri"+i+k+System.nanoTime(), i,
					"description"+i+"-"+k+"-"+System.nanoTime(), states[i]);
		}
		return new CoordinatorLogEntry(tid+k, participantDetails);
	}
	

	@Ignore
	@Test
	public void serializeUsingJava() throws Exception {
		File file = new File("binary.ser");
		
		CoordinatorLogEntry[] coordinatorLogEntries = create();
		FileOutputStream fichier = new FileOutputStream(file);
		long start = System.currentTimeMillis();
		ObjectOutputStream oos = new ObjectOutputStream(fichier);
		for (int i = 0; i < coordinatorLogEntries.length; i++) {
			oos.writeObject(coordinatorLogEntries[i]);
			oos.flush();
			if (i  % 2 ==0 ){
				fichier.getFD().sync();
			}
		}
		oos.close();
		fichier.close();
		System.out.println("serializeUsingJava : " + (System.currentTimeMillis() - start));
		System.out.println("serializeUsingJava size : " + file.length());
	}
}
