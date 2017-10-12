/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.tcc.rest;

import java.util.Calendar;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.tcc.rest.ParticipantLink;

public class ParticipantAdapterImp implements ParticipantAdapter {
	
	private static final long serialVersionUID = 1L;

	private static Logger LOGGER = LoggerFactory.createLogger(ParticipantAdapterImp.class);
	
	public static WebTarget createJaxRsClientForUri(String uri) {
		Client client = ClientBuilder.newClient();
		//see https://jersey.java.net/apidocs/2.5.1/jersey/org/glassfish/jersey/client/ClientProperties.html#SUPPRESS_HTTP_COMPLIANCE_VALIDATION
		client.property("jersey.config.client.suppressHttpComplianceValidation", true);
		return client.target(uri);
	}

	
	public static void callConfirmOnJaxrsClient(WebTarget target)
			throws HeurRollbackException {
		try {
			Response r = target
					.request(MimeTypes.MIME_TYPE_PARTICIPANT_V1)
					.header("Content-type", MimeTypes.MIME_TYPE_PARTICIPANT_V1)
					.method(HttpMethod.PUT);

			if (r.getStatus() == 404) {
				throw new HeurRollbackException();
			}

		} catch (WebApplicationException e) {
			if (e.getResponse().getStatus() == 404) {
				throw new HeurRollbackException();
			} else {
				throw e;
			}
		}
	}
	
	
	private transient WebTarget part;
	private String uri;
	private long expires;

	public ParticipantAdapterImp(ParticipantLink pl) {
		this.uri = pl.getUri();
		Calendar cal = DatatypeConverter.parseDateTime(pl.getExpires());
		this.expires = cal.getTimeInMillis();
	}
	
	WebTarget getParticipant() {
		if (part == null) {
			part = createJaxRsClientForUri(uri);
		}
		return part;
	}


	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public void delete() {
		WebTarget p = getParticipant();
		try {
			p.request(MimeTypes.MIME_TYPE_PARTICIPANT_V1).header("Content-type", MimeTypes.MIME_TYPE_PARTICIPANT_V1).delete();
		} catch (Exception ignore) {
			LOGGER.logTrace("Error invoking cancel on participant", ignore);
		}
	}

	@Override
	public void put() throws HeurRollbackException {
		WebTarget p = getParticipant();
		callConfirmOnJaxrsClient(p);
	}

	@Override
	public void options() {
		WebTarget p = getParticipant();
		try {
			p.request().options();
		} catch (Exception e) {
			LOGGER.logTrace("Error retrieving options", e);
		}
	}

	@Override
	public long getExpires() {
		return expires;
	}

}
