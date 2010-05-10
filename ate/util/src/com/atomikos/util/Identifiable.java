//$Id: Identifiable.java,v 1.1.1.1 2006/08/29 10:01:15 guy Exp $
//$Log: Identifiable.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:15  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:51  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:41  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:04  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:45  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/08/10 08:26:38  guy
//Updated javadoc.
//
//Revision 1.2  2005/08/09 15:24:36  guy
//Updated javadoc.
//
//Revision 1.1.1.1  2001/10/05 13:23:58  guy
//Utilities module
//


package com.atomikos.util;

/**
 *
 *
 *Identifiable objects are those that have a unique object ID,
 *which can be application-dependent.
 *Ideally, this ID can be used to reference the object, even after it has been
 *moved to stable storage.
 */

public interface Identifiable
{
    /**
     *Get the id.
     *
     *@return Object The id, should be the same as returned by the
     *corresponding logimage.
     */
     
    public Object getId();     
 
    
}
