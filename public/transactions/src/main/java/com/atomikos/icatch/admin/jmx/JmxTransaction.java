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

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.atomikos.icatch.admin.AdminTransaction;

/**
 * The base class for MBean administration of transactions.
 */

public abstract class JmxTransaction implements JmxTransactionMBean,
        MBeanRegistration
{

    private AdminTransaction adminTransaction;

    private MBeanServer server;

    private ObjectName name;

    /**
     * Converts the given int state.
     *
     * @param state
     *            The given int state.
     * @return String The string state, or null if not found.
     */

    protected static String convertState ( int state )
    {
        String ret = "UNKNOWN";

        switch ( state ) {
        case AdminTransaction.STATE_ACTIVE:
            ret = "ACTIVE";
            break;
        case AdminTransaction.STATE_PREPARING:
            ret = "PREPARING";
            break;
        case AdminTransaction.STATE_PREPARED:
            ret = "PREPARED";
            break;
        case AdminTransaction.STATE_HEUR_MIXED:
            ret = "HEURISTIC MIXED";
            break;
        case AdminTransaction.STATE_HEUR_HAZARD:
            ret = "HEURISTIC HAZARD";
            break;
        case AdminTransaction.STATE_HEUR_COMMITTED:
            ret = "HEURISTIC COMMIT";
            break;
        case AdminTransaction.STATE_HEUR_ABORTED:
            ret = "HEURISTIC ROLLBACK";
            break;
        case AdminTransaction.STATE_COMMITTING:
            ret = "COMMITTING";
            break;
        case AdminTransaction.STATE_ABORTING:
            ret = "ROLLING BACK";
            break;
        case AdminTransaction.STATE_TERMINATED:
            ret = "TERMINATED";
            break;

        default:
            break;
        }

        return ret;
    }

    

    /**
     * Wraps an existing AdminTransaction instance as an MBean.
     *
     * @param adminTransaction
     *            The existing to wrap.
     */

    public JmxTransaction ( AdminTransaction adminTransaction )
    {
        super ();
        this.adminTransaction = adminTransaction;

    }

    protected AdminTransaction getAdminTransaction ()
    {
        return adminTransaction;
    }

    protected void unregister ()
    {
        try {
            if ( server.isRegistered ( name ) )
                server.unregisterMBean ( name );
        } catch ( Exception e ) {
            e.printStackTrace ();
            throw new RuntimeException ( e.getMessage () );
        }
    }

    /**
     * @see com.atomikos.icatch.admin.jmx.TransactionMBean#getTid()
     */

    public String getTid ()
    {
        return adminTransaction.getTid ();
    }

    /**
     * @see com.atomikos.icatch.admin.jmx.TransactionMBean#getCombinedState()
     */

    public String getState ()
    {

        return adminTransaction.getState().name();
    }


    /**
     * @see javax.management.MBeanRegistration#preRegister(javax.management.MBeanServer,
     *      javax.management.ObjectName)
     */

    public ObjectName preRegister ( MBeanServer server , ObjectName name )
            throws Exception
    {
        this.server = server;
        if ( name == null )
            name = new ObjectName ( "atomikos.transactions", "TID", getTid () );
        this.name = name;
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
        // nothing to do

    }

    /**
     * @see javax.management.MBeanRegistration#postDeregister()
     */
    public void postDeregister ()
    {
        // nothing to do

    }
    
	@Override
	public String[] getParticipantDetails() {
		return adminTransaction.getParticipantDetails();
	}

}
