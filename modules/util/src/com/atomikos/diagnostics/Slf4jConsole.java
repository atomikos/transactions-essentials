package com.atomikos.diagnostics;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 /**
  * 
  * A console that delegates to slf4j - this ensures
  * log4j compatibility. 
  * 
  * 
  *
  *
  */

public class Slf4jConsole implements Console 
{
	
	private static final Logger log =
		LoggerFactory.getLogger ( "atomikos" );

	private int level;
	
	public Slf4jConsole()
	{
		int level = Console.WARN;
		if (log.isInfoEnabled())
			level = Console.INFO;
		else if (log.isDebugEnabled())
			level = Console.DEBUG;
		setLevel(level);
	}

	public void close() throws IOException 
	{
		//nothing to do

	}

	/**
	 * The level. Note that this level
	 * is only returned for consistency with the
	 * API documentation; it is ignored by this
	 * console.
	 */
	public int getLevel() 
	{
		return level;
	}

	/**
	 * This method does nothing for SLF4J - printing without newline
	 * would be too expensive since it is not supported in SLF4J. 
	 */
	
	public void print ( String string ) throws IOException 
	{
		
	}

	/**
	 * This method does nothing for SLF4J - printing without newline
	 * would be too expensive since it is not supported in SLF4J. 
	 */
	
	public void print ( String string , int level ) throws IOException 
	{
		
	}

	public void println ( String string ) throws IOException 
	{
		println ( string , Console.WARN );
	}

	public void println ( String string , int level ) throws IOException 
	{
		switch ( level ) {
			case Console.WARN: 
				if (log.isWarnEnabled()) log.warn ( string );
				break;			
			case Console.INFO: 
				if (log.isInfoEnabled()) log.info ( string );
				break;
			case Console.DEBUG:
				if (log.isDebugEnabled()) log.debug ( string );
				break;	
			default: break;
		}
	}

	/**
	 * Setting the level has no filtering effect for this class: the output level is determined
	 * by the underlying logging tool's separate configuration. However,
	 * we still maintain the level to make sure the CascadedConsole works.
	 */
	public void setLevel ( int level ) 
	{
		this.level = level;
	}

}
