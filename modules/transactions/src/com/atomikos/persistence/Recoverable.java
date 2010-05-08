//$Id: Recoverable.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: Recoverable.java,v $
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
//Revision 1.1.1.1  2006/03/22 13:46:55  guy
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
//Revision 1.1.1.1  2001/03/31 11:00:43  pardon
//adding persistence framework.
//
//Revision 1.1  2001/02/25 11:12:37  pardon
//Added lots of new stuff.
//

package com.atomikos.persistence;

import com.atomikos.util.Identifiable;

/**
 * 
 * 
 * Recoverable interface: supports images for object reconstruction.
 * 
 */

public interface Recoverable extends Identifiable
{

    /**
     * Get an object image for this instance. Allows later reconstruction of the
     * instance.
     */

    public ObjectImage getObjectImage ();

}
