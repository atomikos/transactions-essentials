//$Id: Console.java,v 1.1.1.1 2006/08/29 10:01:15 guy Exp $
//$Log: Console.java,v $
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
//Revision 1.6  2005/10/10 08:11:53  guy
//Added getLevel method to inspect log level.
//
//Revision 1.5  2005/08/09 15:25:46  guy
//Updated javadoc.
//
//Revision 1.4  2004/10/12 13:05:04  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.3  2004/09/01 13:41:37  guy
//Merged in changes of TransactionsRMI 1.22
//
//Revision 1.2.12.1  2004/04/30 14:34:01  guy
//Added the concept of different levels.
//
//Revision 1.2  2002/01/29 11:26:28  guy
//Updated to latest state: repository seemed to be outdated?
//

package com.atomikos.diagnostics;

/**
 *
 *
 *A message console for system output warnings.
 *Warnings can have an optional level, which
 *determines whether or not they will be actually
 *shown (depending on the overall level set on the 
 *console).
 *
 */

public interface Console 
{
	/**
	 * Constant to indicate warning-level messages.
	 * This is the default level, and provides the 
	 * lowest number of log data. Use this 
	 * level to log coarse-grained information.
	 */
	static final int WARN = 1;
	
	/**
	 * Constant to indicate informational-level messages.
	 * Informational messages only show if the level of the log
	 * is set to INFO or DEBUG.
	 */
	
	static final int INFO = 2;
	
	/**
	 * Constant to indicate debug-level messages.
	 * This level can be used to provide info
	 * that only shows up if the console level is 
	 * set to this degree.
	 */
	static final int DEBUG = 3;
    
    /**
     *Print a message to the output of the console.
     *The level is assumed to be the default (WARN).
     *@param string The message to output.
     *@exception java.io.IOException On failure.
     */

    public void println(String string) throws java.io.IOException;
    
    /**
     *Print a string to the output, but no newline at the end.
     *The level is assumed to be the default (WARN).
     *@param string The string to print.
     *@exception java.io.IOException On failure.
     */
     
    public void print ( String string ) throws java.io.IOException;
    
    /**
     * Print a string with newline, at a given level of granularity.
     * @param string The string.
     * @param level The level (one of the predefined constants).
     * @throws java.io.IOException On failure.
     */
    public void println ( String string , int level ) throws java.io.IOException;
    
    
    /**
     * Print a string with a given level of granularity.
     * @param string The string
     * @param level The level (one of the predefined constants).
     * @throws java.io.IOException On failure.
     */
    public void print ( String string, int level ) throws java.io.IOException;
    
    /**
     *Closes the console after use.
     *
     *@exception java.io.IOException On failure.
     */
     
    public void close() throws java.io.IOException;
    
    /**
     * Set the overall granularity level of the console.
     * Messages printed with a higher level will be ignored.
     * @param level The level, one of the predefined constants. 
     * Default is WARN.
     */
    
    public void setLevel ( int level );
    
    /**
     * Gets the level of the console.
     * @return The log level.
     */
    public int getLevel();
    
    
    
}
