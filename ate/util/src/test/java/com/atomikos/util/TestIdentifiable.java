//$Id: TestIdentifiable.java,v 1.1.1.1 2006/08/29 10:01:16 guy Exp $
//$Log: TestIdentifiable.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:16  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:51  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:41  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:04  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:46  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1.1.1  2001/10/05 13:23:58  guy
//Utilities module
//

package com.atomikos.util;

public class TestIdentifiable implements Identifiable
{
    protected Object id_ ;

    public TestIdentifiable()
    {
        id_ = new Object();
    }

    public Object getId()
    {
        return id_;
    }
}
