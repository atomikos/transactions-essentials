package com.atomikos.recovery.imp;

import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.ParticipantLogEntry;

public class Serializer {

	private static final String PROPERTY_SEPARATOR = ",";
	private static final String QUOTE = "\"";
	private static final String END_ARRAY = "]";
	private static final String START_ARRAY = "[";
	private static final String START_OBJECT = "{";
	private static final String END_OBJECT = "}";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public String toJSON(CoordinatorLogEntry coordinatorLogEntry) {
		StringBuilder strBuilder = new StringBuilder(600);
		strBuilder.append(START_OBJECT);
		strBuilder.append(QUOTE).append("coordinatorId").append(QUOTE).append(":").append(QUOTE).append(coordinatorLogEntry.coordinatorId).append(QUOTE);
		strBuilder.append(PROPERTY_SEPARATOR);
		strBuilder.append(QUOTE).append("wasCommitted").append(QUOTE).append(":").append(coordinatorLogEntry.wasCommitted);
		strBuilder.append(PROPERTY_SEPARATOR);
		String prefix = "";
		if(coordinatorLogEntry.participantDetails.length>0){
			strBuilder.append(QUOTE).append("participantDetails").append(QUOTE);
			strBuilder.append(":");
			strBuilder.append(START_ARRAY);
			for (ParticipantLogEntry participantLogEntry : coordinatorLogEntry.participantDetails) {
				strBuilder.append(prefix);
				prefix = PROPERTY_SEPARATOR;
				strBuilder.append(START_OBJECT);
				strBuilder.append(QUOTE).append("participantUri").append(QUOTE).append(":").append(QUOTE).append(participantLogEntry.participantUri).append(QUOTE);
				strBuilder.append(PROPERTY_SEPARATOR);
				strBuilder.append(QUOTE).append("state").append(QUOTE).append(":").append(QUOTE).append(participantLogEntry.state).append(QUOTE);
				strBuilder.append(PROPERTY_SEPARATOR);
				strBuilder.append(QUOTE).append("expires").append(QUOTE).append(":").append(participantLogEntry.expires);
				strBuilder.append(PROPERTY_SEPARATOR);
				strBuilder.append(QUOTE).append("description").append(QUOTE).append(":").append(QUOTE).append(participantLogEntry.description).append(QUOTE);
				strBuilder.append(END_OBJECT);
			}
			strBuilder.append(END_ARRAY);
		}
		strBuilder.append(END_OBJECT);
		strBuilder.append(LINE_SEPARATOR);
		return strBuilder.toString();
	}

}
