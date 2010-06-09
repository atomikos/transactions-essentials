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
