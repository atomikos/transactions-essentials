package com.atomikos.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * 
 * 
 * 
 * A filter class for main static methods (application entry points).
 * 
 * 
 *
 * 
 */

public class MainMethodFilter
implements MemberFilter
{

    public boolean acceptsField(Field field)
    {
        
        return false;
    }

  
    public boolean acceptsMethod(Method method)
    {
     
     	//System.out.println ( "Checking main");
     	//System.out.println ( method.getName());
        if ( !method.getName().endsWith ( "main" ) ) return false;
		//System.out.println ( "Checking pars");
        Class[] pars = method.getParameterTypes();
		//System.out.println ( "Checking par length");
        if ( pars.length != 1 ) return false;
		//System.out.println ( "Checking par type");
        if ( !pars[0].getName().startsWith ( "[Ljava.lang.String")) return false;
        int mod = method.getModifiers();
		//System.out.println ( "Checking public");
        if ( ! Modifier.isPublic(mod) ) return false;
		//System.out.println ( "Checking static");
        if ( ! Modifier.isStatic(mod )) return false;
		//System.out.println ( "Checking return type");
		//System.out.println ( method.getReturnType().getName() );
        if ( ! method.getReturnType().getName().equals ( "void")) return false;
        return true;
    }

   
    public boolean acceptsConstructor(Constructor constructor)
    {
        return false;
    }


    
}
