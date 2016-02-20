/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.admin.jmx;


import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistration;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.LogControl;
import com.atomikos.icatch.config.Configuration;

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
        JmxTransactionMBean[] mBeans = JmxTransactionMBeanFactory.createMBeans(transactions, heuristicsOnly);
        beans = new ObjectName[mBeans.length];
        for ( int i = 0; i < mBeans.length; i++ ) {
            try {
                beans[i] = new ObjectName ( "atomikos.transactions", "TID",
                        mBeans[i].getTid () );
                server.registerMBean ( mBeans[i], beans[i] );
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
