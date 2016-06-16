package com.atomikos.recovery.tcc.rest;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.tcc.rest.MimeTypes;

public class DefaultTccTransport implements TccTransport {

	@Override
	public void put(String uri) throws HeurRollbackException {

		Client client = ClientBuilder.newClient();
		// see
		// https://jersey.java.net/apidocs/2.5.1/jersey/org/glassfish/jersey/client/ClientProperties.html#SUPPRESS_HTTP_COMPLIANCE_VALIDATION
		client.property(
				"jersey.config.client.suppressHttpComplianceValidation", true);
		WebTarget target = client.target(uri);
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

}
