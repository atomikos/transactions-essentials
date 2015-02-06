/**
 * Copyright (C) 2000-2014 Atomikos <info@atomikos.com>
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



/**
 *
 *
 *For managing a set of unique IDs on behalf of a given server
 *
 */

public class UniqueIdMgr
{


	private final static int MAX_LENGTH_OF_NUMERIC_SUFFIX = 8 + 5;
	private final static int MAX_COUNTER_WITHIN_SAME_MILLIS = 32000;


    String server_; //name of server
    int lastcounter_;
  

    /**
     *Generate a new instance for a given server.
     *Assumption: there are never two servers with the same name!
     *
     */

    public UniqueIdMgr ( String server ) {
        super();
        server_ = server;
        lastcounter_ = 0;
    }
 

    //FIX FOR BUG 10104
    private String getCountWithLeadingZeroes (int number)
    {
    		String ret = Long.toString ( number );
    		int max = Long.toString(MAX_COUNTER_WITHIN_SAME_MILLIS).length();
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
        if (lastcounter_ == MAX_COUNTER_WITHIN_SAME_MILLIS) lastcounter_ = 0;
        return getCommonPartOfId() + System.currentTimeMillis() + getCountWithLeadingZeroes ( lastcounter_ ) ;
    }

    private String getCommonPartOfId() {
    	StringBuffer ret = new StringBuffer(64);
		ret.append(server_);
		return ret.toString();
    }

	public int getMaxIdLengthInBytes() {
		// see case 73086
		return getCommonPartOfId().getBytes().length + MAX_LENGTH_OF_NUMERIC_SUFFIX;
	}


}


