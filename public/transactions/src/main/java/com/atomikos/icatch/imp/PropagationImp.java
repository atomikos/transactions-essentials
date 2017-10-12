/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.Stack;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RecoveryCoordinator;

/**
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
        Stack<CompositeTransaction> lineage = propagation.getLineage ();

        // replace most recent ancestor by adaptor
        CompositeTransaction remote = (CompositeTransaction) lineage.peek ();
        CompositeTransaction ct = new CompositeTransactionAdaptor ( lineage,
                remote.getTid (), remote.isSerial (), adaptor);

        lineage.pop ();

        // push adaptor on ancestor stack, in the place of the remote
        lineage.push ( ct );

        return new PropagationImp ( lineage, propagation.isSerial (),
                propagation.getTimeOut () );
    }

    private Stack<CompositeTransaction> lineage_;

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
    @SuppressWarnings("unchecked")
    public PropagationImp ( Stack<CompositeTransaction> lineage , boolean serial , long timeout )
    {
        serial_ = serial;
        lineage_ = (Stack<CompositeTransaction>) lineage.clone ();
        timeout_ = timeout;
    }

    /**
     * @see Propagation
     */

    public Stack<CompositeTransaction> getLineage ()
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
