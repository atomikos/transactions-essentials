package com.atomikos.jdbc;

import java.lang.reflect.InvocationHandler;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.atomikos.icatch.system.Configuration;
import com.atomikos.util.DynamicProxy;

/**
 * Common logic for the different dynamic connection proxies.
 *
 */

public abstract class AbstractConnectionProxy 
implements InvocationHandler
{
	
	private List statements = new ArrayList();

	protected synchronized void addStatement ( Statement s )
	{
		statements.add ( s );
	}
	
	protected synchronized void forceCloseAllPendingStatements ( boolean warn ) 
	{
		Iterator it = statements.iterator();
		while ( it.hasNext() ) {
			Statement s = ( Statement ) it.next();
			try {
				if ( warn ) Configuration.logWarning ( "Forcing close of pending statement: " + s );
				s.close();
			} catch ( Exception e ) {
				//ignore but log
				Configuration.logWarning ( "Error closing pending statement: " , e );
			}
			//cf case 31275: also remove statement from list!
			it.remove();
		}
	}

}
