package com.atomikos.beans;

 /**
  *
  *
  *An exception for signalling an attempt to use an
  *unsupported set operation.
  */

public class ReadOnlyException 
extends Exception
{
    public ReadOnlyException () 
    {
        super(); 
    }
    
    public ReadOnlyException ( String msg )
    {
        super ( msg ); 
    }
}
