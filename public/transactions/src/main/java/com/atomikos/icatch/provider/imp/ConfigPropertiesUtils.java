package com.atomikos.icatch.provider.imp;

import java.util.Properties;

class ConfigPropertiesUtils {

	/**
     * Replace ${...} sequence with the referenced value from the given properties or 
     * (if not found) the system properties -
     * contributed through Marian Kelc (marian.kelc@eplus.de)
     * E-Plus Mobilfunk GmbH &amp; Co. KG, Germany
     */
     private static String evaluateReference ( String value , Properties properties )
     {
         String result = value;
         //by default, the value as-is is returned
         
         int startIndex = value.indexOf ( '$' );
         if ( startIndex > -1 && value.charAt ( startIndex +1 ) == '{') {
        	 	//at least one reference is found
             int endIndex = value.indexOf ( '}' );
             if ( startIndex + 2 == endIndex )
                 throw new IllegalArgumentException ( "property ref cannot refer to an empty name: ${}" );
             if ( endIndex == -1 )
                 throw new IllegalArgumentException ( "unclosed property ref: ${" + value.substring ( startIndex + 2 ) );

             //strip-off reference characters -> get the referenced property name 
             String subPropertyKey = value.substring ( startIndex + 2, endIndex );
             //the properties take precedence -> try them first
             String subPropertyValue = properties.getProperty ( subPropertyKey );
            	if ( subPropertyValue == null ) {
            		//not found in properties -> try system property
            		subPropertyValue = System.getProperty ( subPropertyKey );
            	}
             
             if ( subPropertyValue != null ) {
            	    //in-line refs supported - result is prefix + value + suffix !!!
                 result = result.substring ( 0, startIndex ) + subPropertyValue + result.substring ( endIndex +1 );
                 //two or more refs supported - evaluate any remaining references in the value
                 result =  evaluateReference ( result , properties );
             }
             else {
            	 	//referenced value not found -> ignore any other references and return value as-is
            	    //NOTE: trying to resolve further references would lead to infinite recursion
             }
            	 
         }
         
         return result;
     }
 
 	 static void substitutePlaceHolderValues(Properties p) {
 		 //resolve referenced values with ant-like ${...} syntax
          java.util.Enumeration allProps= p.propertyNames();
          while ( allProps.hasMoreElements() ) {
              String key = ( String ) allProps.nextElement();
              String raw = p.getProperty ( key );
              String value= evaluateReference ( raw , p );
              if ( !raw.equals ( value ) ) {
                p.setProperty ( key, value );
              }
          }
	}
	
}
