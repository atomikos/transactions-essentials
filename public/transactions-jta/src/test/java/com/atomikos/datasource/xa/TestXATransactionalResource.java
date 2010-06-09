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
