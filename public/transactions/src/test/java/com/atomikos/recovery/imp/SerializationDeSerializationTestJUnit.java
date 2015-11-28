package com.atomikos.recovery.imp;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.atomikos.icatch.TxState;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.ParticipantLogEntry;

public class SerializationDeSerializationTestJUnit {

	String tid = "TID";

	Serializer serializer = new Serializer();
	
	Deserializer deserializer = new Deserializer();
	@Test
	public void testDeserializedObjectContainsSameValuesAsSerializedObject() throws Exception {
		givenCoordinatorLogEntry();
		whenSerializeDeserialize();
		thenContentAreTheSame();
	}
	private void thenContentAreTheSame() {
		Assert.assertTrue(EqualsBuilder.reflectionEquals(coordinatorLogEntry, deserialized));
		
	}
	private void whenSerializeDeserialize() {
		deserialized = deserializer.fromJSON(serializer.toJSON(coordinatorLogEntry));
		
	}
	CoordinatorLogEntry coordinatorLogEntry;
	
	CoordinatorLogEntry deserialized;
	

	private void givenCoordinatorLogEntry() {
		coordinatorLogEntry = createCoordinatorLogEntryWithParticipantsInState(TxState.COMMITTING,TxState.COMMITTING);
		
	}



	private CoordinatorLogEntry createCoordinatorLogEntryWithParticipantsInState(TxState... states) {
		ParticipantLogEntry[] participantDetails = new ParticipantLogEntry[states.length];
		for (int i = 0; i < participantDetails.length; i++) {
			participantDetails[i] = new ParticipantLogEntry(tid, "uri"+i+System.nanoTime(), i,
					"description"+i+"-"+"-"+System.nanoTime(), states[i]);
		}
		return new CoordinatorLogEntry(tid, participantDetails);
	}
}
