/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.atomikos.icatch.Participant;

/**
 * A Result is responsible for collecting the replies of a termination round.
 */

abstract class Result
{

    public static final int ALL_OK = 0 , HEUR_HAZARD = 1 , HEUR_MIXED = 2 ,
            HEUR_ROLLBACK = 3 , HEUR_COMMIT = 4 , ALL_READONLY = 5 ,
            ROLLBACK = 6;

    protected int result_ = -1;
    // should be set by analyze()

    protected int numberOfMissingReplies_ = 0;
    protected Stack<Reply> replies_ = new Stack<Reply>();
    private Set<Participant> repliedlist_ = new HashSet<Participant>();

    public Result ( int numberOfRepliesToWaitFor )
    {
        numberOfMissingReplies_ = numberOfRepliesToWaitFor;
    }

    /**
     * Get the overall result for this communication round.
     *
     * @return int One of the static codes.
     *
     * @exception IllegalStateException
     *                If active msgs exist.
     * @exception InterruptedException
     *                If interrupted during wait.
     */

    public int getResult() throws IllegalStateException, InterruptedException
    {
        calculateResultFromAllReplies();
        return result_;
    }


    /**
     * Abstract method: analyze the results for this message round.
     *
     * @exception IllegalStateException
     *                If not done yet.
     * @exception InterruptedException
     *                If interruption during result wait.
     */

    protected abstract void calculateResultFromAllReplies() throws IllegalStateException,
            InterruptedException;

    
    private boolean ignoreReply ( Reply reply ) {
    	// retried messages are not counted in result
        // and duplicate entries per participant neither
        // otherwise duplicates arise if a participant sends replay
    	return reply.isRetried() || repliedlist_.contains(reply.getParticipant());
    }

    /**
     * Add a reply to the result.
     *
     * @param reply
     *            The reply to add.
     */

    public synchronized void addReply(Reply reply)
    {
        if ( !ignoreReply(reply) ) {
        	repliedlist_.add(reply.getParticipant());
        	replies_.push(reply);
        	numberOfMissingReplies_--;
        	notifyAll();
        }
    }

    /**
     * Get all replies for this result's message round. Block until ready.
     *
     * @return Stack All replies in a stack.
     * @exception IllegalStateException
     *                If not all replies are in yet.
     * @exception InterruptedException
     *                During waiting interrupt.
     */

    public Stack<Reply> getReplies() throws IllegalStateException,
            InterruptedException
    {
        waitForReplies();
        return replies_;
    }

    /**
     * Wait until all replies arrived.
     *
     * @exception InterruptedException
     *                If the wait is interrupted.
     */

    synchronized void waitForReplies() throws InterruptedException
    {
        while ( numberOfMissingReplies_ > 0 ) wait();
    }
    
    /**
     * Finds the first exception in the results.
     * 
     * @return The exception, or null if none.
     */
    Exception findFirstOriginalException() {
    	Exception ret = null;
    	for (Reply reply : replies_) {
    		ret = reply.getException();
    		if (ret != null) break;
    	}
    	return ret;
    }

}
