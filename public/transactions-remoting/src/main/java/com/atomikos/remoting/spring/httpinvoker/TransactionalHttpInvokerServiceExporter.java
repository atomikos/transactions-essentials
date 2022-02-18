/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.spring.httpinvoker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import com.atomikos.icatch.RollbackException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.support.ContainerInterceptorTemplate;
import com.atomikos.remoting.support.HeaderNames;

public class TransactionalHttpInvokerServiceExporter extends HttpInvokerServiceExporter {

	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionalHttpInvokerServiceExporter.class);

	private ContainerInterceptorTemplate template = new ContainerInterceptorTemplate();

	@Override
	protected InputStream decorateInputStream(HttpServletRequest request, InputStream is) throws IOException {
		LOGGER.logTrace("Filtering incoming request...");

		try {
			String propagation = request.getHeader(HeaderNames.PROPAGATION_HEADER_NAME);
			template.onIncomingRequest(propagation);
		} catch (IllegalArgumentException e) {
			LOGGER.logWarning("Detected invalid incoming transaction - aborting...");
			throw new IOException(e);
		}

		return super.decorateInputStream(request, is);
	}

	@Override
	protected OutputStream decorateOutputStream(HttpServletRequest request, HttpServletResponse response,
			OutputStream os) throws IOException {
			try {
				String extent = null;
				if (response.getStatus() >= 200 && response.getStatus() < 300) {
					extent = template.onOutgoingResponse(false);
				} else {
					extent = template.onOutgoingResponse(true);
				}

				if (extent != null) {
					response.addHeader(HeaderNames.EXTENT_HEADER_NAME, extent);
				}

			} catch (RollbackException e) {
				String msg = "Transaction was rolled back - probably due to a timeout?";
				LOGGER.logWarning(msg, e);
				throw new IOException(msg, e);
			} catch (Exception e) {
				LOGGER.logError("Unexpected error while terminating transaction", e);
				throw e;
			}

		
		return super.decorateOutputStream(request, response, os);
	}

	

}
