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
