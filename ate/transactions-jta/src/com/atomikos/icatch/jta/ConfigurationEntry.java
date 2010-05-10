//$Id: ConfigurationEntry.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//$Log: ConfigurationEntry.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:10  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:43  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:10  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/08/05 15:03:40  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.2  2004/10/12 13:03:38  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//

package com.atomikos.icatch.jta;

import javax.transaction.xa.XAResource;

/**
 * 
 * 
 * A configuration entry consists of an XAResource and its name.
 */

class ConfigurationEntry
{
    private String name_;
    private XAResource xares_;

    ConfigurationEntry ( XAResource xares , String name )
    {
        xares_ = xares;
        name_ = name;
    }

    /**
     * Check for the name for the given resource.
     * 
     * @param xares
     *            The given XAResource
     * @return String The name, or null if wrong resource.
     */

    String getName ( XAResource xares )
    {
        try {
            if ( xares_.isSameRM ( xares ) )
                return name_;
            else
                return null;
        } catch ( Exception e ) {
            throw new RuntimeException ( e.getMessage () + " "
                    + e.getClass ().getName () );
        }
    }
}
