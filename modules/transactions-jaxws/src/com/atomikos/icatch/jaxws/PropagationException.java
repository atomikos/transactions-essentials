//$Id: PropagationException.java,v 1.1.1.1 2006/10/02 15:21:12 guy Exp $
//$Log: PropagationException.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:12  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:46  guy
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
//Revision 1.2  2006/03/15 10:31:42  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:10  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2005/08/11 09:24:19  guy
//Moved importing and exporting interfaces here.
//
package com.atomikos.icatch.jaxws;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * An exception to signal propagation errors during import. This exception is
 * used to indicate that the transaction headers of an incoming SOAP message are
 * incompatible with the local propagation preference.
 * 
 * 
 */
public class PropagationException extends Exception
{

    /**
     * 
     */
    public PropagationException ()
    {
        super ();
    }

    /**
     * @param reason
     */
    public PropagationException ( String reason )
    {
        super ( reason );
    }

    /**
     * @param reason
     * @param cause
     */
    public PropagationException ( String reason , Throwable cause )
    {
        super ( reason , cause );
    }

    /**
     * @param cause
     */
    public PropagationException ( Throwable cause )
    {
        super ( cause );
    }

}
