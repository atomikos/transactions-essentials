package com.atomikos.recovery.imp;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import com.atomikos.icatch.TxState;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.ParticipantLogEntry;

public class DeserializeTestJUnit {
	Deserializer sut = new Deserializer();

	//@formatter:off
	private String record="{"
							+ "\"coordinatorId\":\"TID\"," 
							+ "\"wasCommitted\":true,"    
						    + "\"participantDetails\":"
						    + "["
						    	+ "{"
						    		+  "\"participantUri\":\"uri\","
						    		+ "\"expires\":0,"
						    		+ "\"state\":\"HEUR_MIXED\","
						    		+ "\"description\":\"description\""
						    	+ "}"
						    	+ ","
						    	+ "{"
						    		+ "\"participantUri\":\"uri\","
						    		+ "\"expires\":0,"
						    		+ "\"state\":\"COMMITTING\","
						    		+ "\"description\":\"description\""
						    	+ "}"
						    + "]"
						   + "}";
	
	private String withSuperiorCoordinatorId="{"
			+ "\"coordinatorId\":\"TID\"," 
			+ "\"wasCommitted\":true," 
			+ "\"superiorCoordinatorId\":SUPERIOR," 
		    + "\"participantDetails\":"
		    + "["
		    	+ "{"
		    		+  "\"participantUri\":\"uri\","
		    		+ "\"expires\":0,"
		    		+ "\"state\":\"HEUR_MIXED\","
		    		+ "\"description\":\"description\""
		    	+ "}"
		    	+ ","
		    	+ "{"
		    		+ "\"participantUri\":\"uri\","
		    		+ "\"expires\":0,"
		    		+ "\"state\":\"COMMITTING\","
		    		+ "\"description\":\"description\""
		    	+ "}"
		    + "]"
		   + "}";
	
	@Test
	public void recreateCoordinatorLogEntry() throws Exception {
		
		ParticipantLogEntry[] participantLogEntries = new ParticipantLogEntry[2];
		participantLogEntries[0] = new ParticipantLogEntry("TID","uri",0,"description",TxState.HEUR_MIXED);
		participantLogEntries[1] = new ParticipantLogEntry("TID","uri",0,"description",TxState.COMMITTING);
		CoordinatorLogEntry expected = new CoordinatorLogEntry("TID",true, participantLogEntries);
		
		CoordinatorLogEntry actual = sut.fromJSON(record);
		Assert.assertTrue(EqualsBuilder.reflectionEquals(expected,actual));
	}
	
	@Test
	public void recreateCoordinatorLogEntryWithSuperiorCoordinatorId() throws Exception {
		
		ParticipantLogEntry[] participantLogEntries = new ParticipantLogEntry[2];
		participantLogEntries[0] = new ParticipantLogEntry("TID","uri",0,"description",TxState.HEUR_MIXED);
		participantLogEntries[1] = new ParticipantLogEntry("TID","uri",0,"description",TxState.COMMITTING);
		CoordinatorLogEntry expected = new CoordinatorLogEntry("TID",true, participantLogEntries,"SUPERIOR");
		
		CoordinatorLogEntry actual = sut.fromJSON(withSuperiorCoordinatorId);
		Assert.assertTrue(EqualsBuilder.reflectionEquals(expected,actual));
	}
		
	@Test
	public void extractArrayFromRecord() throws Exception {
	 String expected="{"
				+ "\"participantUri\":\"uri\","
				+ "\"expires\":0,"
				+ "\"state\":\"HEUR_MIXED\","
				+ "\"description\":\"description\""
			+ "},"
			+ "{"
				+ "\"participantUri\":\"uri\","
				+ "\"expires\":0,"
				+ "\"state\":\"COMMITTING\","
				+ "\"description\":\"description\""
			+ "}";
		String actual = sut.extractArrayPart(record);
		assertEquals(expected, actual);
	}
	
	@Test
	public void findObjectInArray() throws Exception {
		String arrayContent = sut.extractArrayPart(record);
		List<String> elements = sut.tokenize(arrayContent);
		assertEquals(2, elements.size());
	}
	
	@Test
	public void recreateParticipantLogEntry() throws Exception {
		ParticipantLogEntry expected = new ParticipantLogEntry("TID","uri",0,"description",TxState.HEUR_MIXED); 
		
		String participantLogEntry ="{"
				+ "\"coordinatorId\":\"TID\","
				+ "\"participantUri\":\"uri\","
				+ "\"expires\":0,"
				+ "\"state\":\"HEUR_MIXED\","
				+ "\"description\":\"description\""
				+ "}";
		
		ParticipantLogEntry actual = sut.recreateParticipantLogEntry("TID",participantLogEntry);

		Assert.assertThat(expected, new ReflectionEquals(actual));
	}


	
	
	

	@Ignore
	@Test
	public void perfTest() throws Exception {
		List<String> temp = new ArrayList<String>();
		String line;
		    InputStream fis = new FileInputStream("json.txt");
		    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		    BufferedReader br = new BufferedReader(isr);
		
		    while ((line = br.readLine()) != null) {
		    	temp.add(line);
		    }
		    br.close();
		
		    System.out.println(temp.size());
		    long start = System.currentTimeMillis();
		    List<CoordinatorLogEntry> coordinatorLogEntries = new ArrayList<CoordinatorLogEntry>(temp.size());
		    for (String string : temp) {
		    	coordinatorLogEntries.add(sut.fromJSON(string));
			}
		    
		    Assert.assertEquals(10000, coordinatorLogEntries.size());
		    
		    System.out.println("Time to deserialize "+(System.currentTimeMillis()-start));
		
	}
	

	
	
	
	
	

}
