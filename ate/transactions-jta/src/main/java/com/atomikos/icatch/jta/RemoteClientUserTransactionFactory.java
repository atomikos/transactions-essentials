//$Id: RemoteClientUserTransactionFactory.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//$Log: RemoteClientUserTransactionFactory.java,v $
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
//Revision 1.5  2005/08/09 15:23:58  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.4  2004/10/11 13:39:37  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: RemoteClientUserTransactionFactory.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.3  2003/03/26 19:35:48  guy
//$Id: RemoteClientUserTransactionFactory.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Added preservation of timeout setting across JNDI store and lookup.
//$Id: RemoteClientUserTransactionFactory.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//$Id: RemoteClientUserTransactionFactory.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.2  2003/03/11 06:39:01  guy
//$Id: RemoteClientUserTransactionFactory.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: RemoteClientUserTransactionFactory.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//Revision 1.1.2.3  2003/01/29 17:19:46  guy
//Adapted to use JNDI binding instead of Naming of RMI.
//
//Revision 1.1.2.2  2002/11/17 18:36:13  guy
//Corrected JNDI factory mechanism.
//
//Revision 1.1.2.1  2002/11/14 16:33:42  guy
//Added support for remote usertxs.
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
 * The factory for JNDI lookup of RemoteClientUserTransactionFactory objects.
 */

public class RemoteClientUserTransactionFactory implements ObjectFactory
{
    private String url_;

    // the RMI url of the server

    public RemoteClientUserTransactionFactory ()
    {
    }

    /**
     * @see javax.naming.spi.ObjectFactory
     */

    public Object getObjectInstance ( Object obj , Name name , Context nameCtx ,
            Hashtable environment ) throws Exception
    {
        RemoteClientUserTransaction ret = null;

        if ( obj == null || !(obj instanceof Reference) )
            return null;

        Reference ref = (Reference) obj;
        if ( !ref.getClassName ().equals (
                "com.atomikos.icatch.jta.RemoteClientUserTransaction" ) )
            return null;
        // as required by JNDI
        String jndiName = (String) ref.get ( "ServerName" ).getContent ();
        String url = (String) ref.get ( "ProviderUrl" ).getContent ();
        String initialFactory = (String) ref.get ( "ContextFactory" )
                .getContent ();
        String timeoutString = (String) ref.get ( "Timeout" ).getContent ();
        int timeout = Integer.parseInt ( timeoutString );

        ret = new RemoteClientUserTransaction ( jndiName, initialFactory, url );
        ret.setTransactionTimeout ( timeout );
        return ret;
    }

}
