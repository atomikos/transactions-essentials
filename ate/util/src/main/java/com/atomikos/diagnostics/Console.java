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
