/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
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


  
    private final String commonPartOfId; //name of server
    private int lastcounter;
  

    /**
     *Generate a new instance for a given server.
     *Assumption: there are never two servers with the same name!
     *
     */

    public UniqueIdMgr ( String server ) {
        super();
        commonPartOfId=getCommonPartOfId(server);
        lastcounter = 0;
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

    public String get()
    {
        incrementAndGet();
        StringBuffer buffer = new StringBuffer();
        return buffer.append(commonPartOfId).
        			  append(System.currentTimeMillis()).
        			  append(getCountWithLeadingZeroes ( lastcounter )).
        			  toString() ;
    }


	private synchronized void incrementAndGet() {
		lastcounter++;
        if (lastcounter == MAX_COUNTER_WITHIN_SAME_MILLIS) lastcounter = 0;
	}

    private static String getCommonPartOfId(String server) {
    	StringBuffer ret = new StringBuffer(64);
		ret.append(server);
		return ret.toString();
    }

	public int getMaxIdLengthInBytes() {
		// see case 73086
		return commonPartOfId.getBytes().length + MAX_LENGTH_OF_NUMERIC_SUFFIX;
	}


}


