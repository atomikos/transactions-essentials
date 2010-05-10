//$Id: SimpleDataSourceBeanBeanInfo.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//$Log: SimpleDataSourceBeanBeanInfo.java,v $
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
//Revision 1.2  2006/03/15 10:32:01  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:14  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2005/08/09 15:25:06  guy
//Updated javadoc.
//
//Revision 1.1  2004/09/28 11:27:40  guy
//Added classes for Websphere integration.
//
//Revision 1.2  2004/03/22 15:39:16  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.6  2004/02/16 09:37:16  guy
//Added a validating query facility.
//
//Revision 1.1.2.5  2003/11/16 09:03:35  guy
//Updated name of XA DataSource property to follow JDBC conventions.
//
//Revision 1.1.2.4  2003/10/23 15:20:07  guy
//Added shutdown hook for closing the data source.
//Added bean properties for JNDI/XA name configuration.
//
//Revision 1.1.2.3  2003/08/21 20:31:51  guy
//*** empty log message ***
//
//Revision 1.1.2.2  2003/05/18 09:43:15  guy
//Made xid factory a list property, and added an editor for this.
//
//Revision 1.1.2.1  2003/05/15 15:26:46  guy
//Added JavaBean compliant data source for GUI setup.
//

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