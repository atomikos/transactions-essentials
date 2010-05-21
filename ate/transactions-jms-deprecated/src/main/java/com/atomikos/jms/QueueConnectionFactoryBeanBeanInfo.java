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
