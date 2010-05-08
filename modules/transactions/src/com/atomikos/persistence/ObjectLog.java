//$Id: ObjectLog.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: ObjectLog.java,v $
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
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:32:07  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:16  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2005/08/09 15:24:47  guy
//Updated javadoc.
//
//Revision 1.1.1.1  2001/10/05 13:21:34  guy
//Persistence module
//
//Revision 1.1.1.1  2001/03/31 11:00:43  pardon
//adding persistence framework.
//
//Revision 1.3  2001/02/25 16:46:41  pardon
//changed LogManager: history became recover
//
//Revision 1.2  2001/02/25 11:12:37  pardon
//Added lots of new stuff.
//

package com.atomikos.persistence;

import java.util.Vector;

/**
 * 
 * manager, being some entity that allows the following.
 * 
 */

public interface ObjectLog
{
    /**
     * Flush to permanent memory.
     * 
     * @param recoverable
     *            Recoverable instance. NOTE: if the instance has the same
     *            ObjectId as a previous one, then the latter will be overridden
     *            by this one! More precisely, history() will only return the
     *            last image for a given ObjectId.
     * 
     * @exception LogException
     *                if it did not work.
     */

    public void flush ( Recoverable recoverable ) throws LogException;

    /**
     * Initialize the object log. To be called as the first method.
     * 
     * @exception LogException
     *                If error occurs.
     */

    public void init () throws LogException;

    /**
     * Recover all non-deleted object images flushed so far.
     * 
     * @return Vector A list of Recoverable instances, reconstructed from their
     *         images.
     */

    public Vector recover () throws LogException;

    /*
     * Recover the instance with given ID.
     * 
     * @param id The id to recover. @return Recoverable The recovered logimage,
     * null if not found. @exception LogException On failure.
     */

    public Recoverable recover ( Object id ) throws LogException;

    /**
     * Delete the given object from the log.
     * 
     * @param id
     *            The object UID.
     * @exception LogException
     *                on failure.
     */

    public void delete ( Object id ) throws LogException;

    /**
     * Closes the log after use.
     * 
     * @exception LogException
     *                on failure.
     */

    public void close () throws LogException;

}
