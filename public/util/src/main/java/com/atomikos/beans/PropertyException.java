/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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
