/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.recovery.imp;

import com.atomikos.icatch.CoordinatorLogEntry;
import com.atomikos.icatch.ParticipantLogEntry;

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
		strBuilder.append(QUOTE).append("id").append(QUOTE).append(":").append(QUOTE).append(coordinatorLogEntry.id).append(QUOTE);
		strBuilder.append(PROPERTY_SEPARATOR);
		strBuilder.append(QUOTE).append("wasCommitted").append(QUOTE).append(":").append(coordinatorLogEntry.wasCommitted);
		strBuilder.append(PROPERTY_SEPARATOR);
		if (coordinatorLogEntry.superiorCoordinatorId!=null) {
			strBuilder.append(QUOTE).append("superiorCoordinatorId").append(QUOTE).append(":").append(coordinatorLogEntry.superiorCoordinatorId);
			strBuilder.append(PROPERTY_SEPARATOR);
		}
		String prefix = "";
		if(coordinatorLogEntry.participants.length>0){
			strBuilder.append(QUOTE).append("participants").append(QUOTE);
			strBuilder.append(":");
			strBuilder.append(START_ARRAY);
			for (ParticipantLogEntry participantLogEntry : coordinatorLogEntry.participants) {
				strBuilder.append(prefix);
				prefix = PROPERTY_SEPARATOR;
				strBuilder.append(START_OBJECT);
				strBuilder.append(QUOTE).append("uri").append(QUOTE).append(":").append(QUOTE).append(participantLogEntry.uri).append(QUOTE);
				strBuilder.append(PROPERTY_SEPARATOR);
				strBuilder.append(QUOTE).append("state").append(QUOTE).append(":").append(QUOTE).append(participantLogEntry.state).append(QUOTE);
				strBuilder.append(PROPERTY_SEPARATOR);
				strBuilder.append(QUOTE).append("expires").append(QUOTE).append(":").append(participantLogEntry.expires);
				if (participantLogEntry.resourceName!=null) {
					strBuilder.append(PROPERTY_SEPARATOR);
					strBuilder.append(QUOTE).append("resourceName").append(QUOTE).append(":").append(QUOTE).append(participantLogEntry.resourceName).append(QUOTE);	
				}
				strBuilder.append(END_OBJECT);
			}
			strBuilder.append(END_ARRAY);
		}
		strBuilder.append(END_OBJECT);
		strBuilder.append(LINE_SEPARATOR);
		return strBuilder.toString();
	}

}
