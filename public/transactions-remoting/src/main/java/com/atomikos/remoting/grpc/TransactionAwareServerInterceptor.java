/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.grpc;

import java.io.IOException;

import com.atomikos.icatch.RollbackException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.support.ContainerInterceptorTemplate;
import com.atomikos.remoting.support.HeaderNames;

import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

public class TransactionAwareServerInterceptor implements ServerInterceptor {

	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionAwareServerInterceptor.class);
	private ContainerInterceptorTemplate template = new ContainerInterceptorTemplate();

	public <ReqT, RespT> Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, final Metadata metadata,
			final ServerCallHandler<ReqT, RespT> next) {

		String propagation = metadata.get(Key.of(HeaderNames.PROPAGATION_HEADER_NAME, Metadata.ASCII_STRING_MARSHALLER));
		try {
			template.onIncomingRequest(propagation);
		} catch (IllegalArgumentException e) {
			LOGGER.logWarning("Detected invalid incoming transaction - aborting...");
			call.close(Status.FAILED_PRECONDITION, metadata);
		}

		return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
			@Override
			public void close(Status status, Metadata responseMetadata) {
				try {
					String extent = terminateImportedTransaction(status);
					if (extent != null) {
						Key<String> extentHeaderName = Key.of(HeaderNames.EXTENT_HEADER_NAME, Metadata.ASCII_STRING_MARSHALLER);
						metadata.put(extentHeaderName, extent);
						responseMetadata.merge(metadata);
					}
				} catch (Exception e) {
		            String msg = "Unexpected error while terminating transaction";
		            LOGGER.logError(msg, e);
				}

				super.close(status, responseMetadata);
			}
		}, metadata);

	}
    

	private String terminateImportedTransaction(Status status) throws IOException {
		String extent = null;
		boolean error = false;
		try {
			if (!status.isOk()) {
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