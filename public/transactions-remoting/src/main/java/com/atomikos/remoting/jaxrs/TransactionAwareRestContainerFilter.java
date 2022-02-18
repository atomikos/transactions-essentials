/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.jaxrs;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriInfo;

import com.atomikos.icatch.RollbackException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.support.ContainerInterceptorTemplate;
import com.atomikos.remoting.support.HeaderNames;
import com.atomikos.remoting.twopc.AtomikosRestPort;

/**
 * Filter (interceptor) that adds starts a local JTA transaction that becomes
 * part of the remote client's transaction context.
 * 
 * This class is only needed for REST services that embed the Atomikos JTA
 * transaction manager, and it automatically makes incoming REST calls part of
 * the caller's transaction.
 * 
 * Note: this interceptor only works if:
 * <ul>
 * <li>the local transaction service (Atomikos) is running, AND</li>
 * <li>a AtomikosRestPort instance is available for this JVM</li>
 * </ul>
 */

public class TransactionAwareRestContainerFilter implements ContainerRequestFilter, ContainerResponseFilter {

	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionAwareRestContainerFilter.class);

	private static final int UNPROCESSABLE_ENTITY = 422;

	private ContainerInterceptorTemplate template = new ContainerInterceptorTemplate();

	@Context
	UriInfo info;
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		assertAtomikosRestPortUrlSet();
		String propagation = requestContext.getHeaderString(HeaderNames.PROPAGATION_HEADER_NAME);
			try {
				template.onIncomingRequest(propagation);
			} catch (IllegalArgumentException e) {
				LOGGER.logWarning("Detected invalid incoming transaction - aborting...");
				requestContext.abortWith(Response.status(UNPROCESSABLE_ENTITY).build());
			}
	}

	private void assertAtomikosRestPortUrlSet() {
		if (AtomikosRestPort.getUrl() == null ) {
			// Not set in jta.properties => try to guess here...
			AtomikosRestPort.setUrl(info.getBaseUriBuilder().path(AtomikosRestPort.class).build().toString());
			LOGGER.logWarning("Init property " + AtomikosRestPort.REST_URL_PROPERTY_NAME + " not set, guessing it. See https://www.atomikos.com/Documentation/ConfiguringRemoting for the implications...");
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		String extent = terminateImportedTransaction(responseContext);
		if (extent !=null) {
			responseContext.getHeaders().add(HeaderNames.EXTENT_HEADER_NAME, extent);
		}
	}

	private String terminateImportedTransaction(ContainerResponseContext responseContext) throws IOException {
		String extent = null;
		boolean error = false;
		try {
			
			if (responseContext.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
				error = true;
			} 
			extent = template.onOutgoingResponse(error);
		} catch (RollbackException e) {
			throw new IOException(e);
		} catch (Exception e) {
			String msg = "Unexpected error while terminating transaction";
			LOGGER.logError(msg, e);
			throw new IOException(msg, e);
		}
		return extent;
	}

}
