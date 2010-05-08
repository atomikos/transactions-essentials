//$Id: TestPropagationMessageResult.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: TestPropagationMessageResult.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:55  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:18  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/08/09 15:23:39  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.2  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/02/18 13:32:20  guy
//Added test files to package under CVS.
//
//Revision 1.1  2001/03/05 19:14:39  pardon
//Continued working on 2pc messaging.
//


package com.atomikos.icatch.imp;



/**
 *
 *
 *A test class for PropagationMessage testing.
 *Needed because in the simple test, we need to add the reply
 *even if it is retried. Otherwise, testing is harder.
 */

class TestPropagationMessageResult extends Result
{
    public TestPropagationMessageResult(int count)
    {

        super(count);
    }
    
    public void analyze() throws IllegalStateException
    {

    }

    public synchronized void addReply(Reply reply)
    {
        replies_.push(reply);
        messagecount_--;
        notifyAll();
    }
}
