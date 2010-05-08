package com.atomikos.jdbc.nonxa;

import java.beans.PropertyDescriptor;

/**
 * 
 * 
 * 
 * 
 * 
 * 
 */
public class NonXADataSourceBeanBeanInfo extends java.beans.SimpleBeanInfo
{
    public PropertyDescriptor[] getPropertyDescriptors ()
    {

        PropertyDescriptor[] ret = new PropertyDescriptor[9];

        try {
            PropertyDescriptor pd = null;
            Class clazz = NonXADataSourceBean.class;

            ret[0] = new PropertyDescriptor ( "connectionTimeout", clazz );
            ret[0].setShortDescription ( "refresh interval for pool (in secs)" );
            ret[1] = new PropertyDescriptor ( "uniqueResourceName", clazz );
            ret[1].setShortDescription ( "give this source a UNIQUE name" );

            ret[2] = new PropertyDescriptor ( "url", clazz );
            ret[2].setShortDescription ( "the connect URL to use" );

            ret[3] = new PropertyDescriptor ( "poolSize", clazz );
            ret[3].setShortDescription ( "the size of the pool" );
            ret[4] = new PropertyDescriptor ( "user", clazz );
            ret[4].setShortDescription ( "the username to connect with" );
            ret[5] = new PropertyDescriptor ( "password", clazz );
            ret[5].setShortDescription ( "the password for the user" );
            ret[6] = new PropertyDescriptor ( "driverClassName", clazz );
            ret[6].setShortDescription ( "the name of the driver class" );
            ret[7] = new PropertyDescriptor ( "validatingQuery", clazz );
            ret[7]
                    .setShortDescription ( "a SQL query to validate the settings" );
            ret[8] = new PropertyDescriptor ( "testOnBorrow" , clazz );
            ret[8].setShortDescription ( "test connections before use?" );
            
        } catch ( Exception e ) {
            throw new RuntimeException ( e.getMessage () );
        }
        return ret;
    }
}
