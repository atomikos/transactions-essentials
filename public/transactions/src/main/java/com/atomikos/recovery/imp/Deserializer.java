package com.atomikos.recovery.imp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atomikos.icatch.TxState;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.ParticipantLogEntry;

public class Deserializer {

	private static final String JSON_ARRAY_END = "]";

	private static final String JSON_ARRAY_START = "[";

	
	List<String> tokenize(String content) {
		List<String> result = new ArrayList<String>();
		int endObject = content.indexOf("}");
		while(endObject >0){
			String object = content.substring(0,endObject+1);
			result.add(object);
			content = content.substring(endObject+1);
			endObject = content.indexOf("}");
		}
		return result;
	}

	String extractArrayPart(String content) {
		if(!content.contains(JSON_ARRAY_START) && !content.contains(JSON_ARRAY_END)) {
			//no array...
			return "";
		}
		//else
		int start=content.indexOf(JSON_ARRAY_START);
		int end=content.indexOf(JSON_ARRAY_END);
		
		return content.substring(start+1, end);
	}
	public CoordinatorLogEntry fromJSON(String coordinatorLogEntryStr) {
		String arrayContent = extractArrayPart(coordinatorLogEntryStr);
		List<String> elements = tokenize(arrayContent);
		ParticipantLogEntry[] participantLogEntries = new ParticipantLogEntry[elements.size()];
		
		for (int i = 0; i < participantLogEntries.length; i++) {
			participantLogEntries[i]=recreateParticipantLogEntry(elements.get(i));
		}
		
		Map<String,String> toto = new HashMap<String, String>(2);
		String[] attributes = coordinatorLogEntryStr.split(",");
		for (String attribute : attributes) {
			String[] pair = attribute.split(":");
			toto.put(pair[0].replace("\"", ""), pair[1].replace("\"", ""));
		}
		
		CoordinatorLogEntry actual = new CoordinatorLogEntry(toto.get("coordinatorId"),Boolean.valueOf(toto.get("wasCommitted")),  participantLogEntries);
		return actual;
	}
	
	ParticipantLogEntry recreateParticipantLogEntry(
			String participantLogEntry) {
		participantLogEntry = participantLogEntry.replaceAll("\\{", "").replaceAll("\\}", "");
		
		Map<String,String> toto = new HashMap<String, String>(5);
		String[] attributes = participantLogEntry.split(",");
		for (String attribute : attributes) {
			String[] pair = attribute.split(":");
			if(pair.length>1){
				toto.put(pair[0].replace("\"", ""), pair[1].replace("\"", ""));	
			}
			
		}
		
		ParticipantLogEntry actual = new ParticipantLogEntry(toto.get("coordinatorId"),
				toto.get("participantUri"), Long.valueOf(toto.get("expires")), toto.get("description"), TxState.valueOf(toto.get("state")));
		return actual;
	}
}
