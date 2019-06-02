/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
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
public class AtomikosRestPort {

	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosRestPort.class);

	private static RecoveryLog recoveryLog;
	
	public static void init() {
	    recoveryLog = Configuration.getRecoveryLog();
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
	
	@Context
	UriInfo uriInfo;
	@POST
	@Path("{rootId}/{coordinatorId}")
	public Response prepare(@PathParam("rootId") String rootId, @PathParam("coordinatorId") String coordinatorId, Map<String,Integer> cascadeList) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("prepare ( ... ) received for root " + rootId);
		}
		TransactionService service = Configuration.getTransactionService();
		
		Integer count = cascadeList.get(uriInfo.getRequestUri().toString());
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
		    try {
                delegateToRecovery(coordinatorId, true);
            } catch (LogException e) {
                LOGGER.logWarning("Error in commit for root " + rootId, e);
                throw409(e);
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
