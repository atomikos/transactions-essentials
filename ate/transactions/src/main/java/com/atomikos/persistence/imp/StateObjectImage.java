package com.atomikos.persistence.imp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.Recoverable;

/**
 * 
 * 
 * An object image for reconstruction of staterecoverables through a state
 * recovery mgr.
 */

class StateObjectImage implements Recoverable, ObjectImage
{
    // force set serialUID to allow backward log compatibility.
    static final long serialVersionUID = 4440634956991605946L;

    protected ObjectImage img_;

    public StateObjectImage ()
    {
    }

    public StateObjectImage ( ObjectImage image )
    {
        img_ = image;
    }

    public Object getId ()
    {
        return img_.getId ();
    }

    public ObjectImage getObjectImage ()
    {
        return img_;
    }

    public Recoverable restore ()
    {
        return img_.restore ();
    }

    public void readExternal ( ObjectInput in ) throws IOException,
            ClassNotFoundException
    {
        img_ = (ObjectImage) in.readObject ();
    }

    public void writeExternal ( ObjectOutput out ) throws IOException
    {
        out.writeObject ( img_ );
    }
}
