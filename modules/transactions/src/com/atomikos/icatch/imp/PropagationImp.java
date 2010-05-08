//$Id: PropagationImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: PropagationImp.java,v $
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
//Revision 1.10  2005/08/10 16:23:03  guy
//Debugged/adapted for compensation and dito testing.
//
//Revision 1.9  2005/08/09 15:23:39  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.8  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.7  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: PropagationImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Revision 1.6  2004/09/08 15:41:12  guy
//$Id: PropagationImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Overridden equals() and hashCode() to work with JBoss client demarcated txs.
//$Id: PropagationImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//
//$Id: PropagationImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Revision 1.5  2003/03/11 06:38:53  guy
//$Id: PropagationImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: PropagationImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//
//Revision 1.4.4.1  2003/01/29 17:19:25  guy
//Changed Synchronization callback context for subtxs.
//
//Revision 1.4  2002/02/03 10:03:17  guy
//Updated comments.
//
//Revision 1.3  2001/11/02 07:46:48  guy
//Made class package again, since JTS redefines its own implementation anyway.
//
//Revision 1.2  2001/11/01 17:19:08  guy
//Made this class public, for reuse in JTS module.
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//

package com.atomikos.icatch.imp;

import java.util.Stack;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RecoveryCoordinator;

/**
 * 
 * 
 * Implementation of <code>Propagation</code> interface.
 */

public class PropagationImp implements Propagation
{

    /**
     * Create a new instance. This method replaces the lowest ancestor by an
     * adaptor (interposition) for both efficiency and correct replay handling.
     * Implementations of ImportingTransactionManager should use this method to
     * convert an incoming propagation into the proper local instance, or replay
     * requests will not work properly.
     * 
     * @param propagation
     *            The propagation for which to create a new instance.
     * @param adaptor
     *            The adaptor for replay requests.
     */

    public static Propagation adaptPropagation ( Propagation propagation ,
            RecoveryCoordinator adaptor )
    {
        Stack lineage = propagation.getLineage ();

        // replace most recent ancestor by adaptor
        CompositeTransaction remote = (CompositeTransaction) lineage.peek ();
        CompositeTransaction ct = new CompositeTransactionAdaptor ( lineage,
                remote.getTid (), remote.isSerial (), adaptor , remote.getCompositeCoordinator().isRecoverableWhileActive() );

        lineage.pop ();

        // push adaptor on ancestor stack, in the place of the remote
        lineage.push ( ct );

        return new PropagationImp ( lineage, propagation.isSerial (),
                propagation.getTimeOut () );
    }

    private Stack lineage_;

    private boolean serial_;

    private long timeout_;
    


    /**
     * Construct a new instance.
     * 
     * @param lineage
     *            The lineage stack of ancestors.
     * @param serial
     *            Serial mode indicator.
     * @param timeout
     *            The timeout left for the tx.
     */
    
    public PropagationImp ( Stack lineage , boolean serial , long timeout )
    {
        serial_ = serial;
        lineage_ = (Stack) lineage.clone ();
        timeout_ = timeout;
    }

    /**
     * @see Propagation
     */

    public Stack getLineage ()
    {
        return lineage_;
    }

    /**
     * @see Propagation
     */

    public boolean isSerial ()
    {
        return serial_;
    }

    /**
     * @see Propagation
     */

    public long getTimeOut ()
    {
        return timeout_;
    }

    /**
     * Required for JBoss integration: client demarcation depends on this.
     */
    public boolean equals ( Object o )
    {
        boolean ret = false;
        if ( o instanceof PropagationImp ) {
            PropagationImp other = (PropagationImp) o;
            CompositeTransaction otherCt = (CompositeTransaction) other.lineage_
                    .peek ();
            CompositeTransaction ct = (CompositeTransaction) lineage_.peek ();

            // if the lowermost parents are the same then the propagation
            // is also the same
            ret = ct.isSameTransaction ( otherCt );
        }
        return ret;
    }

    /**
     * Required for JBoss integration.
     */

    public int hashCode ()
    {
        int ret = 0;

        CompositeTransaction ct = (CompositeTransaction) lineage_.peek ();
        ret = ct.getTid ().hashCode ();

        return ret;
    }
    
 
}
