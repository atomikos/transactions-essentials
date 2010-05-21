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
