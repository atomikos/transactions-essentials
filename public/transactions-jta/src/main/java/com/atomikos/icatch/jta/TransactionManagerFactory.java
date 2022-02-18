/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
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

    @SuppressWarnings("rawtypes")
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
