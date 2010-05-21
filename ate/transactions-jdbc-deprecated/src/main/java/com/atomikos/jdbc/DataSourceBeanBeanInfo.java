package com.atomikos.jdbc;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


/**
 * 
 * 
 * A bean descriptor that tells GUI wizards which properties to edit.
 */

public class DataSourceBeanBeanInfo extends SimpleBeanInfo
{

    public PropertyDescriptor[] getPropertyDescriptors ()
    {

        PropertyDescriptor[] ret = new PropertyDescriptor[9];

        try {
            PropertyDescriptor pd = null;
            Class clazz = DataSourceBean.class;

            ret[0] = new PropertyDescriptor ( "xaDataSource", clazz );
            ret[0].setShortDescription ( "the XADataSource instance to use" );
            
            ret[1] = new PropertyDescriptor ( "uniqueResourceName", clazz );
            ret[1].setShortDescription ( "give this source a UNIQUE name" );

            ret[2] = new PropertyDescriptor ( "xidFormat", clazz );
            ret[2].setShortDescription ( "the XID format to use" );
            ret[2].setPropertyEditorClass ( XidFactoryEditor.class );

            ret[3] = new PropertyDescriptor ( "connectionPoolSize", clazz );
            ret[3].setShortDescription ( "the size of the pool" );
            
            ret[4] = new PropertyDescriptor ( "connectionTimeout", clazz );
            ret[4].setShortDescription ( "liveness check by pool (in seconds)" );
            
            ret[5] = new PropertyDescriptor ( "exclusiveConnectionMode", clazz );
            ret[5].setShortDescription ( "connections reusable only AFTER 2PC" );
            
            ret[6] = new PropertyDescriptor ( "dataSourceName", clazz );
            ret[6]
                    .setShortDescription ( "optional name of XaDataSource in JNDI" );
            ret[6].setHidden ( true );
            
            ret[7] = new PropertyDescriptor ( "validatingQuery", clazz );
            ret[7]
                    .setShortDescription ( "a SQL query to validate the settings" );
            
            ret[8] = new PropertyDescriptor ( "testOnBorrow", clazz );
            ret[8].setShortDescription ( "test connections before use?" );
            
        } catch ( Exception e ) {
            throw new RuntimeException ( e.getMessage () );
        }
        return ret;
    }

}
