package com.atomikos.icatch.imp;

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
