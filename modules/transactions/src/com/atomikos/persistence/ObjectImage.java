//$Id: ObjectImage.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Id: ObjectImage.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: ObjectImage.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:06  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:32  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:32:07  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:16  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2005/08/09 15:24:47  guy
//Updated javadoc.
//
//Revision 1.1.1.1  2001/10/05 13:21:34  guy
//Persistence module
//

package com.atomikos.persistence;

import java.io.Externalizable;

import com.atomikos.util.Identifiable;

/**
 * 
 * 
 * An ObjectImage is a loggable state that can be managed by an ObjectLog.
 * 
 */

public interface ObjectImage extends Externalizable, Identifiable
{
    /**
     * Restore an equivalent replica of the original instance. Called by
     * ObjectLog on recovering the object.
     * 
     * @return Recoverable An equivalent replica of the original.
     * 
     */

    public Recoverable restore ();
}
