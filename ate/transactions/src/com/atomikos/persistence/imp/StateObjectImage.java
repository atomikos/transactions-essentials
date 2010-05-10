//$Id: StateObjectImage.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: StateObjectImage.java,v $
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
//Revision 1.3  2005/08/09 15:24:47  guy
//Updated javadoc.
//
//$Id: StateObjectImage.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//Revision 1.2  2003/03/11 06:40:10  guy
//$Id: StateObjectImage.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: StateObjectImage.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//
//Revision 1.1.1.1.4.1  2002/08/30 15:07:46  guy
//Included serialVersionUID for backward log compatibility.:wq
//
//Revision 1.1.1.1  2001/10/05 13:21:34  guy
//Persistence module
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
