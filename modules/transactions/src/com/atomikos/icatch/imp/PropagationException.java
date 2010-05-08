//$Id: PropagationException.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: PropagationException.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:40  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:09  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.5  2005/08/09 15:23:39  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.4  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.3  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2004/10/08 07:09:41  guy
//Added tolerance for exceptions in endRecovery.
//Added improved exception logging in Configuration.
//Added necessary files in Makefile.
//Overridden printStackTrace in PropagationException.
//Added/improved tests in RecoveryTester.
//Added improved lookup of jta.properties in UserTransactionServiceImp.
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//
//Revision 1.1  2001/03/01 19:26:57  pardon
//Added more.
//

package com.atomikos.icatch.imp;

/**
 * 
 * 
 * An error of propagation messages. Some errors are transient, leading to a
 * retry of the message send. Others are fatal, and reported back to the sender.
 */

class PropagationException extends java.io.IOException
{
    protected boolean transient_ = false;
    // default is fatal

    protected Exception detail_ = null;

    // wrapped exception

    /**
     * Constructor.
     * 
     * @param detail
     *            The wrapped exception.
     * @param trans
     *            If true, then the failure will NOT be reported to the Sender,
     *            but rather be dealt with in the Propagator by retrying it.
     */

    public PropagationException ( Exception detail , boolean trans )
    {
        super ();
        transient_ = trans;
        detail_ = detail;
    }

    /**
     * Get transient flag.
     * 
     * @return boolean True if the error is transient and its message can be
     *         retried.
     */

    public boolean isTransient ()
    {
        return transient_;
    }

    /**
     * Get detail.
     * 
     * @return Exception The underlying error cause.
     */

    public Exception getDetail ()
    {
        return detail_;
    }

    public void printStackTrace ()
    {
        super.printStackTrace ();
        if ( detail_ != null )
            detail_.printStackTrace ();
    }

}
