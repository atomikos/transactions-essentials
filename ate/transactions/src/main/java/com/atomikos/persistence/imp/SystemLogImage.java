package com.atomikos.persistence.imp;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.Recoverable;

/**
 * 
 * 
 * An internal system class for logging.
 */

class SystemLogImage implements Recoverable, Externalizable
{
    // TODO TEST: changed from Serializable to Externalizable!!!

    // Force-set the serial version ID to make sure that log
    // data can be read.
    static final long serialVersionUID = 4153546869295179306L;

    protected Recoverable recoverable_ = null;
    protected boolean forgettable_ = false;

    public SystemLogImage ()
    {
        // required for externalizable
        // or for writing the terminating entry on restart.
        forgettable_ = true;
    }

    public SystemLogImage ( Recoverable recoverable , boolean forgettable )
    {
        recoverable_ = recoverable;
        forgettable_ = forgettable;
    }

    public Object getId ()
    {
        if ( recoverable_ == null ) // terminating entry
            return new String ( "END_OF_LOG_ENTRY" );
        else
            return recoverable_.getId ();
    }

    /**
     * Test if an image is forgettable. Needed in case of sequential logs, to
     * write a termination image long after an image was flushed.
     */

    public boolean isForgettable ()
    {
        return forgettable_;
    }

    /**
     * Get the recoverable. Needed to return the right implementation class to
     * the client!
     * 
     * @return Recoverable The wrapped recoverable.
     */

    public ObjectImage getObjectImage ()
    {
        return recoverable_.getObjectImage ();
    }

    public Recoverable getRecoverable ()
    {
        return recoverable_;
    }

    public void readExternal ( ObjectInput in ) throws IOException,
            ClassNotFoundException
    {
        ObjectImage objectimage = null;
        objectimage = (ObjectImage) in.readObject ();
        recoverable_ = objectimage.restore ();
        forgettable_ = in.readBoolean ();

    }

    public void writeExternal ( ObjectOutput out ) throws IOException
    {
        ObjectImage img = recoverable_.getObjectImage ();
        out.writeObject ( img );
        out.writeBoolean ( forgettable_ );
    }

}
