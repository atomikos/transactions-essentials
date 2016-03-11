/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.tcc.rest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.DatatypeConverter;

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
		Calendar cal = DatatypeConverter.parseDateTime(pl.getExpires());
		this.expires = cal.getTimeInMillis();
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
			LOGGER.logTrace("Error invoking cancel on participant", ignore);
		}
	}

	@Override
	public void put() throws HeurRollbackException {
		Participant p = getParticipant();
		try {
			p.confirm();
		} catch (WebApplicationException e) {
			if (e.getResponse().getStatus() == 404) {
				LOGGER.logError("Heuristic cancel by participant " + uri );
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
			LOGGER.logTrace("Error retrieving options", e);
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
