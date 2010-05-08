//$Id: TestPropagationMessage.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: TestPropagationMessage.java,v $
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
//Revision 1.2  2001/03/06 19:05:39  pardon
//Adapted heuristic message passing and completed Participant implementation.
//
//Revision 1.1  2001/03/05 19:14:39  pardon
//Continued working on 2pc messaging.
//

package com.atomikos.icatch.imp;
import com.atomikos.icatch.Participant;

/**
 *
 *
 *A test implemenation for propagation message functions.
 */


class TestPropagationMessage extends PropagationMessage
{
    private boolean forcefail_=false;
    private Boolean answer_=null;
    private boolean retry_=false;
    java.util.Date date_=new java.util.Date();

    public TestPropagationMessage(Result result, boolean retry, 
			    boolean forcefail,
		   Boolean answer)
    {
        this(null,result,retry,forcefail,answer);
       
    }

    public TestPropagationMessage(Participant part,
			    Result result,
			    boolean retry,
			    boolean forcefail,
			    Boolean answer)
    {
        super(part,result);
        answer_=answer;
        forcefail_=forcefail;
        retry_=retry;
    }
    
    protected Object send() throws PropagationException
    {
        
        if (forcefail_)
	  throw new PropagationException(new Exception("Testing failure"),
					       retry_);
       
        return answer_;
    }

    public String toString() 
    {
        return ("TestPropagationMessage  "+date_.getTime());
    }


    protected void setForceFail(boolean fail)
    {
        forcefail_=fail;
    }
}

