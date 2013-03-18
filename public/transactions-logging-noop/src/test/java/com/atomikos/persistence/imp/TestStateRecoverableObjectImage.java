package com.atomikos.persistence.imp;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.atomikos.icatch.TxState;
import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.Recoverable;

/**
 *
 *
 *An object image for testing.
 */

public class TestStateRecoverableObjectImage implements ObjectImage
{
    
    protected TxState state_ ;
    protected Object id_;

    public TestStateRecoverableObjectImage () 
    {
    }

    public TestStateRecoverableObjectImage ( TxState state ,
				     Object id  )
    {
        state_ = state;
        id_ = id;
    }


    public Object getId()
    {
        return id_;
    }

    public Recoverable restore()
    {
        return new TestStateRecoverable ( id_ , state_ );
    }
    
    public void writeExternal ( ObjectOutput out ) 
    throws IOException
    {
        out.writeObject ( state_ );
        out.writeObject ( id_ );
    }

    public void readExternal ( ObjectInput in )
        throws IOException, ClassNotFoundException
    {
        state_ =(TxState) in.readObject();
        id_ = in.readObject();
    }
    
}
