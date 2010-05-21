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

package com.atomikos.datasource.xa.jca;

import java.util.Stack;

import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.datasource.xa.XidFactory;
import com.atomikos.diagnostics.Console;

/**
 * 
 * 
 * 
 * 
 * 
 * A TransactionalResource that works with the JCA architecture.
 * 
 */

public class JcaTransactionalResource extends XATransactionalResource
{

    private ManagedConnectionFactory mcf;

    private ManagedConnection connection;

    public JcaTransactionalResource ( String servername ,
            ManagedConnectionFactory mcf )
    {
        super ( servername );
        this.mcf = mcf;

    }

    public JcaTransactionalResource ( String servername ,
            ManagedConnectionFactory mcf , XidFactory xidFactory )
    {
        super ( servername , xidFactory );
        this.mcf = mcf;

    }

    public boolean usesXAResource ( XAResource xares )
    {
        // used for XID creation; return false
        // since this task is taken over by a dedicated
        // resource. otherwise, deadlock occurs on the
        // first enlist of an XAResource that comes
        // from a pool limited to size 1!!!
        return false;
    }

    /*
     * @see com.atomikos.datasource.xa.XATransactionalResource#refreshXAConnection()
     */
    protected XAResource refreshXAConnection () throws ResourceException
    {
        printMsg ( "refreshXAConnection() for resource: " + getName (),
                Console.INFO );
        XAResource ret = null;
        if ( connection != null ) {
            try {
                connection.destroy ();
            } catch ( Exception normal ) {
                // this can be expected, since this method is only called
                // if there is a connection problem
            }

        }

        try {
            printMsg ( "about to block for new connection...", Console.INFO );
            // System.out.println ( "ABOUT TO BLOCK FOR NEW CONNECTION");
            connection = mcf.createManagedConnection ( null, null );

        } catch ( javax.resource.ResourceException e ) {
            // ignore and return null: happens if resource is down
            // at this moment
            connection = null;
        } finally {
            // System.out.println ( "BLOCKING DONE");
            printMsg ( "blocking done.", Console.INFO );
        }

        try {
            if ( connection != null )
                ret = connection.getXAResource ();
        } catch ( javax.resource.ResourceException e ) {
            printMsg ( "error getting XAResource: " + e.getMessage (),
                    Console.WARN );
            Stack errors = new Stack ();
            errors.push ( e );
            throw new ResourceException ( "Error in getting XA resource",
                    errors );
        }
        printMsg ( "refreshXAConnection() done.", Console.INFO );
        // System.out.println ( "DONE REFRESHXACONNECTION FOR RESOURCE: " +
        // getName() );

        return ret;
    }

    /**
     * Overrides default close to include closing any open connections to the
     * source.
     */

    public void close () throws ResourceException
    {
        super.close ();
        try {
            if ( connection != null )
                connection.destroy ();
        } catch ( Exception err ) {
            // throw new ResourceException ( err.getMessage() );
            // exception REMOVED because it close clashes
            // with shutdown hooks (in which the order of TS and
            // DS shutdown is unpredictable)
        }
    }

}
