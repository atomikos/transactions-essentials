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

package com.atomikos.icatch.tcc.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.provider.JSONProvider;

import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.tcc.rest.ParticipantLink;

class ParticipantAdapterImp implements ParticipantAdapter {
	
	private static final long serialVersionUID = 1L;

	private static Logger LOGGER = LoggerFactory.createLogger(ParticipantAdapterImp.class);
	
	private transient Participant part;
	private String uri;
	private long expires;

	public ParticipantAdapterImp(ParticipantLink pl) {
		this.uri = pl.getUri();
		this.expires = pl.getExpires().toGregorianCalendar().getTime().getTime();
	}
	
	Participant getParticipant() {
		if (part == null) {
			JSONProvider prov = new JSONProvider();
			prov.setIgnoreNamespaces(true);
	        List<JSONProvider> providers = new ArrayList<JSONProvider>();
	        providers.add(prov);
			part = JAXRSClientFactory.create(uri, Participant.class, providers);
			WebClient.client(part).accept(MimeTypes.MIME_TYPE_PARTICIPANT_V1).type(MimeTypes.MIME_TYPE_PARTICIPANT_V1);
		}
		return part;
	}

	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public void delete() {
		Participant p = getParticipant();
		try {
			p.cancel();
		} catch (Exception ignore) {
			LOGGER.logDebug("Error invoking cancel on participant", ignore);
		}
	}

	@Override
	public void put() throws HeurRollbackException {
		Participant p = getParticipant();
		try {
			p.confirm();
		} catch (WebApplicationException e) {
			if (e.getResponse().getStatus() == 404) {
				throw new HeurRollbackException();
			} else {
				throw e;
			}
		} 
	}

	@Override
	public void options() {
		WebClient client = getRawParticipant();
		try {
			client.options();
		} catch (Exception e) {
			LOGGER.logDebug("Error retrieving options", e);
		}
	}

	private WebClient getRawParticipant() {

		WebClient client = WebClient.create(uri);
		return client;
	}

	@Override
	public long getExpires() {
		return expires;
	}

}
