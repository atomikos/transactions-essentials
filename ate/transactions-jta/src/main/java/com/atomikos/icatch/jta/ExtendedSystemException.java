//$Id: ExtendedSystemException.java,v 1.2 2006/09/19 08:03:54 guy Exp $
//$Log: ExtendedSystemException.java,v $
//Revision 1.2  2006/09/19 08:03:54  guy
//FIXED 10050
//
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
//Revision 1.4  2005/08/05 15:03:40  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.3  2004/10/12 13:03:38  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2004/06/25 11:47:22  guy
//Overridden printStackTrace method.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//

package com.atomikos.icatch.jta;

import java.util.Enumeration;
import java.util.Stack;

import javax.transaction.SystemException;

/**
 * 
 * 
 * A better system exception, containing nested errors in a stack.
 */

public class ExtendedSystemException extends SystemException
{
    private Stack errors_;

    public ExtendedSystemException ( String msg , Stack errors )
    {
        super ( msg );
        errors_ = errors;
    }

    /**
     * Get any nested errors as a stack.
     * 
     * @return Stack The nested error stack, or null if none.
     */

    public Stack getErrors ()
    {
        return (Stack) errors_.clone ();
    }

    public void printStackTrace ()
    {
        super.printStackTrace ();
        if ( errors_ != null ) {
            Enumeration enumm = errors_.elements ();
            while ( enumm.hasMoreElements () ) {
                Exception e = (Exception) enumm.nextElement ();
                e.printStackTrace ();
            }
        }
    }
}
