/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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

package com.atomikos.datasource.xa;

import javax.transaction.xa.XAResource;

import com.atomikos.datasource.ResourceException;

/**
 *
 * This class is useful only for enlist via the JTA API. In particular, this class
 * is a 'workaround' for buggy XAResource implementations where isSameRM returns false
 * even if it should not. With the default automatic resource registration mode,
 * this situation would lead to another XATransactionalResource being added to the
 * configuration for each such enlist. As an alternative, this class pretends to
 * recognize <b>any</b> XAResource and can be used to avoid problems when
 * automatic registration is <b>disabled</b>: instead of reporting errors
 * claiming that the XAResource is unknown for faulty isSameRM cases,
 * this class will silently accept them.
 *
 *
 */
public class AcceptAllXATransactionalResource extends XATransactionalResource
{

    /**
     * @param servername
     */
    public AcceptAllXATransactionalResource ( String servername )
    {
        super ( servername );
        super.setAcceptAllXAResources ( true );
    }

    /**
     * @param servername
     * @param factory
     */
    public AcceptAllXATransactionalResource ( String servername ,
            XidFactory factory )
    {
        super ( servername , factory );
        super.setAcceptAllXAResources ( true );
    }

    /**
     * @see com.atomikos.datasource.xa.XATransactionalResource#refreshXAConnection()
     */
    protected XAResource refreshXAConnection () throws ResourceException
    {

        return null;
    }

    /**
     * Always returns true.
     */
    public boolean usesXAResource ( XAResource res )
    {
        // this is essential for XID mapping
        return true;
    }

    protected void recoverTheDeprecatedWay ()
    {
        // nothing to do
    }

    public void endRecovery ()
    {
        // nothing to do
    }



}
