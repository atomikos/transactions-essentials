//$Id: TestXATransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//$Log: TestXATransactionalResource.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:13  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:55  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:17  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2004/10/12 13:04:59  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: TestXATransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.2  2003/03/11 06:43:07  guy
//$Id: TestXATransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: TestXATransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//Revision 1.1.2.1  2002/08/29 07:25:15  guy
//Adapted to new paradigm: XATransactionalResource is abstract in  order
//to refresh the XAResource if it times out.
//


package com.atomikos.datasource.xa;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.ResourceException;

 /**
  *
  *
  *A test impl. of XATransactionalResource that works with the
  *XAResource stubs.
  */
  
public class TestXATransactionalResource
extends XATransactionalResource
{
  
    private XAResource xares_;
    
    public TestXATransactionalResource ( XAResource xares , String name )
    {
        super ( name );
        xares_ = xares;
    } 
    
    protected XAResource refreshXAConnection()
    throws ResourceException
    {
        return xares_; 
    }
}
