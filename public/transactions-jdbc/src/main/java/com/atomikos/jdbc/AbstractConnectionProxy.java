/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc;

import java.lang.reflect.InvocationHandler;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * Common logic for the different dynamic connection proxies.
 *
 */

public abstract class AbstractConnectionProxy 
implements InvocationHandler
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AbstractConnectionProxy.class);
	
	private List<Statement> statements = new ArrayList<Statement>();

	protected synchronized void addStatement ( Statement s )
	{
		statements.add ( s );
	}
	
	protected synchronized void forceCloseAllPendingStatements ( boolean warn ) 
	{
		Iterator<Statement> it = statements.iterator();
		while ( it.hasNext() ) {
			Statement s =  it.next();
			try {
                // github issue 53: Don't force close already closed statements.
                if (!s.isClosed()) {
    				String msg = "Forcing close of pending statement: " + s;
    				if ( warn ) LOGGER.logWarning ( msg );
    				else LOGGER.logTrace ( msg );
    				s.close();
                }
			} catch ( Exception e ) {
				//ignore but log
				LOGGER.logWarning ( "Error closing pending statement: " , e );
			}
			//cf case 31275: also remove statement from list!
			it.remove();
		}
	}

    // github issue 53: Remove closed/completed statements from the pending statements list.
    protected synchronized void removeClosedStatements() {
        Iterator<Statement> it = this.statements.iterator();
        while (it.hasNext()) {
            Statement s = it.next();
            try {
                if (s.isClosed()) {
                    it.remove();
                }
            } catch (Exception e) {
                //ignore but log
                LOGGER.logWarning( "Error checking if statement can be removed: " , e );
            }
        }
    }

}
