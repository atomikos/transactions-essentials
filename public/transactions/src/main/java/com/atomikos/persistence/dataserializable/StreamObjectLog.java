package com.atomikos.persistence.dataserializable;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.LogStream;
import com.atomikos.persistence.ObjectLog;
import com.atomikos.persistence.Recoverable;

public class StreamObjectLog implements ObjectLog{
	private static final Logger LOG = LoggerFactory.createLogger(StreamObjectLog.class);

    private LogStream logstream_;
    private Hashtable contentForNextCheckpoint_;
    private boolean initialized_ = false;
    private long flushesSinceLastCheckpoint_;
    private long maxFlushesBetweenCheckpoints_;

    public StreamObjectLog ( LogStream logstream , long maxFlushesBetweenCheckpoints )
    {
        logstream_ = logstream;
        contentForNextCheckpoint_ = new Hashtable ();
        maxFlushesBetweenCheckpoints_ = maxFlushesBetweenCheckpoints;
        flushesSinceLastCheckpoint_ = 0;
    }

    private synchronized void flushAndWriteCheckpointIfThresholdReached(SystemLogImage img, boolean shouldSync) throws LogException
    {
    	flushImage(img, shouldSync);
        flushesSinceLastCheckpoint_++;
        if ( flushesSinceLastCheckpoint_ >= maxFlushesBetweenCheckpoints_ ) {
            forceWriteCheckpoint();
        }
    }

	private void flushImage(SystemLogImage img, boolean shouldSync)
			throws LogException {
		logstream_.flushObject ( img , shouldSync );
		// cf case 85463: also update what we checkpoint
        if ( img.isForgettable() ) {
            discardThisAndPriorImagesForNextCheckpoint(img);
        } else {
            rememberImageForNextCheckpoint(img);
        }
	}

    /**
     * @see ObjectLog
     */

    public synchronized void init () throws LogException
    {
        if (initialized_) return;

        try {
            recoverFromUnderlyingLogStream();
        }
        catch ( Exception e ) {
        	logAsWarningAndRethrowAsLogException("Unexpected error during init",e,false);
        }
        finally {
            initialized_ = true;
            forceWriteCheckpoint();
        }

    }

	private void logAsWarningAndRethrowAsLogException(String msg, Exception e, boolean forceCheckpoint)
			throws LogException {
		LOG.logWarning(msg , e);
		if (forceCheckpoint) forceWriteCheckpoint();
		if (e instanceof LogException) throw (LogException) e;
		else throw new LogException (msg,e);
	}

	private void recoverFromUnderlyingLogStream() throws LogException {
		Vector recovered = logstream_.recover ();
		if ( recovered != null ) {
		    Enumeration entries = recovered.elements ();
		    while ( entries.hasMoreElements () ) {
		        SystemLogImage entry = (SystemLogImage) entries.nextElement ();
		        if ( entry.getId () != null ) {
		            if ( !entry.isForgettable () ) rememberImageForNextCheckpoint(entry);
		            else discardThisAndPriorImagesForNextCheckpoint(entry);
		        }
		    }
		}
	}

	private void forceWriteCheckpoint()
			throws LogException {
		logstream_.writeCheckpoint ( contentForNextCheckpoint_.elements () );
        flushesSinceLastCheckpoint_ = 0;
	}

    /**
     * @see ObjectLog
     */

    public synchronized Vector recover () throws LogException
    {
        if ( !initialized_ ) throw new LogException ( "Not initialized" );

        Vector ret = new Vector ();
        Enumeration enumm = contentForNextCheckpoint_.elements ();
        while ( enumm.hasMoreElements () ) {
            SystemLogImage next = (SystemLogImage) enumm.nextElement ();
            ret.addElement ( next.getObjectImage().restore() );
        }
        return ret;
    }

    /**
     * @see ObjectLog
     */

    public synchronized void flush ( Recoverable rec ) throws LogException
    {
        if ( rec == null ) return;

        SystemLogImage simg = new SystemLogImage ( rec, false );
        flush ( simg , true );
    }

    protected synchronized void flush ( SystemLogImage img , boolean shouldSync )
            throws LogException
    {
        if ( img == null ) return;

        try {
        	flushAndWriteCheckpointIfThresholdReached(img,shouldSync);
        } catch ( Exception e ) {
        	logAsWarningAndRethrowAsLogException("Unexpected error during flush", e ,true);
        }
    }

	private void rememberImageForNextCheckpoint(SystemLogImage img) {
		contentForNextCheckpoint_.put ( img.getId (), img );
	}

	private void discardThisAndPriorImagesForNextCheckpoint(SystemLogImage img) {
		if ( contentForNextCheckpoint_.containsKey ( img.getId () ) ) {
		    contentForNextCheckpoint_.remove ( img.getId () );
		}
	}


    /**
     * @see ObjectLog
     */

    public synchronized Recoverable recover ( Object id ) throws LogException
    {
        if ( !contentForNextCheckpoint_.containsKey ( id ) ) return null;

        SystemLogImage simg = (SystemLogImage) contentForNextCheckpoint_.get ( id );
        return simg.getObjectImage ().restore ();

    }

    /**
     * @see ObjectLog
     */

    public synchronized void delete ( Object id ) throws LogException
    {
        SystemLogImage previous = (SystemLogImage) contentForNextCheckpoint_.get ( id );
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
        try {
            closeUnderlyingLogStream();
        } catch ( LogException le ) {
            logAsWarningAndRethrowAsLogException("Unexpected error during close", le, false);
        } finally {
        	 initialized_ = false; // to allow re-init on restart of TM
        }
    }

	private void closeUnderlyingLogStream() throws LogException {
		if ( logstream_ != null ) {
		    logstream_.close ();
		}
	}
}
