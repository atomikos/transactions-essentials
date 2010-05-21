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
