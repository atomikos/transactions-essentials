package com.atomikos.datasource.xa;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.ResourceTransaction;
import com.atomikos.icatch.CompositeTransaction;

/**
 * 
 * 
 * A SiblingMapper encapsulates the mapping policy for assigning a
 * ResourceTransaction to a composite tx instance.
 */

class SiblingMapper
{
    protected Hashtable siblings_;
    protected XATransactionalResource res_;

    protected String root_ ;

    SiblingMapper ( XATransactionalResource res , String root )
    {
        siblings_ = new Hashtable ();
        res_ = res;
        root_ = root;
    }

    protected ResourceTransaction map ( CompositeTransaction ct )
            throws ResourceException, IllegalStateException
    {
        Stack errors = new Stack ();
        XAResourceTransaction last = null;
        try {
            // check if previous map exists for the SAME sibling ct.
            last = (XAResourceTransaction) siblings_.get ( ct );
            if ( last == null || res_.usesWeakCompare ()
                    || res_.acceptsAllXAResources () || last.isActive () ) {

                // try to reuse another sibling's restx, but only if serial!
                Enumeration enumm = siblings_.elements ();
                if ( enumm.hasMoreElements () )
                    last = (XAResourceTransaction) enumm.nextElement ();

                if ( last == null || !ct.isSerial () || res_.usesWeakCompare ()
                        || res_.acceptsAllXAResources () || last.isActive () ) {

                    // if resource uses weak compare mode then
                    // do NOT reuse restx instances, since the
                    // TMJOIN flag may fail if multiple resource mgrs
                    // for the same vendor are in use.
                    // the same holds for acceptsAllXAResources
                    // also, in order to allow concurrent enlistings
                    // for the same XAResource, we need to return
                    // a new restx if the one found is still active

                    last = new XAResourceTransaction ( res_, ct , root_ );

                    // coord.addParticipant ( last ); //SUBTX ABORT
                    siblings_.put ( ct, last );
                }

            }
        }

        catch ( Exception e ) {
            errors.push ( e );
            throw new ResourceException ( "ResourceTransaction map failure",
                    errors );
        }

        ct.addParticipant ( last );

        return last;
    }
}
