package com.atomikos.beans;

 /**
  *
  *
  *An exception for the conversion of primitive values to 
  *their wrapper objects. Thrown if the associated
  *primitive class is not really a primitive class.
  */

public class ClassNotPrimitiveException 
extends Exception
{
    public ClassNotPrimitiveException()
    {
        super(); 
    } 
    
    public ClassNotPrimitiveException ( String msg )
    {
          super ( msg );
    }
}
