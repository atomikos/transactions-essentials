//$Id: SiblingMapper.java,v 1.2 2006/09/19 08:03:54 guy Exp $
//$Log: SiblingMapper.java,v $
//Revision 1.2  2006/09/19 08:03:54  guy
//FIXED 10050
//
//Revision 1.1.1.1  2006/08/29 10:01:10  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:36  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:52  guy
//Import.
//
//Revision 1.4  2006/03/21 13:22:32  guy
//Adapted for active recovery and 1 coordinator per subtx.
//
//Revision 1.3  2006/03/15 10:31:30  guy
//Formatted code.
//
//Revision 1.2  2006/03/15 10:22:52  guy
//Refactored to 1 coordinator per subtransaction.
//
//Revision 1.1.1.1  2006/03/09 14:59:06  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2004/10/12 13:04:52  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: SiblingMapper.java,v 1.2 2006/09/19 08:03:54 guy Exp $
//Revision 1.6  2004/09/03 10:02:12  guy
//$Id: SiblingMapper.java,v 1.2 2006/09/19 08:03:54 guy Exp $
//Redesigned XID and ResTx mapping to allow:
//$Id: SiblingMapper.java,v 1.2 2006/09/19 08:03:54 guy Exp $
//-second enlist for same underlying resource (without delist of first)
//$Id: SiblingMapper.java,v 1.2 2006/09/19 08:03:54 guy Exp $
//-each XID is unique even within the same tx
//$Id: SiblingMapper.java,v 1.2 2006/09/19 08:03:54 guy Exp $
//
//$Id: SiblingMapper.java,v 1.2 2006/09/19 08:03:54 guy Exp $
//Revision 1.5  2004/09/01 13:40:44  guy
//$Id: SiblingMapper.java,v 1.2 2006/09/19 08:03:54 guy Exp $
//Merged in TRMI 1.22 changes: logging.
//$Id: SiblingMapper.java,v 1.2 2006/09/19 08:03:54 guy Exp $
//Added acceptsAllXAResources functionality for JBoss integration.
//$Id: SiblingMapper.java,v 1.2 2006/09/19 08:03:54 guy Exp $
//
//$Id: SiblingMapper.java,v 1.2 2006/09/19 08:03:54 guy Exp $
//Revision 1.4  2003/03/11 06:42:57  guy
//$Id: SiblingMapper.java,v 1.2 2006/09/19 08:03:54 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: SiblingMapper.java,v 1.2 2006/09/19 08:03:54 guy Exp $
//
//Revision 1.3.4.1  2002/09/18 08:51:25  guy
//Added weakCompare mode for JMS SONIC.
//
//Revision 1.3  2002/02/26 14:08:29  guy
//Corrected getResourceTransaction: IllegalStateException if CT finished.
//Required for JTA compatibility.
//
//Revision 1.2  2002/01/29 11:22:35  guy
//Updated CVS to latest state.
//

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
