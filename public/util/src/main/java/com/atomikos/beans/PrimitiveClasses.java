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

package com.atomikos.beans;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


 /**
  *
  *
  *A utility class for primitive class manipulation.
  */

public final class PrimitiveClasses
{
    private static Map map_ = new HashMap();
    //maps primitive classes to wrapper classes

    private static Map numericMap_ = new HashMap();
    //contains a map for a numeric primitive class
    //the mapped value is a Boolean: True iff the
    //class allows decimal values

   

    static {
        map_.put ( Boolean.TYPE , Boolean.class );
        map_.put ( Character.TYPE , Character.class );
        map_.put ( Short.TYPE , Short.class );
        map_.put ( Integer.TYPE , Integer.class );
        map_.put ( Long.TYPE , Long.class );
        map_.put ( Float.TYPE , Float.class );
        map_.put ( Double.TYPE , Double.class );
        map_.put ( Byte.TYPE , Byte.class );
        map_.put ( Void.TYPE , Void.class );

        numericMap_.put ( Short.class , new Boolean ( false ) );
        numericMap_.put ( Integer.class , new Boolean ( false ) );
        numericMap_.put ( Long.class , new Boolean ( false ) );
        numericMap_.put ( Float.class , new Boolean ( true ) );
        numericMap_.put ( Double.class , new Boolean ( true ) );
        
    }


    /**
     *Check if a class should be restricted to numeric values.
     *@param clazz The class to test.
     *@return boolean If true then the class allows only numeric values.
     */
    
    public static boolean isNumericClass ( Class clazz )
    {
        return numericMap_.containsKey ( clazz );
    }

    /**
     *Check if a class allows decimal numeric values.
     *@param clazz The class to test.
     *@return boolean If true then the class allows decimal numeric values.
     */

    public static boolean isDecimalClass ( Class clazz )
    {
        boolean ret = false;

        Boolean value = ( Boolean ) numericMap_.get ( clazz );
        if ( value != null ) ret = value.booleanValue();

        return ret;
    }
    
    /**
      *Get the wrapper type for the given primitive.
      *@param primitiveClass The primitive class.
      *@return Class The wrapper, or null if not a primitive.
      */
      
    public static Class getWrapperClass ( Class primitiveClass )
    {
          return ( Class ) map_.get ( primitiveClass );
    }
    
     /**
      *Constructs a wrapper object that holds a value
      *for the given primitiveClass, based on the
      *given String value.
      *@param val The String value.
      *@param primitiveClass The class for which the 
      *wrapper object should be made.
      *@exception ClassNotPrimitiveException If 
      *the supplied class is not recognized as a primitive class.
      *@return Object The wrapper object.
      */
    public static Object createWrapperObject ( String val , Class primitiveClass )
    throws ClassNotPrimitiveException
    {
         Object ret = null;
       
        Class[] args = new Class[1];
        args[0] = String.class;
        Class wrapperClass = getWrapperClass ( primitiveClass );
        if ( wrapperClass == null )
            throw new ClassNotPrimitiveException ( primitiveClass.getName() );
        
        try {
          Constructor c = wrapperClass.getConstructor ( args );
          Object[] initArgs = new Object[1];
          initArgs[0] = val;
          if  ( c != null )
            ret  = c.newInstance ( initArgs );
        }
        catch ( Exception e ) {
            //e.printStackTrace();
            throw new RuntimeException ( e.getMessage () ); 
        }
       
        return ret;
    }
}
