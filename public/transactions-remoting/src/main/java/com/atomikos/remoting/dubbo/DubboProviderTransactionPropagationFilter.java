/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.dubbo;

import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

import com.atomikos.icatch.RollbackException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.support.ContainerInterceptorTemplate;
import com.atomikos.remoting.support.HeaderNames;

@Activate(group = PROVIDER, order = 100)
public class DubboProviderTransactionPropagationFilter implements Filter, Filter.Listener {

    private final Logger LOGGER = LoggerFactory.createLogger(DubboProviderTransactionPropagationFilter.class);

    private ContainerInterceptorTemplate template = new ContainerInterceptorTemplate();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        LOGGER.logTrace("Filtering incoming request...");

        try {
            String propagation = RpcContext.getContext().getAttachment(HeaderNames.PROPAGATION_HEADER_NAME);
            if ( LOGGER.isTraceEnabled() ) {
                LOGGER.logTrace("propagation: " + propagation);
            }
            template.onIncomingRequest(propagation);
        } catch (IllegalArgumentException e) {
            LOGGER.logWarning("Detected invalid incoming transaction - aborting...");
            throw new RpcException(e);
        }
        return invoker.invoke(invocation);
    }

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        String extent = terminateImportedTransaction(appResponse);
        appResponse.setAttachment(HeaderNames.EXTENT_HEADER_NAME, extent);
    }

    @Override
    public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {
        LOGGER.logError("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + t.getClass().getName() + ": " + t.getMessage(), t);
    }

    private String terminateImportedTransaction(Result result) {
        String extent = null;
        try {
            if (result.hasException()) {
                extent = template.onOutgoingResponse(true);
            } else {
                extent = template.onOutgoingResponse(false);
            }
        } catch (RollbackException e) {
            String msg = "Transaction was rolled back - probably due to a timeout?";
            LOGGER.logWarning(msg, e);
        } catch (Exception e) {
            String msg = "Unexpected error while terminating transaction";
            LOGGER.logError(msg, e);
        }
        return extent;
    }
}
