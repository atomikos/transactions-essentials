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

package com.atomikos.jdbc;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


/**
 * 
 * 
 * A bean descriptor that tells GUI wizards which properties to edit.
 */

public class SimpleDataSourceBeanBeanInfo extends SimpleBeanInfo
{

    public PropertyDescriptor[] getPropertyDescriptors ()
    {

        PropertyDescriptor[] ret = new PropertyDescriptor[8];

        try {
            PropertyDescriptor pd = null;
            Class clazz = SimpleDataSourceBean.class;

            ret[0] = new PropertyDescriptor ( "xaDataSourceProperties", clazz );
            ret[0]
                    .setShortDescription ( "semicolon-separated list of name=value pairs" );

            ret[1] = new PropertyDescriptor ( "uniqueResourceName", clazz );
            ret[1].setShortDescription ( "identifying name used for logging" );

            ret[2] = new PropertyDescriptor ( "xaDataSourceClassName", clazz );
            ret[2]
                    .setShortDescription ( "full classname of vendor-specific XADataSource" );
            ret[2].setPropertyEditorClass ( XidFactoryEditor.class );

            ret[3] = new PropertyDescriptor ( "connectionPoolSize", clazz );
            ret[3].setShortDescription ( "size of the internal pool" );
            ret[4] = new PropertyDescriptor ( "connectionTimeout", clazz );
            ret[4].setShortDescription ( "liveness check by pool (in seconds)" );
            ret[5] = new PropertyDescriptor ( "validatingQuery", clazz );
            ret[5]
                    .setShortDescription ( "optional SQL query to validate the settings" );
            ret[6] = new PropertyDescriptor ( "exclusiveConnectionMode", clazz );
            ret[6]
                    .setShortDescription ( "don't share connections within a transaction" );
            ret[7] = new PropertyDescriptor ( "testOnBorrow", clazz );
            ret[7].setShortDescription ( "test connections when gotten?" );

        } catch ( Exception e ) {
            throw new RuntimeException ( e.getMessage () );
        }
        return ret;
    }

}
