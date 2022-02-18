/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.jaxrs;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import com.atomikos.remoting.support.ClientInterceptorTemplate;
import com.atomikos.remoting.support.HeaderNames;

/**
 * Filter (interceptor) that adds the current thread's JTA transaction context
 * to outgoing REST calls, and makes sure that the response is added to the
 * commit scope of the active transaction.
 * 
 * This class is only needed for REST clients that embed the Atomikos JTA
 * transaction manager, and it automatically makes outgoing REST calls part of
 * the transaction.
 */

public class TransactionAwareRestClientFilter implements ClientRequestFilter, ClientResponseFilter {

	private ClientInterceptorTemplate template = new ClientInterceptorTemplate();

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		String propagation = template.onOutgoingRequest();
		if (propagation != null) {
			requestContext.getHeaders().add(HeaderNames.PROPAGATION_HEADER_NAME, propagation);
		}
	}

	@Override
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		String extent = responseContext.getHeaderString(HeaderNames.EXTENT_HEADER_NAME);
		template.onIncomingResponse(extent);

	}

}
