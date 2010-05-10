//$Id: LogControlImp.java,v 1.2 2006/09/19 08:03:52 guy Exp $
//$Log: LogControlImp.java,v $
//Revision 1.2  2006/09/19 08:03:52  guy
//FIXED 10050
//
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.3  2006/03/21 13:22:56  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.2  2006/03/15 10:31:40  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:09  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.3  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2002/02/22 16:54:39  guy
//Corrected bug in LogControlImp, added debug comments in the rest.
//
//Revision 1.1  2002/01/08 15:21:26  guy
//Updated to new LogAdministrator paradigm.
//

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
