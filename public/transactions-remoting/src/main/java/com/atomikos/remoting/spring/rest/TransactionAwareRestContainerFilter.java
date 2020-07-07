/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.spring.rest;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.atomikos.icatch.RollbackException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.support.ContainerInterceptorTemplate;
import com.atomikos.remoting.support.HeaderNames;
/**
 * Filter (interceptor) that adds starts a local JTA transaction that becomes part 
 * of the remote client's transaction context.
 * 
 * This class is only needed for REST services that embed the Atomikos JTA transaction manager,
 * and it automatically makes incoming REST calls part of the caller's transaction.
 * 
 * Note: this interceptor only works if:
 * <ul>
 * <li>the local transaction service (Atomikos) is running, AND</li>
 * <li>a AtomikosRestPort instance is available for this JVM</li>
 * </ul>
 */
public class TransactionAwareRestContainerFilter extends OncePerRequestFilter {
	
	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionAwareRestContainerFilter.class);
	
	private ContainerInterceptorTemplate template = new ContainerInterceptorTemplate();
	
    @Override
    protected void doFilterInternal(HttpServletRequest request, final HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    	
    	String propagation = request.getHeader(HeaderNames.PROPAGATION_HEADER_NAME);
    	
    		try {
    			template.onIncomingRequest(propagation);
    			HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response) {
    			    @Override
    			    public void setStatus(int sc) {
    			        super.setStatus(sc);
    			        String extent = terminateImportedTransaction(sc);
						response.addHeader(HeaderNames.EXTENT_HEADER_NAME, extent);
    			    }
    			};
    			filterChain.doFilter(request, wrapper);
    		} catch (IllegalStateException e) {
    			LOGGER.logWarning("Detected invalid incoming transaction - aborting...");
    			response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value()); 
    		}
		
		
    }
    
    private String terminateImportedTransaction(int httpCode)  {
		String extent = null;
		try {					
			if(HttpStatus.valueOf(httpCode).is2xxSuccessful()) {
				extent = template.onOutgoingResponse(false);
			} else {
				extent = template.onOutgoingResponse(true);
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