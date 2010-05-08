//$Id: DefaultXidFactory.java,v 1.2 2007/02/05 08:20:58 guy Exp $
//$Log: DefaultXidFactory.java,v $
//Revision 1.2  2007/02/05 08:20:58  guy
//Merged in changes from 3.1.4
//
//Revision 1.1.1.1.4.1  2007/01/24 13:44:49  guy
//FIXED 10111
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
//Revision 1.3  2006/03/15 10:31:30  guy
//Formatted code.
//
//Revision 1.2  2006/03/15 10:22:52  guy
//Refactored to 1 coordinator per subtransaction.
//
//Revision 1.1.1.1  2006/03/09 14:59:06  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.8  2004/10/12 13:04:52  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.7  2004/09/06 09:29:29  guy
//Redesigned recovery.
//Redesigned XID generation: this is now done based on the TM name,
//no longer the resource name. This allows one resource to generate
//ALL xids (bootstrapping TM) and nevertheless each XAResource
//can be recovered as it is added later on.
//
//Revision 1.6  2004/09/03 10:02:12  guy
//Redesigned XID and ResTx mapping to allow:
//-second enlist for same underlying resource (without delist of first)
//-each XID is unique even within the same tx
//
//Revision 1.5  2004/03/22 15:39:34  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.4.10.1  2003/05/15 15:27:08  guy
//Added implementation of Serializable, needed for Bean setup of DataSource.
//
//Revision 1.4  2002/02/27 09:13:33  guy
//Changed XID creation: seed not necessary: inside one LOCAL ct there is no
//internal parallellism -> no violations of isolation possible.
//
//Revision 1.3  2002/02/26 11:18:07  guy
//Updated to use a different seed for each XID constructed.
//Needed to make each XID unique, even if for same tid and resource.
//
//Revision 1.2  2002/01/29 11:22:35  guy
//Updated CVS to latest state.
//

package com.atomikos.datasource.xa;

import javax.transaction.xa.Xid;

/**
 * 
 * 
 * A default Xid factory.
 */

public class DefaultXidFactory extends AbstractXidFactory implements
        java.io.Serializable
{


}
