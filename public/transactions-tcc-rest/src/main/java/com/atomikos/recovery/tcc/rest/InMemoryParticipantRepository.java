/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.tcc.rest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public enum InMemoryParticipantRepository {
	INSTANCE;
	
	private HashMap<String, Collection<String>> cache = new HashMap<String, Collection<String>>();
	
	private InMemoryParticipantRepository() {
		
	}

	public Collection<String> getParticipantLogEntries(String id) {
		return cache.get(id);
	}

	public void remove(String id) {
		cache.remove(id);
	}

	public void save(String id, Set<String> uris) {
		cache.put(id, uris);
	}

}
