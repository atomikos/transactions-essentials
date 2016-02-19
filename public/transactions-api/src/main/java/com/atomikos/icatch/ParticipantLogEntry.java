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

package com.atomikos.icatch;


public class ParticipantLogEntry {

	/**
	 * The ID of the global transaction as known by the transaction core.
	 */
	
	public final String coordinatorId;
	
	/**
	 * Identifies the participant within the global transaction.
	 */
	
	public final String uri;

	/**
	 * When does this participant expire (expressed in millis since Jan 1, 1970)? 
	 */
	
	public final long expires;
	
	/**
	 * Best-known state of the participant.
	 */
	public final TxState state;
	
	/**
	 * For diagnostic purposes, null if not relevant.
	 */
	public final String resourceName;
	
	public ParticipantLogEntry(String coordinatorId, String uri, 
			long expires, String resourceName, TxState state) {
		this.coordinatorId = coordinatorId;
		this.uri = uri;
		this.expires = expires;
		this.resourceName = resourceName;
		this.state = state;
	}
	
	
	
	@Override
	public boolean equals(Object other) {
		boolean ret = false;
		if (other instanceof ParticipantLogEntry) {
			ParticipantLogEntry o = (ParticipantLogEntry) other;
			if (o.coordinatorId.equals(coordinatorId) && o.uri.equals(uri)) ret = true;
		}
		return ret;
	}
	
	@Override
	public int hashCode() {
		return coordinatorId.hashCode();
	}



	@Override
	public String toString() {
		return "ParticipantLogEntry [id=" + coordinatorId
				+ ", uri=" + uri + ", expires=" + expires
				+ ", state=" + state + ", resourceName=" + resourceName + "]";
	}


}
