package com.atomikos.persistence.dataserializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.atomikos.icatch.DataSerializable;
import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.Recoverable;
import com.atomikos.util.ClassLoadingHelper;

public class SystemLogImage implements Recoverable, DataSerializable{

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

    private static final String END_OF_LOG_ENTRY = "END_OF_LOG_ENTRY";
    public Object getId ()
    {
        if ( recoverable_ == null ) // terminating entry
            return END_OF_LOG_ENTRY;
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


    public void writeData(DataOutput out) throws IOException {
		out.writeUTF(recoverable_.getClass().getName());
		((DataSerializable)recoverable_).writeData(out);
		out.writeBoolean ( forgettable_ );
	}

	public void readData(DataInput in) throws IOException {
			String className=in.readUTF();
			recoverable_ = (Recoverable)ClassLoadingHelper.newInstance(className);
			((DataSerializable)recoverable_).readData(in);
			forgettable_=in.readBoolean();
	}


}
