//$Id: JndiObjectFactory.java,v 1.2 2006/10/30 10:37:09 guy Exp $
//$Log: JndiObjectFactory.java,v $
//Revision 1.2  2006/10/30 10:37:09  guy
//Merged in changes of 3.1.0 release
//
//Revision 1.1.1.1.4.1  2006/10/13 13:07:03  guy
//ADDED 1010
//
//Revision 1.1.1.1  2006/08/29 10:01:12  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:31  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:32:05  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:15  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2005/08/09 15:25:21  guy
//Updated javadoc.
//
//Revision 1.1.1.1  2004/09/18 12:42:50  guy
//Added separate JMS module.
//
//Revision 1.2  2004/03/22 15:39:38  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.1  2003/05/18 09:43:35  guy
//Added JNDI support and bean config support.
//

package com.atomikos.jms;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * 
 * 
 * An object factory for JMS administrated objects.
 */

public class JndiObjectFactory implements ObjectFactory
{
    public JndiObjectFactory ()
    {
    }

    public Object getObjectInstance ( Object obj , Name name , Context nameCtx ,
            Hashtable environment ) throws Exception
    {
    		Object ret = null;
        if ( !(obj instanceof Reference) )
            return null;
        
        Reference ref = (Reference) obj;
        String className = ref.getClassName();
        if ( JtaQueueConnectionFactory.class.getName ().equals ( className ) ) {
        		String url = (String) ref.get ( "ResourceName" ).getContent ();
        		ret = JtaQueueConnectionFactory.getInstance ( url );
        }   
        else if ( JtaTopicConnectionFactory.class.getName ().equals ( className ) ) {
        		String url = (String) ref.get ( "ResourceName" ).getContent ();
        		ret = JtaTopicConnectionFactory.getInstance ( url );
        }
        else {
        		ret = null;
        		//as required by JNDI
        }
        
        return ret;
    }

}