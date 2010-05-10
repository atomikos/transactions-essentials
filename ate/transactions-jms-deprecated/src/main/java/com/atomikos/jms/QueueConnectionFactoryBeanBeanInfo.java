//$Id: QueueConnectionFactoryBeanBeanInfo.java,v 1.2 2006/10/30 10:37:10 guy Exp $
//$Log: QueueConnectionFactoryBeanBeanInfo.java,v $
//Revision 1.2  2006/10/30 10:37:10  guy
//Merged in changes of 3.1.0 release
//
//Revision 1.1.1.1.4.1  2006/10/20 07:03:13  guy
//Completed JMS 1.1 support
//
//Revision 1.1.1.1  2006/08/29 10:01:13  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:32  guy
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
//Revision 1.3  2005/08/09 15:25:21  guy
//Updated javadoc.
//
//Revision 1.2  2005/05/13 14:53:18  guy
//Corrected/added descriptor for xaQueueConnectionFactory property.
//
//Revision 1.1.1.1  2004/09/18 12:42:50  guy
//Added separate JMS module.
//
//Revision 1.2  2004/03/22 15:39:38  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.4  2003/10/23 15:20:24  guy
//Added bean property for JNDI XA name configuration.
//
//Revision 1.1.2.3  2003/08/21 20:31:58  guy
//redesign
//
//Revision 1.1.2.2  2003/05/22 06:32:59  guy
//Completed bean support.
//
//Revision 1.1.2.1  2003/05/18 09:43:35  guy
//Added JNDI support and bean config support.
//

package com.atomikos.jms;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * 
 * 
 * A bean descriptor that tells GUI wizards which properties to edit.
 */

public class QueueConnectionFactoryBeanBeanInfo extends SimpleBeanInfo
{
    public PropertyDescriptor[] getPropertyDescriptors ()
    {

        PropertyDescriptor[] ret = new PropertyDescriptor[3];

        try {
            Class clazz = QueueConnectionFactoryBean.class;

            // ret[0] = new PropertyDescriptor ( "xaQueueConnectionFactoryName"
            // , clazz );
            // ret[0].setShortDescription ( "the XA factory instance to use" );

            ret[0] = new PropertyDescriptor ( "resourceName", clazz );
            ret[0]
                    .setShortDescription ( "give this source a GLOBALLY UNIQUE name" );


            ret[1] = new PropertyDescriptor ( "xaFactoryJndiName", clazz );
            ret[1].setShortDescription ( "JNDI name of XA factory" );
            ret[1].setHidden ( false );
            
            ret[2] = new PropertyDescriptor ( "xaQueueConnectionFactory", clazz );
            ret[2].setHidden ( true );

        } catch ( Exception e ) {
            throw new RuntimeException ( e.getMessage () );
        }
        return ret;
    }

}