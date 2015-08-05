package com.atomikos.util;

import java.io.Serializable;

import javax.naming.Reference;

/**
 * 
 * 
 * 
 * 
 *
 * 
 */

public class SerializableObjectFactoryTester
{
	

    public static void main(String[] args)
    throws Exception
    {
    	String name = "name";
    	int value = 12;
    	int transientValue = 3;
    	
    	TestBean bean = new TestBean();
    	bean.setName ( name );
    	bean.setValue ( value );
    	bean.setTransientValue ( transientValue );
    	Reference ref = 
    		SerializableObjectFactory.createReference ( bean );
    	SerializableObjectFactory fact = new SerializableObjectFactory();
    	
    	bean = ( TestBean ) fact.getObjectInstance (
    		ref , null , null , null );
    	if ( ! bean.getName().equals ( name ) )
    		throw new Exception ( "getName failure");
    	if ( bean.getValue() != value )
    		throw new Exception ( "getValue failure");
    	if ( bean.getTransientValue() == transientValue )
    		throw new Exception ( "getTransientValue failure");
    }
    
   	static class TestBean implements Serializable
   	{
   		private String name;
   		
   		private int value;
   		
   		private transient int transientValue;
   		
   		TestBean()
   		{
   			name = "";
   			value = 0;
   			transientValue = 0;
   		}
   		
   		
        /**
         * @return
         */
        public String getName()
        {
            return name;
        }

        /**
         * @return
         */
        public int getTransientValue()
        {
            return transientValue;
        }

        /**
         * @return
         */
        public int getValue()
        {
            return value;
        }

        /**
         * @param string
         */
        public void setName(String string)
        {
            name = string;
        }

        /**
         * @param i
         */
        public void setTransientValue(int i)
        {
            transientValue = i;
        }

        /**
         * @param i
         */
        public void setValue(int i)
        {
            value = i;
        }

   	}
}
