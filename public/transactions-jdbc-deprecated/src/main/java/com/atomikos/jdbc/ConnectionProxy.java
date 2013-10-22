/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 *
 *
 *
 *
 *
 * A dynamic proxy to allow external pooling, and also to allow late enlisting
 * of resources with transactions.
 */

class ConnectionProxy implements InvocationHandler
{
	private static final Logger LOGGER = LoggerFactory.createLogger(ConnectionProxy.class);

	private final static List NON_TRANSACTIONAL_METHOD_NAMES = Arrays.asList(new String[] {
			"equals",
			"hashCode",
			"notify",
			"notifyAll",
			"toString",
			"wait"
			});



	static Set getAllImplementedInterfaces ( Class clazz )
    {
    		Set ret = null;

    		if ( clazz.getSuperclass() != null ) {
    			//if superclass exists: first add the superclass interfaces!!!
    			ret = getAllImplementedInterfaces ( clazz.getSuperclass() );
    		}
    		else {
    			//no superclass: start with empty set
    			ret = new HashSet();
    		}

    		//add the interfaces in this class
    		Class[] interfaces = clazz.getInterfaces();
    		for ( int i = 0 ; i < interfaces.length ; i++ ) {
    			ret.add ( interfaces[i] );
    		}

    		return ret;
    }

    static Object newInstance ( DTPPooledConnection pc , java.sql.Connection c ,
            TransactionalResource resource ) throws SQLException
    {
        Object ret = null;

        ConnectionProxy proxy = new ConnectionProxy ( c, resource, pc );

        Set interfaces = getAllImplementedInterfaces ( c.getClass() );
        Class[] interfaceClasses = ( Class[] ) interfaces.toArray ( new Class[0] );
        ret = java.lang.reflect.Proxy.newProxyInstance ( c.getClass ()
                .getClassLoader (), interfaceClasses, proxy );

        return ret;
    }

    private Connection wrapped_;
    private TransactionalResource resource_;
    private DTPPooledConnection pc_;

    private ConnectionProxy ( Connection wrapped ,
            TransactionalResource resource , DTPPooledConnection pc )
    {
        wrapped_ = wrapped;
        resource_ = resource;
        pc_ = pc;
    }

    /**
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke ( Object o , Method m , Object[] args )
            throws Throwable
    {
        if (NON_TRANSACTIONAL_METHOD_NAMES.contains(m.getName())) {
        	if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ("Calling non-transactional method '" + m.getName() + "' on connection proxy, bypassing enlistment");
        	return m.invoke ( wrapped_, args );
        }

        Object ret = null;
        XAResourceTransaction restx = null;
        CompositeTransaction ct = null;
        CompositeTransactionManager ctm = null;
        ctm = Configuration.instance().getCompositeTransactionManager ();
        boolean inTx = false;
        // false if resume should be called (first invocation in tx)

        if ( ctm != null )
            ct = ctm.getCompositeTransaction ();

        inTx = pc_.isInResourceTransaction ();
        if ( ct != null && ct.getProperty (  TransactionManagerImp.JTA_PROPERTY_NAME ) != null && !pc_.isDiscarded () ) {
            // only enlist if there is a NORMAL tx
            // AND if the pc is not yet discarded (i.e. closed event
            // without a new getConnection() event)
            if ( !inTx ) {
                // not yet enlisted
                // IMPORTANT: we should enlist ONLY ONCE,
                // since we don't have wrappers for each
                // type of dependent object (statements,...)
                // where the real action is
                // Also, for the same reason we can only
                // delist at close time, since that is the
                // earliest detectable moment in time
                // when all these dependent objects are no
                // longer accessible.
                // VITAL ASSERTION FOR THIS TO WORK:
                // after close, the ResourceTransaction association
                // of the pooled connection MUST be null, or the
                // same restx will be reused in different
                // JTA transactions!
                restx = (XAResourceTransaction) resource_
                        .getResourceTransaction ( ct );
                pc_.setResourceTransaction ( restx );
                restx.resume ();
                LOGGER
                        .logDebug ( "JDBC ConnectionProxy: using resource transaction: "
                                + restx.getXid () );

            }

        }

        try {
            if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "JDBC ConnectionProxy: delegating "
                    + m.getName () + " to connection " + wrapped_.toString () );
//            if ("close".equals(m.getName())) {
//
//            }
            ret = m.invoke ( wrapped_, args );
        } catch ( InvocationTargetException i ) {
            pc_.setInvalidated ();
            pc_.close ();
            if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug (
                    "Exception in pooled connection - closing it", i );
            AtomikosSQLException.throwAtomikosSQLException ( i.getMessage() , i );
        } catch ( Exception e ) {
            String msg = "Exception in pooled connection: unexpected error - closing it";
            LOGGER.logWarning ( msg, e );
            pc_.setInvalidated ();
            pc_.close ();
            AtomikosSQLException.throwAtomikosSQLException ( e.getMessage() , e );
        }

        return ret;

    }

}
