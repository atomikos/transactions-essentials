package com.atomikos.datasource;

import java.util.Enumeration;
import java.util.Stack;

/**
*
*
*<p>
*Exception on the level of the resource manager.
*Contains more detailed info of actual protocol's exception.
*/

public class ResourceException extends com.atomikos.icatch.SysException{
    //private Stack errors_=null;
    
    public ResourceException(String msg){
        super(msg);
        
    }
    
    public ResourceException(String msg, Stack errors)
    {
        //this (msg);
//        errors_=(Stack) errors.clone();
        super ( msg , errors );
    }
    
    public ResourceException(Stack errors){
        
        super ( "ResourceException" , errors );
    }
    
    //TODO rename to getErrors (overrides SysException)
    //TODO add method printToConsole
    public Stack getDetails(){
      return getErrors();
    }
    
    public void printStackTrace()
    {
    	super.printStackTrace();
    	if ( ! ( getDetails() == null || getDetails().empty())) {
    		Enumeration enumm = getDetails().elements();
    		while ( enumm.hasMoreElements() ) {
    			Exception next = ( Exception ) enumm.nextElement();
    			next.printStackTrace();
    		}
    	}
    }
}

