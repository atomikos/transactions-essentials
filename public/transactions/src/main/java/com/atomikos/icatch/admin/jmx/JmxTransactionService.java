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

package com.atomikos.icatch.admin.jmx;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistration;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.LogControl;
import com.atomikos.icatch.system.Configuration;

/**
 * An MBean implementation for JMX-based transaction administration. If you use
 * this class, then you will also need to register a JmxLogAdministrator with
 * the UserTransactionService. An instance of this class can be registered in a
 * JMX server, and will co-operate with the JmxLogAdministrator. The net effect
 * of this will be that pending transactions can be monitored and administered
 * via JMX.
 */

public class JmxTransactionService implements JmxTransactionServiceMBean,
        MBeanRegistration
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(JmxTransactionService.class);

    private MBeanServer server;
    // the server, to register TransactionMBean instances

    private ObjectName[] beans;
    // cache the last set of beans, needed to unregister
    // them eventually
    
    private boolean heuristicsOnly;


    /**
     * Creates a new instance.
     */

    public JmxTransactionService ()
    {
        super ();

    }

    private JmxTransaction createMBean ( AdminTransaction tx )
    {
        JmxTransaction ret = null;
        switch ( tx.getState () ) {
        case AdminTransaction.STATE_PREPARED:
            ret = new JmxPreparedTransaction ( tx );
            break;
        case AdminTransaction.STATE_HEUR_ABORTED:
            ret = new JmxHeuristicTransaction ( tx );
            break;
        case AdminTransaction.STATE_HEUR_COMMITTED:
            ret = new JmxHeuristicTransaction ( tx );
            break;
        case AdminTransaction.STATE_HEUR_HAZARD:
            ret = new JmxHeuristicTransaction ( tx );
            break;
        case AdminTransaction.STATE_HEUR_MIXED:
            ret = new JmxHeuristicTransaction ( tx );
            break;
        default:
            ret = new JmxDefaultTransaction ( tx );
            break;
        }

        return ret;
    }

    private synchronized void unregisterBeans ()
    {
        try {
            if ( beans != null ) {
                for ( int i = 0; i < beans.length; i++ ) {
                    if ( server.isRegistered ( beans[i] ) )
                        server.unregisterMBean ( beans[i] );
                }
            }
        } catch ( InstanceNotFoundException e ) {
            throw new RuntimeException ( e.getMessage () );
        } catch ( MBeanRegistrationException e ) {
            throw new RuntimeException ( e.getMessage () );
        }
        beans = null;
    }

    private AdminTransaction[] filterHeuristics ( AdminTransaction[] txs )
    {
    	List ret = new ArrayList();
    	for ( int i = 0 ; i < txs.length ; i++ ) {
    		AdminTransaction next = txs[i];
    		switch ( next.getState() ) {
    			case AdminTransaction.STATE_HEUR_ABORTED: 
    				ret.add ( next );	
    				break;
    			case AdminTransaction.STATE_HEUR_COMMITTED:
    				ret.add ( next );
    				break;
    			case AdminTransaction.STATE_HEUR_HAZARD:
    				ret.add ( next );
    				break;
    			case AdminTransaction.STATE_HEUR_MIXED:
    				ret.add ( next );
    				break;
    			default: break;
    		}
    	}
    	return ( AdminTransaction[] ) ret.toArray ( new AdminTransaction[0] );
    }
    
    /**
     * @see com.atomikos.icatch.admin.jmx.TransactionServiceMBean#getTransactions()
     */

    public synchronized ObjectName[] getTransactions ()
    {
        // clean up previous beans
        unregisterBeans ();
        LogControl logControl = JmxLogAdministrator.getInstance ()
                .getLogControl ();
        if ( logControl == null )
            throw new RuntimeException (
                    "LogControl is null: transaction service not running?" );
        AdminTransaction[] transactions = logControl.getAdminTransactions ();
        if ( heuristicsOnly ) {
        	transactions = filterHeuristics ( transactions );
        }
        beans = new ObjectName[transactions.length];

        for ( int i = 0; i < transactions.length; i++ ) {
            try {
                beans[i] = new ObjectName ( "atomikos.transactions", "TID",
                        transactions[i].getTid () );
                JmxTransaction bean = createMBean ( transactions[i] );
                server.registerMBean ( bean, beans[i] );
            } catch ( Exception e ) {
                throw new RuntimeException ( e.getMessage () );
            }
        }

        return beans;
    }

    /**
     * @see javax.management.MBeanRegistration#preRegister(javax.management.MBeanServer,
     *      javax.management.ObjectName)
     */

    public ObjectName preRegister ( MBeanServer server , ObjectName name )
            throws Exception
    {
        this.server = server;
        
        JmxLogAdministrator admin = JmxLogAdministrator.getInstance();
        Configuration.addLogAdministrator ( admin );
        
        if ( name == null )
            name = new ObjectName ( "atomikos", "name", "TransactionService" );
        return name;
    }

    /**
     * @see javax.management.MBeanRegistration#postRegister(java.lang.Boolean)
     */

    public void postRegister ( Boolean arg0 )
    {

        // nothing to do
    }

    /**
     * @see javax.management.MBeanRegistration#preDeregister()
     */
    public void preDeregister () throws Exception
    {
    	unregisterBeans();
    }

    /**
     * @see javax.management.MBeanRegistration#postDeregister()
     */
    public void postDeregister ()
    {
        // nothing to do

    }

    /**
     * Sets whether only heuristic transactions should be returned.
     * Optional, defaults to false.
     * 
     * @param heuristicsOnly 
     */
    
	public void setHeuristicsOnly ( boolean heuristicsOnly ) 
	{
		this.heuristicsOnly = heuristicsOnly;
		
	}

	
	public boolean getHeuristicsOnly() 
	{
		return heuristicsOnly;
	}

}
