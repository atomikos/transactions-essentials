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

