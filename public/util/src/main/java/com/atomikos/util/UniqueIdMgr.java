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


package com.atomikos.util;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


/**
 *
 *
 *For managing a set of unique IDs on behalf of a given server
 *
 */

public class UniqueIdMgr 
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(UniqueIdMgr.class);

    private static long MAX_PER_EPOCH = 32000;
    //max no of txs with same epoch part in xid.
    //constructs a unique TID for a particular server
    
	private final static int MAX_LENGTH_OF_NUMERIC_SUFFIX = 16;

    
    String server_; //name of server
    long epoch_;//needed to ensure uniqueness
    long lastcounter_;
    int limit_;
    //if positive: indicates max epoch value -> limits number
    //of startups of servers
    private String prefix_, suffix_;
    //prefix and suffix to add to each generated id
    
    private VersionedFile file_;
   
    /**
     *Generate a new instance for a given server.
     *Assumption: there are never two servers with the same name!
     *
     */
    
    public UniqueIdMgr ( String server ) {
        this ( server , "./" );
        limit_ = -1;
        server_ = server;
        epoch_ = new Date().getDate();
        lastcounter_ = 0;
    }
    
    /**
     *Preferred constructor: based on file-logged epoch value.
     *
     *@param server The server's unique name.
     *@param directorypath The path (with ending slash!) where the epoch 
     *file should be written.
     */
     
    public UniqueIdMgr ( String server , String directorypath ) 
    {
        this ( server , directorypath , -1 );
    }
    
    /**
     *Constructor for startup limit. 
     *Useful for evaluation versions:
     *after the epoch reaches the limit, no new 
     *IDs will be created.
     */
     
    public UniqueIdMgr ( String server, String directorypath, int limit )
    {
        super();
        limit_ = limit;  
        server_ = server;
        file_ = new VersionedFile ( directorypath , server , ".epoch" );
        setSuffix ( "" );
        setPrefix ( "" );
        
       
        try {
          epoch_ = readEpoch ( );
        }
        catch ( Exception e )  {
          throw new RuntimeException ( e.getMessage() );	
        }
        lastcounter_ = 0;
    }
    
    /**
     * Sets the suffix to add to each generated ID.
     * 
     * @param suffix The suffix, defaults to empty string.
     * This suffix is added directly after the server (base) name
     * but is not the last part of the ID.
     */
    public void setSuffix ( String suffix )
    {
    	suffix_ = suffix;
    }
    
    /**
     * Sets the prefix to add to each generated ID.
     * @param prefix The prefix, defaults to empty string.
     * This value is added at the very beginning of each
     * generated ID.
     */
    
    public void setPrefix ( String prefix )
    {
    	prefix_ = prefix;
    }
    
    
    
    /**
     *Read the next epoch value from the epoch file.
     *At the same time, this increments the value in the epoch file,
     *for next restart.
     *
     *@return long The next value to use.
     *@exception IOException If reading or writing fails.
     */
     
    protected long readEpoch() throws IOException
    {   
        long ret = 0;
             
        
        try {
			  FileInputStream fin = file_.openLastValidVersionForReading();
			  DataInputStream din = new DataInputStream ( fin );
			  ret = din.readLong();
			  din.close();
			  fin.close();
		} catch  ( FileNotFoundException firstStartup ) {
			//merely use the initial epoch value
		}
        
        
        writeEpoch ( ret + 1 );
        
        if ( ret + 1 <= 0 ) 
          throw new RuntimeException ( "Epoch overflow!");
        
        return ret+1 ;
    }
    
    /**
     *Write the given value into the epoch file.
     *@param value The next epoch value.
     *@exception IOException On failure.
     */
     
    protected void writeEpoch ( long value ) throws IOException
    { 
      if ( value <= 0 || ( limit_ >= 0 && value > limit_ ) ) {
        throw new RuntimeException ( "Epoch overflow!" );
      }
      
      FileOutputStream fout = file_.openNewVersionForWriting();
      DataOutputStream dout = new DataOutputStream ( fout );
      dout.writeLong ( value );
      dout.flush();
      fout.flush ();
      //FIX FOR CASE 31037
      fout.getFD ().sync ();
      dout.close();
      fout.close();
      file_.discardBackupVersion();
      file_.close();
    }
    
    //FIX FOR BUG 10104
    private String getCountWithLeadingZeroes ( long number )
    {
    		String ret = Long.toString ( number );
    		int max = Long.toString ( MAX_PER_EPOCH ).length();
    		int len = ret.length();
    		StringBuffer zeroes = new StringBuffer();
    		while ( len < max ) {
    			zeroes.append ( "0" );
    			len++;
    		}
    		ret = zeroes.append ( ret ).toString();
    		return ret;
    }

    
    /**
     *The main way of obtaining a new UniqueId.
     *
     */
	
    public synchronized String get()
    {
        lastcounter_++;
        if ( lastcounter_ > MAX_PER_EPOCH ) {
        	lastcounter_ = 0;
        	epoch_++;
        	try {
        	  writeEpoch ( epoch_ );
        	}
        	catch ( Exception e ) {
        	  throw new RuntimeException ( e.getMessage() );	
        	}
        }
        
        return getCommonPartOfId() + getCountWithLeadingZeroes ( lastcounter_ )  + getCountWithLeadingZeroes ( epoch_ );
    }
    
    private String getCommonPartOfId() {
    	StringBuffer ret = new StringBuffer(64);
		ret.append(prefix_).append(server_).append(suffix_);
		return ret.toString();
    }

	public int getMaxIdLengthInBytes() {
		// see case 73086
		return getCommonPartOfId().getBytes().length + MAX_LENGTH_OF_NUMERIC_SUFFIX;
	}
    
   
}


