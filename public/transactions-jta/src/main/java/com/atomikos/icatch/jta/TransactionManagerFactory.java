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

package com.atomikos.icatch.jta;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 *
 *
 *
 *
 *
 * An Object factory for the Transaction Manager.
 */

public class TransactionManagerFactory implements ObjectFactory
{

    /**
     *
     */

    public TransactionManagerFactory ()
    {
        super ();

    }

    /**
     * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object,
     *      javax.naming.Name, javax.naming.Context, java.util.Hashtable)
     */

    public Object getObjectInstance ( Object obj , Name reg , Context arg2 ,
            Hashtable arg3 ) throws Exception
    {
        Object ret = null;
        if ( obj == null || !(obj instanceof Reference) )
            return null;

        Reference ref = (Reference) obj;
        if ( ref.getClassName ().equals (
                "com.atomikos.icatch.jta.TransactionManagerImp" ) )
            ret = TransactionManagerImp.getTransactionManager ();
        else if ( ref.getClassName ().equals (
                "com.atomikos.icatch.jta.J2eeTransactionManager" ) )
            ret = new J2eeTransactionManager ();
        else
            ret = null;

        return ret;
    }

}
