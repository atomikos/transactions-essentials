/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.grpc;

import com.atomikos.remoting.support.ClientInterceptorTemplate;
import com.atomikos.remoting.support.HeaderNames;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.MethodDescriptor;
import io.grpc.Status;

public class TransactionAwareClientInterceptor implements ClientInterceptor {

	private ClientInterceptorTemplate template = new ClientInterceptorTemplate();
	
	
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> methodDescriptor, final CallOptions callOptions, final Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(final Listener<RespT> responseListener, final Metadata headers) {
            	String propagation = template.onOutgoingRequest();
        		if (propagation != null) {
        			headers.put(Key.of(HeaderNames.PROPAGATION_HEADER_NAME, Metadata.ASCII_STRING_MARSHALLER), propagation);	
        		}
                super.start(
                		new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                			@Override
                			public void onClose(Status status, Metadata trailers) {
                				String extent = trailers.get(Key.of(HeaderNames.EXTENT_HEADER_NAME, Metadata.ASCII_STRING_MARSHALLER));
                				template.onIncomingResponse(extent);
                				super.onClose(status, trailers);
                			}
						},
                		headers);
            }
            
            
        };
    }

}