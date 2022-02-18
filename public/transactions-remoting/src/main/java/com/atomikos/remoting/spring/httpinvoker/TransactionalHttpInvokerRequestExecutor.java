/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.spring.httpinvoker;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.support.ClientInterceptorTemplate;
import com.atomikos.remoting.support.HeaderNames;

public class TransactionalHttpInvokerRequestExecutor extends SimpleHttpInvokerRequestExecutor {

	private final Logger LOGGER = LoggerFactory.createLogger(TransactionalHttpInvokerRequestExecutor.class);

	
	private ClientInterceptorTemplate template = new ClientInterceptorTemplate();

	@Override
	protected void prepareConnection(HttpURLConnection con, int contentLength)
			throws IOException {
		LOGGER.logTrace("Filtering request...");
		String propagation = template.onOutgoingRequest();
		con.setRequestProperty(HeaderNames.PROPAGATION_HEADER_NAME, propagation);
		super.prepareConnection(con, contentLength);
	}

	@Override
	protected void validateResponse(HttpInvokerClientConfiguration config,
			HttpURLConnection con) throws IOException {
		super.validateResponse(config, con);
		LOGGER.logTrace("Filtering response...");
		String extent = con.getHeaderField(HeaderNames.EXTENT_HEADER_NAME);
		template.onIncomingResponse(extent);
	}


}
