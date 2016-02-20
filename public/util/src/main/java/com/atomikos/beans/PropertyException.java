/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.beans;

 /**
  *
  *
  *An exception indicating a failure to set or get a bean property.
  */

public class PropertyException
extends Exception
{
    private Throwable nested_;
    //the nested exception
    
    
   public PropertyException ( String msg ) {
	super ( msg );
 }
     /**
      *Creates a new instance with a nested exception.
      *@param nested The nested exception.
      */
      
    public PropertyException ( Throwable nested )
    {
        this ( null , nested );
    } 
    
    /**
      *Creates a new instance with a message and nested exception.
      *@param msg The message.
      *@param nested The nested exception.
      */
      
    public PropertyException ( String msg , Throwable nested )
    {
        super ( msg );
        nested_ = nested;
    }
    
    /**
     *Get the nested exception.
     *@return Exception The nested exception.
     */
    
    public Throwable getNestedException()
    {
        return nested_; 
    }
    
    public void printStackTrace()
    {
    	if ( nested_ != null ) nested_.printStackTrace();
    	super.printStackTrace();
    }
}
