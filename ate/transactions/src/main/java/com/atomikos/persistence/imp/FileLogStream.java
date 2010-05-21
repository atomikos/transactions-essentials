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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.StreamCorruptedException;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import com.atomikos.diagnostics.Console;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.LogStream;
import com.atomikos.util.VersionedFile;

/**
 * 
 * 
 * A file implementation of a LogStream.
 */

public class FileLogStream implements LogStream
{
  

    private FileOutputStream output_;
    // keeps track of the latest output stream returned
    // from writeCheckpoint, so that it can be closed ( invalidated )
    // if necessary.

    private ObjectOutputStream ooutput_;

    private boolean simulateCrash_;
    // for testing

    private Console console_;
    // for output

    private boolean corrupt_;
    // true if checkpoint; second call of recover
    // not allowed, otherwise suffix_ will be wrong
    // especially since checkpoint failed.

    private VersionedFile file_;
    
    public FileLogStream ( String baseDir , String baseName , Console console )
            throws IOException
    {
        file_ = new VersionedFile ( baseDir , baseName , ".log" );
        simulateCrash_ = false;
        console_ = console;
        corrupt_ = false;
    }

    private void closeOutput () throws LogException
    {
        Stack errors = new Stack ();

        // try to close the previous output stream, if any.
        try {
            if ( file_ != null ) {
	             file_.close();
                 if ( console_ != null )
                        console_.println ( "Logfile closed: " + file_.getCurrentVersionFileName() );
            }
            output_ = null;
            ooutput_ = null;
        } catch ( IOException e ) {
            errors = new Stack ();
            throw new LogException ( "Error closing previous output", errors );
        }
    }

    /**
     * Makes write checkpoint crash before old file delete.
     * 
     * For debugging only.
     */

    void setCrashMode ()
    {
        simulateCrash_ = true;
    }

   
    public synchronized Vector recover () throws LogException
    {
       
        if ( corrupt_ )
            throw new LogException ( "Instance might be corrupted" );

        Stack errors = new Stack ();
        Vector ret = new Vector ();
        InputStream in = null;

        try {
            FileInputStream f = file_.openLastValidVersionForReading();
      
            in = f;

            ObjectInputStream ins = new ObjectInputStream ( in );
            int count = 0;
            if ( console_ != null ) {
                console_.println ( "Starting read of logfile " + file_.getCurrentVersionFileName() );
            }
            while ( in.available () > 0 ) {
                // if crashed, then unproper closing might cause endless
                // blocking!
                // therefore, we check if avaible first.
                count++;
                Object nxt = ins.readObject ();

                ret.addElement ( nxt );
                if ( count % 10 == 0 ) {
                    if ( console_ != null )
                        console_.print ( "." );
                }

            }
            if ( console_ != null ) {
                console_.println ( "Done read of logfile" );
            }
        } catch ( java.io.EOFException unexpectedEOF ) {
            // ignore, since this happens if log was not closed properly
            // due to crash
        	// merely return what was read so far...
        } catch ( StreamCorruptedException unexpectedEOF ) {
            // ignore, since this happens if log was not closed properly
            // due to crash
        	// merely return what was read so far...
        } catch ( ObjectStreamException unexpectedEOF ) {
            // ignore, since this happens if log was not closed properly
            // due to crash
        	// merely return what was read so far...
        } catch ( FileNotFoundException firstStart ) {
        	// the file could not be opened for reading;
        	// merely return the default empty vector
        } catch ( Exception e ) {
            System.err.println ( e.getMessage () );
            System.err.println ( e.getClass ().getName () );
            e.printStackTrace ();
            errors.push ( e );
            throw new LogException ( "Error in recover", errors );
        } finally {
            try {
                if ( in != null )
                    in.close ();

            } catch ( IOException io ) {
                errors.push ( io );
                throw new LogException ( "Error in recover", errors );
            }
        }

        return ret;
    }

    public synchronized void writeCheckpoint ( Enumeration elements )
            throws LogException
    {
        Stack errors = new Stack ();

        // first, make sure that any pending output stream handles
        // in the client are invalidated
        closeOutput ();

        try {
            // open the new output file
            // NOTE: after restart, any previous and failed checkpoint files
            // will be overwritten here. That is perfectly OK.
            output_ = file_.openNewVersionForWriting();
            ooutput_ = new ObjectOutputStream ( output_ );
            while ( elements != null && elements.hasMoreElements () ) {
                Object next = elements.nextElement ();
                ooutput_.writeObject ( next );
            }
            ooutput_.flush ();
            output_.flush ();
            output_.getFD ().sync ();
            // NOTE: we do NOT close the object output, since the client
            // will probably want to write more!
            // Thus, we return the open stream to the client.
            // Any closing will be done later, during cleanup if necessary.

            if ( simulateCrash_ ) {
            	corrupt_ = true;
            	throw new LogException ( "Old file could not be deleted" );
            }
            
            try {
            	file_.discardBackupVersion();
            } catch ( IOException errorOnDelete ) {
            	 corrupt_ = true;
                 // should restart
                 throw new LogException ( "Old file could not be deleted" );
            }
        } catch ( Exception e ) {
            errors.push ( e );
            throw new LogException ( "Error during checkpointing", errors );
        }
        
       

    }

    public synchronized void flushObject ( Object o ) throws LogException
    {
        if ( ooutput_ == null )
            throw new LogException ( "Not Initialized or already closed" );
        try {
            ooutput_.writeObject ( o );
            output_.flush ();
            ooutput_.flush ();
            output_.getFD ().sync ();
        } catch ( IOException e ) {
            Stack errors = new Stack ();
            throw new LogException ( e.getMessage (), errors );
        }
    }

    public synchronized void close () throws LogException
    {
        closeOutput ();
    }

    public void finalize () throws Throwable
    {
        try {
            close ();
        } finally {
            super.finalize ();
        }
    }

	public long getSize() throws LogException 
	{
		return file_.getSize();
	}
}
