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

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.tcc.rest.Coordinator;
import com.atomikos.tcc.rest.ParticipantLink;
import com.atomikos.tcc.rest.Transaction;

@Consumes({"application/tcc+json"})
@Path("/coordinator")
public class CoordinatorImp implements Coordinator {
	
	private static Logger LOGGER = LoggerFactory.createLogger(CoordinatorImp.class);
	private CompositeTransactionManager ctm;
	
	public CoordinatorImp() {
	}
	
	
	private CompositeTransactionManager getCompositeTransactionManager() {
		if (Configuration.getCompositeTransactionManager() == null) {
			LOGGER.logWarning("Transaction core not yet initialized - initializing now, but should be done before!");
			Configuration.init();
		}
		if (ctm == null) {
			ctm = Configuration.getCompositeTransactionManager();
		}
		return ctm;
	}
	
	private CompositeTransaction createTaasTransaction(Transaction transaction) {
		CompositeTransaction ct = null;
		try {
			ct = convertToCompositeTransaction(transaction);
		} catch (Exception e) {
			LOGGER.logWarning("Unexpected error while creating transaction", e);
			throw new WebApplicationException(500);
		}
		return ct;
	}

	private CompositeTransaction convertToCompositeTransaction(Transaction transaction) {
		long timeout = deriveTimeout(transaction);
		CompositeTransaction ct = getCompositeTransactionManager().createCompositeTransaction(timeout);
		for (ParticipantLink pl : transaction.getParticipantLinks()) {
			ParticipantAdapterImp pa = new ParticipantAdapterImp(pl);
			TccParticipant p = new TccParticipant(pa);
			ct.addParticipant(p);
		}
		return ct;
	}

	private long deriveTimeout(Transaction transaction) {
		long timeout = 10000L;
		long now = System.currentTimeMillis();
		for (ParticipantLink pl : transaction.getParticipantLinks()) {
			long expires = pl.getExpires().toGregorianCalendar().getTime().getTime();
			long participantTimeout = expires - now;
			if (participantTimeout > timeout) {
				timeout = participantTimeout;
			}
		}
		return timeout;
	}

	@Override
	@PUT
	@Path("/confirm")
	public void confirm(Transaction transaction) {
		validateRequestIntegrity(transaction);
		CompositeTransaction taasTransaction = createTaasTransaction(transaction);
		commitTransaction(taasTransaction);
	}

	private void validateRequestIntegrity(Transaction transaction) {
		if (transaction == null) failWithInvalidRequest("transaction must not be null");
		for (ParticipantLink pl : transaction.getParticipantLinks()) {
			validateParticipantLink(pl);
		}
	}

	private void validateParticipantLink(ParticipantLink pl) {
		if (pl.getExpires() == null) failWithInvalidRequest("each participantLink must have an 'expires'");
		if (pl.getUri() == null) failWithInvalidRequest("each participantLink must have a value for 'uri'");
	}
	
	private void failWithInvalidRequest(String message) {	
		Response response =	Response.status(400).entity(message).type(MediaType.TEXT_PLAIN).build();
		throw new WebApplicationException(response);
	}
	
	
	private void commitTransaction(CompositeTransaction taasTransaction) {
		try {
			taasTransaction.commit();
		} catch (SecurityException e) {
			// no permission means timeout / cancel everywhere
			throwCancelledException();
		} catch (RollbackException e) {
			throwCancelledException();
		} catch (HeurRollbackException e) {
			throwCancelledException();
		} catch (Exception e) {
			LOGGER.logWarning("Unexpected error during confirm", e);
			Response response =	Response.status(409).entity("partial confirmation - check each participant for details").type(MediaType.TEXT_PLAIN).build();
			throw new WebApplicationException(response);
		}
	}


	private void throwCancelledException() {
		Response response =	Response.status(404).entity("transaction has timed out and was cancelled").type(MediaType.TEXT_PLAIN).build();
		throw new WebApplicationException(response);
	}

	@Override
	@PUT
	@Path("/cancel")
	public void cancel(Transaction transaction) {
		validateRequestIntegrity(transaction);
		CompositeTransaction taasTransaction = createTaasTransaction(transaction);
		rollbackTransaction(taasTransaction);
	}

	private void rollbackTransaction(CompositeTransaction taasTransaction) {
		try {
			taasTransaction.rollback();
		} catch (Exception e) {
			// ignore but log
			LOGGER.logWarning("Unexpected error during rollback", e);
		}
	}
	
}
