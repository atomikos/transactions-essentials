//$Id: TestStateRecoverableObjectImage.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: TestStateRecoverableObjectImage.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:21  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/09 15:24:49  guy
//Updated javadoc.
//
//Revision 1.3  2002/02/20 10:11:00  guy
//Added generic test files for state recovery mech.
//
//Revision 1.2  2002/02/18 13:32:33  guy
//Added test files to package under CVS.
//
//Revision 1.1  2002/01/29 12:34:24  guy
//Added test files to package dir.
//

package com.atomikos.persistence.imp;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.Recoverable;

/**
 *
 *
 *An object image for testing.
 */

public class TestStateRecoverableObjectImage implements ObjectImage
{
    
    protected Object state_ ;
    protected Object id_;

    public TestStateRecoverableObjectImage () 
    {
    }

    public TestStateRecoverableObjectImage ( Object state ,
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
        state_ = in.readObject();
        id_ = in.readObject();
    }
    
}
