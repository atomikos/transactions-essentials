/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.spring.rest;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.atomikos.remoting.support.ClientInterceptorTemplate;
import com.atomikos.remoting.support.HeaderNames;

/**
 * Filter (interceptor) that adds the current thread's JTA transaction context to 
 * outgoing REST calls, and makes sure that the response is added to the commit scope
 * of the active transaction.
 * 
 * This class is only needed for REST clients that embed the Atomikos JTA transaction manager,
 * and it automatically makes outgoing REST calls part of the transaction.
 */

public class TransactionAwareRestClientInterceptor implements
		ClientHttpRequestInterceptor {
	
	private ClientInterceptorTemplate template = new ClientInterceptorTemplate();
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body,
			ClientHttpRequestExecution execution) throws IOException {
		String propagation  = template.onOutgoingRequest();
		if (propagation!=null) {
			request.getHeaders().set(HeaderNames.PROPAGATION_HEADER_NAME, propagation);	
		}
		ClientHttpResponse response = execution.execute(request, body);
		String extent = response.getHeaders().getFirst(HeaderNames.EXTENT_HEADER_NAME);			
		if (extent !=null) {
			template.onIncomingResponse(extent);	
		}
		return response;
	}
}