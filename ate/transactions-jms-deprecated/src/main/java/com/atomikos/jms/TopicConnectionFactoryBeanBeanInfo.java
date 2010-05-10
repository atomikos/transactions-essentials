package com.atomikos.jms;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * 
 * 
 * Bean info class for topic connection factories.
 * 
 * <p>
 * Topic functionality in this product was sponsored by <a href="http://www.webtide.com">Webtide</a>.
 *
 */
public class TopicConnectionFactoryBeanBeanInfo extends SimpleBeanInfo 
{
	
	  public PropertyDescriptor[] getPropertyDescriptors ()
	    {

	        PropertyDescriptor[] ret = new PropertyDescriptor[3];

	        try {
	            Class clazz = TopicConnectionFactoryBean.class;

	            ret[0] = new PropertyDescriptor ( "resourceName", clazz );
	            ret[0]
	                    .setShortDescription ( "give this source a GLOBALLY UNIQUE name" );

	            ret[1] = new PropertyDescriptor ( "xaFactoryJndiName", clazz );
	            ret[1].setShortDescription ( "JNDI name of XA factory" );
	            ret[1].setHidden ( false );
	            
	            ret[2] = new PropertyDescriptor ( "xaTopicConnectionFactory", clazz );
	            ret[2].setHidden ( true );

	        } catch ( Exception e ) {
	            throw new RuntimeException ( e.getMessage () );
	        }
	        return ret;
	    }

}
