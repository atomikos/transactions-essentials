/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.twopc;

import static javax.ws.rs.client.ClientBuilder.newClient;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.support.HeaderNames;

/**
 * A utility adapter class for easily adding REST participants to the core
 * transaction engine.
 */

public class ParticipantAdapter implements Participant {

	private static final Logger LOGGER = LoggerFactory.createLogger(ParticipantAdapter.class);

	private static Client client;
	
	private final URI uri; 

	private final Map<String, Integer> cascadeList = new HashMap<>();

	public ParticipantAdapter(URI uri) {
		if (client == null) {
			String className = Configuration.getConfigProperties().getRestClientBuilder();
			try {
				Class<?> builderClass = Thread.currentThread().getContextClassLoader().loadClass(className);
				RestClientBuilder restClientBuilder = (RestClientBuilder)builderClass.newInstance();
				client = restClientBuilder.build();
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
		this.uri = uri;
	}

	@Override
	public String getURI() {
		return uri.toASCIIString();
	}

	@Override
	public void setCascadeList(Map<String, Integer> allParticipants) throws SysException {
		this.cascadeList.putAll(allParticipants);
	}

	@Override
	public void setGlobalSiblingCount(int count) {
		this.cascadeList.put(getURI(), count);
	}

	@Override
	public int prepare() throws RollbackException, HeurHazardException, HeurMixedException, SysException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("Calling prepare on " + getURI());
		}
		try {
			int result = client.target(uri).request()
					.buildPost(Entity.entity(cascadeList, HeaderNames.MimeType.APPLICATION_VND_ATOMIKOS_JSON))
					.invoke(Integer.class);
			if (LOGGER.isTraceEnabled()) {
				LOGGER.logTrace("Prepare returned " + result);
			}
			return result;
		} catch (WebApplicationException e) {
			int status = e.getResponse().getStatus();
			if (status == 404) {
				// 404 writes a String entity - we have to consume it
				consumeStringEntity(e.getResponse());
				LOGGER.logWarning("Remote participant not available - any remote work will rollback...", e);
				throw new RollbackException();
			} else {
				if (status == 409) {
					// 409 writes a String entity - we have to consume it
					consumeStringEntity(e.getResponse());
					e.getResponse().close();
				}
				LOGGER.logWarning("Unexpected error during prepare - see stacktrace for more details...", e);
				throw new HeurHazardException();
			}
		}
	}

	@Override
	public void commit(boolean onePhase)
			throws HeurRollbackException, HeurHazardException, HeurMixedException, RollbackException, SysException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("Calling commit on " + getURI());
		}

		Response r = client.target(uri).path(String.valueOf(onePhase)).request().buildPut(Entity.entity("", HeaderNames.MimeType.APPLICATION_VND_ATOMIKOS_JSON)).invoke();

		if (r.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			int status = r.getStatus();
			switch (status) {
			case 404:
				// 404 writes a String entity - we have to consume it
				consumeStringEntity(r);
				if (onePhase) {
					LOGGER.logWarning("Remote participant not available - default outcome will be rollback");
					throw new RollbackException();
				}
			case 409:
				// 409 writes a String entity - we have to consume it
				consumeStringEntity(r);
				LOGGER.logWarning("Unexpected 409 error on commit");
				throw new HeurMixedException();
			default:
				LOGGER.logWarning("Unexpected error on commit: " + status);
				throw new HeurHazardException();
			}
		}

	}

	@Override
	public void rollback() throws HeurCommitException, HeurMixedException, HeurHazardException, SysException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("Calling rollback on " + getURI());
		}

		Response r = client.target(uri).request().header(HttpHeaders.CONTENT_TYPE, HeaderNames.MimeType.APPLICATION_VND_ATOMIKOS_JSON).delete();

		if (r.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			int status = r.getStatus();
			switch (status) {
			case 409:
				// 409 writes a String entity - we have to consume it
				consumeStringEntity(r);
				LOGGER.logWarning("Unexpected 409 error on rollback");
				throw new HeurMixedException();
			case 404:
				LOGGER.logDebug("Unexpected 404 error on rollback - ignoring...");
				break;
			default:
				LOGGER.logWarning("Unexpected error on rollback: " + status);
				throw new HeurHazardException();
			}
		}

	}

	@Override
	public void forget() {
	}

	@Override
	public String getResourceName() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		boolean ret = false;
		if (o instanceof ParticipantAdapter) {
			ParticipantAdapter other = (ParticipantAdapter) o;
			ret = getURI().equals(other.getURI());
		}
		return ret;
	}

	@Override
	public int hashCode() {
		return getURI().hashCode();
	}
	
	@Override
	public String toString() {
		return "ParticipantAdapter for: " + getURI();
	}
	
	private void consumeStringEntity(Response r) {
		// the entity body has to be consumed to allow pooling of http connections.
		// see https://stackoverflow.com/questions/27063667/httpclient-4-3-blocking-on-connection-pool
		try {
			r.readEntity(String.class);
		} catch (Exception e) {
			// catch exception. we only want to be sure that all content was cosumed
		}
	}

}
