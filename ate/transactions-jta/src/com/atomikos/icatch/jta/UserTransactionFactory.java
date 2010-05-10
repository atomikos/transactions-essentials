//$Id: UserTransactionFactory.java,v 1.1.1.1.4.1 2007/05/05 12:32:21 guy Exp $
//$Log: UserTransactionFactory.java,v $
//Revision 1.1.1.1.4.1  2007/05/05 12:32:21  guy
//FIXED 10121
//
//Revision 1.1.1.1  2006/08/29 10:01:10  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:44  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:10  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.5  2005/08/05 15:03:41  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.4  2004/10/12 13:03:38  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.3  2004/10/12 08:25:07  guy
//Renamed J2EE UserTransaction class.
//
//Revision 1.2  2004/10/08 14:31:07  guy
//Optimized JTA classes for J2EE integration.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//

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
