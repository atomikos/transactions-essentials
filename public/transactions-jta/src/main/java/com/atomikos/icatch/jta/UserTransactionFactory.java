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

package com.atomikos.icatch.jta;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 *
 *
 * The factory for JNDI lookup of UserTransactionImp objects.
 */

public class UserTransactionFactory implements ObjectFactory
{

    public UserTransactionFactory ()
    {
    }

    /**
     * @see javax.naming.spi.ObjectFactory
     */

    public Object getObjectInstance ( Object obj , Name name , Context nameCtx ,
            Hashtable environment ) throws Exception
    {
        Object ret = null;
        if ( obj == null || !(obj instanceof Reference) )
            return null;

        Reference ref = (Reference) obj;
        if ( ref.getClassName ().equals (
                "com.atomikos.icatch.jta.UserTransactionImp" ) )
            ret = new UserTransactionImp ();
        else if ( ref.getClassName ().equals (
                "com.atomikos.icatch.jta.J2eeUserTransaction" ) )
            ret = new J2eeUserTransaction ();
        else if ( ref.getClassName().equals (
        		   "javax.transaction.UserTransaction" ) )
        		//ISSUE 10121: fix for Tomcat 5.5: class is always the JTA type
        		ret = new UserTransactionImp();
        else
            ret = null;

        return ret;

    }

}
