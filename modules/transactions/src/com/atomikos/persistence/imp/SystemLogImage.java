//$Id: SystemLogImage.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: SystemLogImage.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:06  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:32  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:55  guy
//Import.
//
//Revision 1.2  2006/03/15 10:32:07  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:16  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/09/16 08:59:38  guy
//Modified to implement Externalizable (not Serializable) and
//to write the ObjectImage instead of the Recoverable object itself.
//
//Revision 1.3  2005/08/09 15:24:47  guy
//Updated javadoc.
//
//$Id: SystemLogImage.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//Revision 1.2  2003/03/11 06:40:10  guy
//$Id: SystemLogImage.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: SystemLogImage.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//
//Revision 1.1.1.1.4.1  2002/08/30 15:07:46  guy
//Included serialVersionUID for backward log compatibility.:wq
//
//Revision 1.1.1.1  2001/10/05 13:21:34  guy
//Persistence module
//

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
