//$Id: ForgetResult.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: ForgetResult.java,v $
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
//Revision 1.2  2006/03/15 10:31:39  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:08  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.2  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/03/06 16:21:09  guy
//Adapted forget mechanism to include waiting for result. Otherwise, forget
//is not propagated due to TERMINATED state.
//

package com.atomikos.icatch.imp;

/**
 * 
 * 
 * A result object for forget messages.
 */

class ForgetResult extends Result
{

    protected boolean analyzed_;

    // true if all answers processed

    /**
     * Constructor.
     * 
     * @param count
     *            The number of messages to process.
     */

    public ForgetResult ( int count )
    {
        super ( count );
        analyzed_ = false;

    }

    protected synchronized void analyze () throws IllegalStateException,
            InterruptedException

    {
        // nothing to do here
    }

}
