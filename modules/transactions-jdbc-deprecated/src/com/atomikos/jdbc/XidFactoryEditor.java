//$Id: XidFactoryEditor.java,v 1.2 2006/09/19 08:03:56 guy Exp $
//$Log: XidFactoryEditor.java,v $
//Revision 1.2  2006/09/19 08:03:56  guy
//FIXED 10050
//
//Revision 1.1.1.1  2006/08/29 10:01:12  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:31  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:32:01  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:14  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/08/09 15:25:06  guy
//Updated javadoc.
//
//Revision 1.2  2004/03/22 15:39:16  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.1  2003/05/18 09:43:15  guy
//Made xid factory a list property, and added an editor for this.
//

package com.atomikos.jdbc;

import java.beans.PropertyEditorSupport;

/**
 * 
 * 
 * A reflection-based property editor for XidFactor. This allows the displaying
 * of an enummeration of possibilities for the XidFactory.
 */

public class XidFactoryEditor extends PropertyEditorSupport
{
    public XidFactoryEditor ()
    {
    }

    public String[] getTags ()
    {
        return new String[] { "Default" };
    }

    public String getAsText ()
    {
        return (String) getValue ();
    }

    public void setAsText ( String text ) throws IllegalArgumentException
    {
        setValue ( text );
    }
}
