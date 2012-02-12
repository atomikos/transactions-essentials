/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.persistence.imp;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import com.atomikos.diagnostics.Console;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.LogStream;
import com.atomikos.persistence.ObjectLog;
import com.atomikos.persistence.Recoverable;

/**
 * 
 * implementation. It keeps on growing, and only does a checkpoint on restart.
 * 
 * 
 */

public class StreamObjectLog implements ObjectLog
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(StreamObjectLog.class);

    protected LogStream logstream_;
    protected Hashtable logTable_;

    protected long size_;
    private boolean initialized_ = false;
    protected boolean panic_ = false;
    // if true: flush methods generate exception
    // set by writeCheckpoint
    protected Console console_;
    // for diagnostics

    private long count_;
    // how many flushes since last checkpoint?

    private long maxCount_;
    // how many is max count until next checkpoint?

    private StreamObjectLog ()
    {
        // not to be called
    }

    /**
     * Constructor. Builds a new StreamObjectLog with the given logstream and
     * the specified maximum number of entries.
     * 
     * @param logstream
     *            The underlying logstream. This stream should be reserved for
     *            this instance! Upon close, the underlying stream will also be
     *            closed.
     * @param checkpointInterval
     *            How many flush() calls between two checkpoints?
     * @param console
     *            For output of feedback.
     * 
     */

    public StreamObjectLog ( LogStream logstream , long checkpointInterval ,
            Console console )
    {
        logstream_ = logstream;
        size_ = 0;
        console_ = console;
        logTable_ = new Hashtable ();
        maxCount_ = checkpointInterval;
        count_ = 0;
    }

    /**
     * Checks if count limit is reached, writes checkpoint if so.
     */

    private synchronized void writeCheckpoint () throws LogException
    {

        count_++;
        if ( count_ >= maxCount_ ) {
            logstream_.writeCheckpoint ( logTable_.elements () );
            count_ = 0;
        }

    }

    /**
     * @see ObjectLog
     */

    public synchronized void init () throws LogException
    {
        Stack errors = new Stack ();
        Vector recovered = null;

        if ( initialized_ )
            return;

        try {
            recovered = logstream_.recover ();

            if ( recovered != null ) {

                Enumeration entries = recovered.elements ();
                while ( entries.hasMoreElements () ) {
                    SystemLogImage entry = (SystemLogImage) entries
                            .nextElement ();

                    if ( entry.getId () != null ) {

                        if ( !entry.isForgettable () ) {
                            if ( !logTable_.containsKey ( entry.getId () ) )
                                size_++;

                            // this replaces previous entries of this tid
                            logTable_.put ( entry.getId (), entry );

                        } else if ( logTable_.containsKey ( entry.getId () ) ) {

                            // condition needed for duplicate deletes
                            // otherwise size will not be right.
                            // duplicate deletes are possible because a
                            // terminator entry may be written more than once

                            logTable_.remove ( entry.getId () ); 

                            size_--;

                        }

                    } // if
                } // while
            } // if

        } // try
        catch ( LogException le ) {
            throw le;
        } catch ( Exception e ) {

            // bad exit condition
            errors.push ( e );
            throw new LogException ( e.getMessage (), errors );

        }// catch

        finally {
            initialized_ = true;
        }

        // write checkpoint, so that instance will work with new file.
        // ONLY done if init worked OK!
        // After this, fout_ is ready for flush

        logstream_.writeCheckpoint ( logTable_.elements () );
    }

    /**
     * @see ObjectLog
     */

    public synchronized Vector recover () throws LogException
    {
        Stack errors = new Stack ();
        Vector hist = new Vector ();

        if ( !initialized_ )
            throw new LogException ( "Not initialized" );
        Enumeration enumm = logTable_.elements ();

        while ( enumm.hasMoreElements () ) {
            SystemLogImage next = (SystemLogImage) enumm.nextElement ();

            hist.addElement ( next.getObjectImage ().restore () );
        }// while

        return hist;

    }// recover

    /**
     * @see ObjectLog
     */

    public synchronized void flush ( Recoverable rec ) throws LogException
    {
        if ( rec == null )
            return;

        SystemLogImage simg = new SystemLogImage ( rec, false );
        flush ( simg , true );

    }

    protected synchronized void flush ( SystemLogImage img , boolean shouldSync )
            throws LogException
    {
        Stack errors = new Stack ();

        if ( img == null )
            return;

        // test if last checkpoint was written ok.
        if ( panic_ )
            throw new LogException ( "StreamObjectLog: PANIC" );

        try {

            try {
                logstream_.flushObject ( img , shouldSync );
                writeCheckpoint ();
                // fout_.flush();
            } catch ( LogException ioerr ) {
                ioerr.printStackTrace ();
                errors.push ( ioerr );
                // make sure that logfile remains in consistent state by
                // checkpointing
                try {
                    logstream_.writeCheckpoint ( logTable_.elements () );
                } catch ( Exception e ) {
                    errors.push ( e );
                }
                throw new LogException ( ioerr.getMessage (), errors );
            }

            // replace/add local tid status in logTable_.
            // for Checkpointing!

            if ( img.isForgettable () ) {
                if ( logTable_.containsKey ( img.getId () ) ) {
                    // to avoid that logTable_ keeps growing!
                    logTable_.remove ( img.getId () );
                    size_--;
                }

            } else {
                if ( !logTable_.containsKey ( img.getId () ) ) {
                    size_++;
                }
                logTable_.put ( img.getId (), img );

            }

        }// try
        catch ( LogException le ) {
            System.err.println ( "Error in StreamObjectLog.flush() "
                    + le.getMessage () );
            throw le;
        } catch ( Exception e ) {

            System.err.println ( "Error in StreamObjectLog.flush() "
                    + e.getMessage () );

            errors.push ( e );
            throw new LogException ( e.getMessage (), errors );
        }// catch
    }

    /**
     * @see ObjectLog
     */

    public synchronized Recoverable recover ( Object id ) throws LogException
    {
        if ( !logTable_.containsKey ( id ) )
            return null;
        SystemLogImage simg = (SystemLogImage) logTable_.get ( id );
        return simg.getObjectImage ().restore ();

    }

    /**
     * @see ObjectLog
     */

    public synchronized void delete ( Object id ) throws LogException
    {
        SystemLogImage previous = (SystemLogImage) logTable_.get ( id );
        if ( previous == null ) {
            // all actives are in table -> if not there: already deleted
            return;
        }
        Recoverable bogus = previous.getRecoverable ();
        SystemLogImage simg = new SystemLogImage ( bogus, true );
        flush ( simg , false );
    }

    /**
     * @see ObjectLog
     */

    public synchronized void close () throws LogException
    {
        Stack errors = new Stack ();
        try {
            if ( logstream_ != null ) {
                logstream_.close ();
            }
            initialized_ = false;
            // so logstream will be read on re-init ( restart of client TM )

        } catch ( LogException le ) {
            throw le;
        } catch ( Exception e ) {
        	e.printStackTrace();
            errors.push ( e );
            throw new LogException ( e.getMessage (), errors );
        }
    }

}
