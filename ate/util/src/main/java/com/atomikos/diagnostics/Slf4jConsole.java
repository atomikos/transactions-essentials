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
