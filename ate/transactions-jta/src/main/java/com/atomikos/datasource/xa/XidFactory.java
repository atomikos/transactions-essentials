//$Id: XidFactory.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//$Log: XidFactory.java,v $
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
//Revision 1.2  2006/03/15 10:31:30  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:06  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.5  2004/10/12 13:04:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.4  2002/02/27 09:13:34  guy
//Changed XID creation: seed not necessary: inside one LOCAL ct there is no
//internal parallellism -> no violations of isolation possible.
//
//Revision 1.3  2002/02/26 11:18:07  guy
//Updated to use a different seed for each XID constructed.
//Needed to make each XID unique, even if for same tid and resource.
//
//Revision 1.2  2002/01/29 11:22:36  guy
//Updated CVS to latest state.
//

package com.atomikos.datasource.xa;

import javax.transaction.xa.Xid;

/**
 * 
 * 
 * A factory for creating new Xid instances. This allows different factories for
 * different resources, which is needed because some resources need a custom Xid
 * format.
 */

public interface XidFactory
{
    /**
     * Creates a new Xid instance for a given composite transaction id and
     * resource name.
     * 
     * @param tid
     *            The unique ID of the composite transaction.
     * @param resourcename
     *            The unique resource name.
     * @return Xid The Xid instance.
     */

    public Xid createXid ( String tid , String resourcename );
}
