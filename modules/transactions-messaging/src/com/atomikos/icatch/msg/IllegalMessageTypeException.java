//$Id: IllegalMessageTypeException.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: IllegalMessageTypeException.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:15  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:46  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:30  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:48  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:12  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2005/08/05 15:03:46  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.4.1  2004/06/14 08:09:18  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.1.2.1  2002/10/31 16:06:48  guy
//Added basic message framework for 2PC over message systems.
//

package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * An exception indicating that a message type is not understood.
 */

public class IllegalMessageTypeException extends Exception
{
    public IllegalMessageTypeException ()
    {
        super ();
    }

    public IllegalMessageTypeException ( String s )
    {
        super ( s );
    }

}
