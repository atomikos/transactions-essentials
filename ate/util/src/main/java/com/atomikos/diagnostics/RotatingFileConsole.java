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

package com.atomikos.diagnostics;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * 
 * 
 * 
 * A console that writes to a set of rotating files.
 *
 * 
 */

public class RotatingFileConsole implements Console
{
	private static final Object eolIndicatorParameter = new Object();
	
	private int level = Console.WARN;
	private Logger logger;
	private FileHandler handler;
	
	
	public RotatingFileConsole ( String fileNamePattern , 
		int limit , int fileCount ) 
		throws SecurityException, IOException
	{
		setLevel ( Console.WARN );
		if ( limit >=0 ) {
			//see case 23539: make sure to append!
			handler = new FileHandler ( 
						fileNamePattern , limit , fileCount , true );
		}
		else {
			//see case 23539: make sure to append!
			handler = new FileHandler ( fileNamePattern , true );
		}
		
		logger = Logger.getLogger ( "com.atomikos.diagnostics" );
		logger.addHandler ( handler );
		logger.setLevel ( Level.ALL );
		logger.setUseParentHandlers ( false );
		handler.setFormatter ( new FileFormatter() );
		
	}

    /* (non-Javadoc)
     * @see com.atomikos.diagnostics.Console#println(java.lang.String)
     */
    public void println ( String string ) throws IOException
    {
    	//add non-null parameter to indicate EOL
        logger.log ( Level.WARNING , string , eolIndicatorParameter );

    }

    /* (non-Javadoc)
     * @see com.atomikos.diagnostics.Console#print(java.lang.String)
     */
    public void print ( String string ) throws IOException
    {
    	Object dummy = null;
    	//add null parameter to signal NO EOL
        logger.log ( Level.WARNING , string , dummy );

    }

    /* (non-Javadoc)
     * @see com.atomikos.diagnostics.Console#println(java.lang.String, int)
     */
    public void println ( String string, int level ) throws IOException
    {
        if ( getLevel() >= level ) {
        	println ( string );
        }

    }

    /* (non-Javadoc)
     * @see com.atomikos.diagnostics.Console#print(java.lang.String, int)
     */
    public void print(String string, int level) throws IOException
    {
        if ( getLevel() >= level ) {
        	print ( string );
        }

    }

    /* (non-Javadoc)
     * @see com.atomikos.diagnostics.Console#close()
     */
    public void close() throws IOException
    {
      	handler.close();
		
    }

    /* (non-Javadoc)
     * @see com.atomikos.diagnostics.Console#setLevel(int)
     */
    public void setLevel(int level)
    {
        this.level = level;

    }

	public int getLevel()
	{
		return this.level;
	}
	
	private static class FileFormatter 
	extends Formatter
	{
		private DateFormat formatter;
		
		FileFormatter()
		{
			//bug 10037: changed hour template from hh to kk
			formatter = new SimpleDateFormat ( "yy-MM-dd kk:mm:ss,SSS" );
		}

        /* (non-Javadoc)
         * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
         */
        public String format(LogRecord record)
        {
             StringBuffer buf = new StringBuffer();
			 
			 Object[] pars = record.getParameters();
			 if ( pars[0] != null ) {
			 	buf.append ( formatter.format ( new Date() ) );
			    buf.append ( " [" + Thread.currentThread().getName() + "] " );
			 }
			 
			 buf.append ( record.getMessage() );
			 buf.append ( "\n" );
			 
             return buf.toString();
        }

	}
}
