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

package com.atomikos.icatch.imp;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.LogControl;

/**
 * 
 * 
 * A default implementation of LogControl.
 */

class LogControlImp implements com.atomikos.icatch.admin.LogControl
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(LogControlImp.class);

    private TransactionServiceImp service_;

    /**
     * Create a new instance.
     * 
     * @param service
     *            The transaction service to work with.
     */

    LogControlImp ( TransactionServiceImp service )
    {
        service_ = service;
    }

    /**
     * @see LogControl
     */

    public AdminTransaction[] getAdminTransactions ()
    {
        AdminTransaction[] ret = null;
        AdminTransaction[] template = new AdminTransaction[0];
        Vector vect = new Vector ();
        Enumeration enumm = service_.getCoordinatorImpVector ().elements ();
        while ( enumm.hasMoreElements () ) {
            CoordinatorImp c = (CoordinatorImp) enumm.nextElement ();
            AdminTransaction tx = new AdminTransactionImp ( c );
            vect.addElement ( tx );
        }
        ret = (AdminTransaction[]) vect.toArray ( template );

        return ret;
    }

    /**
     * @see LogControl
     */

    public AdminTransaction[] getAdminTransactions ( String[] tids )
    {
        AdminTransaction[] ret = null;
        AdminTransaction[] temp = new AdminTransaction[0];
        Vector vect = new Vector ();

        Hashtable filter = new Hashtable ();
        for ( int i = 0; i < tids.length; i++ ) {
            filter.put ( tids[i], tids[i] );
        }

        Enumeration enumm = service_.getCoordinatorImpVector ().elements ();
        while ( enumm.hasMoreElements () ) {
            CoordinatorImp c = (CoordinatorImp) enumm.nextElement ();
            if ( filter.containsKey ( c.getCoordinatorId () ) ) {
                AdminTransaction tx = new AdminTransactionImp ( c );
                vect.addElement ( tx );
            }
        }
        ret = (AdminTransaction[]) vect.toArray ( temp );

        return ret;
    }
}
