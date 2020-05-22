package com.atomikos.remoting.dubbo;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.support.ClientInterceptorTemplate;
import com.atomikos.remoting.support.HeaderNames;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;

@Activate(group = CONSUMER, order = 100)
public class DubboConsumerTransactionPropagationFilter implements Filter, Filter.Listener {

    private final Logger LOGGER = LoggerFactory.createLogger(DubboConsumerTransactionPropagationFilter.class);

    private ClientInterceptorTemplate template = new ClientInterceptorTemplate();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        LOGGER.logTrace("Filtering request...");
        String propagation = template.onOutgoingRequest();
        RpcContext.getContext().setAttachment(HeaderNames.PROPAGATION_HEADER_NAME, propagation);
        return invoker.invoke(invocation);
    }

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        LOGGER.logTrace("Filtering response...");
        String extent = appResponse.getAttachment(HeaderNames.EXTENT_HEADER_NAME);
        if ( LOGGER.isTraceEnabled() ) {
            LOGGER.logTrace("extent: " + extent);
        }
        template.onIncomingResponse(extent);
    }

    @Override
    public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {

    }
}
