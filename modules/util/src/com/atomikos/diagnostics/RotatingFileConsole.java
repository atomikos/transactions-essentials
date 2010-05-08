//$Id: RotatingFileConsole.java,v 1.2 2006/09/04 15:27:55 guy Exp $
//$Log: RotatingFileConsole.java,v $
//Revision 1.2  2006/09/04 15:27:55  guy
//FIXED: 10037
//
//Revision 1.1.1.1  2006/08/29 10:01:15  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:51  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:40  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:03  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:43  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2005/04/29 15:00:59  guy
//Changed to allow unlimited size as well.
//
//Revision 1.1  2005/04/29 14:30:09  guy
//Added rotating console implementation.
//
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
