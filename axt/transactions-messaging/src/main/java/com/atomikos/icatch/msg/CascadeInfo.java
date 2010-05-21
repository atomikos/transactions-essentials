package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A container type for information related to orphan detection at prepare time.
 */

public class CascadeInfo implements java.io.Serializable
{
    public int count;

    public String participant;

    /**
     * No-arg constructor required by java.io.Serializable.
     */

    public CascadeInfo ()
    {
        this ( 0 , null );
    }

    /**
     * Constructs a new instance.
     * 
     * @param count
     *            The no of invocations globally detected at the corresponding
     *            participant.
     * @param participant
     *            The URI of the participant.
     */

    public CascadeInfo ( int count , String participant )
    {
        this.count = count;
        this.participant = participant;
    }
}
