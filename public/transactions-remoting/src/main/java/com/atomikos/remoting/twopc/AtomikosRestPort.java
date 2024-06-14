/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.twopc;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.imp.CoordinatorImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.PendingTransactionRecord;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.TxState;
import com.atomikos.remoting.support.HeaderNames;

 /**
  * A REST port for making the local JVM's invocations part of the transaction scope
  * of the calling client (if any). An instance needs to be running if you want to 
  * accept incoming transactional REST calls.
  */

@Path("/atomikos")
@Consumes(HeaderNames.MimeType.APPLICATION_VND_ATOMIKOS_JSON)
@Produces("text/plain")
public class AtomikosRestPort {
	
	public static final String REST_URL_PROPERTY_NAME = "com.atomikos.icatch.rest_port_url";

	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosRestPort.class);
	
	private static String atomikosRestPortUrl;
	
	/**
	 * Sets the URL for this port - either from jta.properties or as detected on first invocation of our interceptor.
	 * Needed because JAX-RS does not have an easy why of finding out on which URL we are bound.
	 * 
	 * @param url Can be null in which case this method does nothing. If the URL was already set then a second invocation will not have any effect.
	 */
	public static void setUrl(String url) {
		if (atomikosRestPortUrl == null && url != null) { //cf case 181280
			atomikosRestPortUrl = url;
			if (!atomikosRestPortUrl.endsWith("/")) 
				atomikosRestPortUrl = atomikosRestPortUrl + "/";
		} 
	}
	
	public static String getUrl() {
		return atomikosRestPortUrl;
	}
	
	public static String buildParticipantUrl(CompositeTransaction ct) throws SysException {
		assertRestPortUrlSet();
		return getUrl()+ct.getCompositeCoordinator().getRootId()+"/" + ct.getCompositeCoordinator().getCoordinatorId();
	}

	private static void assertRestPortUrlSet() {
		if (getUrl() == null) {
			throw new SysException("Please set property " + AtomikosRestPort.REST_URL_PROPERTY_NAME + " - see https://www.atomikos.com/Documentation/ConfiguringRemoting for details");
		}
	}

	private static RecoveryLog recoveryLog;
	
	public static void init(String url) {
	    recoveryLog = Configuration.getRecoveryLog();
	    setUrl(url);
	}
	
	private static String buildParticipantUrl(String root, String coordinatorId) {
		return atomikosRestPortUrl + root + "/" + coordinatorId;
	}
	
	@GET
	public String ping() {
		return "Hello from Atomikos!";
	}

	@GET
	@Path("{coordinatorId}")
	public String getOutcome(@PathParam("coordinatorId") String coordinatorId) {
	    TxState ret = TxState.TERMINATED;
	    PendingTransactionRecord record = null;
        try {
            record = recoveryLog.get(coordinatorId);
        } catch (LogReadException e) {
            LOGGER.logWarning("Unexpected log exception", e);
            throw409(e);
        }
	    if (record != null) {
	        ret = record.state;
	    }
	    return ret.toString();
	}
	
	@POST
	@Path("{rootId}/{coordinatorId}")
	public Response prepare(@PathParam("rootId") String rootId, @PathParam("coordinatorId") String coordinatorId, Map<String,Integer> cascadeList) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("prepare ( ... ) received for root " + rootId);
		}
		TransactionService service = Configuration.getTransactionService();
		String extentUri = buildParticipantUrl(rootId, coordinatorId);
		Integer count = cascadeList.get(extentUri);
		Participant part = service.getParticipant(rootId);
		if (part == null) {
			return Response.status(Status.NOT_FOUND).entity(rootId).build();
		}
		part.setGlobalSiblingCount(count);
		part.setCascadeList(cascadeList);
		int result = -1;
		try {
			result = part.prepare();
		} catch (RollbackException rb) {
			LOGGER.logWarning("Error in prepare for root " + rootId, rb);
			throw404();
		} catch (Exception e) {
			LOGGER.logWarning("Error in prepare for root " + rootId, e);
			throw409(e);
		}
		
		return Response.status(Status.CREATED).entity(result).build();
	}

	private void throw404() {
		Response response = Response.status(Status.NOT_FOUND)
				.entity("Transaction has timed out and was rolledback")
				.type(MediaType.TEXT_PLAIN).build();
		throw new WebApplicationException(response);
	}

	private void throw409(Exception e) {
		Response response = Response.status(Status.CONFLICT).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
		throw new WebApplicationException(response);
	}

	@PUT
	@Path("{rootId}/{coordinatorId}/{onePhase}")
	public Response commit(@PathParam("rootId") String rootId, @PathParam("coordinatorId") String coordinatorId, @PathParam("onePhase") boolean onePhase) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("commit() received for root " + rootId + " onePhase = " + onePhase);
		}
		TransactionService service = Configuration.getTransactionService();
		CoordinatorImp part = (CoordinatorImp) service.getParticipant(rootId);
		
		if (part != null) { 
		    if (!part.getState().isFinalState()) { //ignore remote commit retries that worked here the first time
		        if (!part.getState().transitionAllowedTo(TxState.COMMITTING)) {
		            if(!onePhase) {
		                LOGGER.logWarning("Commit no longer allowed for root " + rootId + " - probably due to heuristic rollback?");
		                return Response.status(Status.CONFLICT).entity(rootId).build(); 
		            }
		        } else {
		            try {
		                part.commit(onePhase);
		            } catch (RollbackException rb) {
		                LOGGER.logWarning("Error in commit for root " + rootId, rb);
		                throw404();
		            } catch (Exception e) {
		                LOGGER.logWarning("Error in commit for root " + rootId, e);
		                throw409(e);
		            }   
		        }
		    }
		} else { //abandoned in OLTP? => delegate to log and recovery
	
                if (!onePhase) {
                    try {
                        delegateToRecovery(coordinatorId, true);
                    } catch (LogException e) {
                        LOGGER.logWarning("Error in commit for root " + rootId, e);
                        throw409(e);
                    }
                } else {
                    Response response = Response.status(Status.CONFLICT).entity(rootId).type(MediaType.TEXT_PLAIN).build();
                    throw new WebApplicationException(response);
                }

		}
		
 		
		return Response.status(Status.NO_CONTENT).build();
	}

	private void delegateToRecovery(String coordinatorId, boolean commit) throws LogException {
        if (recoveryLog == null) {
            recoveryLog = Configuration.getRecoveryLog();
        }
        if (commit) {
            recoveryLog.recordAsCommitting(coordinatorId);
        } else {
            recoveryLog.forget(coordinatorId);
        }
    }

    @DELETE
	@Path("{rootId}/{coordinatorId}")
	public Response rollback(@PathParam("rootId") String rootId, @PathParam("coordinatorId") String coordinatorId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("rollback() received for root " + rootId);
		}
		TransactionService service = Configuration.getTransactionService();
		Participant part = service.getParticipant(rootId);
		if (part != null) { //null for remote retry of rollback that worked here the first time
			try {
				part.rollback();
			} catch (Exception e) {
				LOGGER.logWarning("Error in rollback for root " + rootId, e);
				throw409(e);
			}
		} else { //abandoned in OLTP? => delegate to log and recovery
	            try {
                    delegateToRecovery(coordinatorId, false);
                } catch (LogException e) {
                    LOGGER.logWarning("Error in rollback for root " + rootId, e);
                    throw409(e);
                }
		}

		return Response.status(Status.NO_CONTENT).build();
	}

}
