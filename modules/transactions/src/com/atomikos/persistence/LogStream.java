//$Id: LogStream.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: LogStream.java,v $
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
//Revision 1.2  2006/03/15 10:32:06  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:16  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2005/05/10 08:44:47  guy
//Merged-in changes from Transactions_2_03 branch.
//
//Revision 1.1.1.1.18.1  2005/03/11 13:06:19  guy
//BUG FIX: Added sync() call in addition to flush.
//
//Revision 1.1.1.1  2001/10/05 13:21:34  guy
//Persistence module
//

package com.atomikos.persistence;

import java.util.Enumeration;
import java.util.Vector;

/**
 * 
 * 
 * The LogStream interface is an abstract definition of a stream that
 * encapsulates a physical log. In contrast to a regular input or output stream,
 * a log stream has both input and output facilities to the same underlying log.
 * It also allows for checkpointing, but the client is responsible for
 * determining the checkpoint contents. Checkpointing allows the log stream to
 * decrease in size.
 */

public interface LogStream
{
    /**
     * Get the size of the stream.
     * 
     * @return long The size of the stream.
     * @exception LogException
     *                On error.
     */

    public long getSize () throws LogException;

    /**
     * Reads the log contents, and initializes the data structure. Should be
     * called first.
     * 
     * @return Vector The read objects from the log.
     * @exception LogException
     *                On failure.
     */

    public Vector recover () throws LogException;

    /**
     * After intial recovery, it is good practice to write a checkpoint with
     * only the most recent data. This method does that, and can also be called
     * at random intervals during normal operation.
     * 
     * @param elements
     *            The elements to keep in the log.
     * 
     * 
     * @exception LogException
     *                On failure.
     */

    public void writeCheckpoint ( Enumeration elements ) throws LogException;

    /**
     * Flush (force) an object to the stream. If this method returns then the
     * object is guaranteed to be persisted.
     * 
     * @param o
     *            The object to flush.
     * @throws LogException
     *             On failure.
     */
    public void flushObject ( Object o ) throws LogException;

    /**
     * For proper termination: a close method.
     * 
     * @exception LogException
     *                On failure.
     */

    public void close () throws LogException;

}
