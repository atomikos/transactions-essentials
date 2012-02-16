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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atomikos.util.ClassLoadingHelper;

/**
 *
 *
 *
 *
 *
 * A wizard class for HTML platforms. An instance can be used to
 * configure a bean in an HTML browser. Indexed properties are
 * NOT supported.
 */

public class HtmlBeanWizard
{

	private static final String PAR_NAME_PREFIX = "com.atomikos.beans.property.";
	//unique prefix to ensure that properties are represented by
	//unique parameter names in HTML requests

	private static final String BOOLEAN_PREFIX = "boolean.";
	//indicates which parameter correspond to boolean properties
	//and are therefore represented by a checkbox in the form

	private static Property[] filterProperties ( Property[] props )
	throws PropertyException
	{
		if ( props == null ) return null;

		ArrayList list = new ArrayList();

		for ( int i = 0 ; i < props.length ; i++ ) {
			if ( ! (  props[i].isHidden() ||
					  props[i].isReadOnly() ||
					  props[i].getEditor() == null ||
					  props[i].getIndexedProperty() != null ) )
				list.add ( props[i] );

		}
		return ( Property[] ) list.toArray ( new Property[0] );
	}


	private BeanInspector inspector;

	private Map parsedProperties;


	public HtmlBeanWizard ()
	{
		parsedProperties = new HashMap();
	}

	/**
	 * Set the class name of the underlying bean.
	 * This method should be called before using the instance.
	 * @param name
	 * @throws ClassNotFoundException If the class is not found.
	 *
	 */

	public void setClassName ( String name )
	throws ClassNotFoundException
	{
		Class clazz = ClassLoadingHelper.loadClass ( name );

		Object bean = null;
		try {
			bean = clazz.newInstance();
		}
		catch ( Exception e ) {
			throw new RuntimeException ( e.getMessage() );
		}
		inspector = new BeanInspector ( bean );

	}

	public String[] getPropertyNames()
	throws PropertyException
	{
		Collection names = new ArrayList();
		String[] template = new String[0];
		//do nothing if not inited
		if ( inspector == null ) return template;

		Property[] props = filterProperties ( inspector.getProperties() );
		for ( int i = 0 ; i < props.length ; i++ ) {
			names.add( props[i].getName() );
		}
		return ( String[] ) names.toArray ( template );
	}

	public Object getPropertyValue ( String propertyName )
	throws PropertyException
	{
		Object ret = null;

		if ( parsedProperties.containsKey ( propertyName ) ) {
			//last parse has priority over what we set before,
			//otherwise we can never set read-only properties
			//in the HTML client (who gets the values this way)
			ret = parsedProperties.get ( propertyName );
		}
		else if ( inspector != null ) {
			Property p = inspector.getProperty ( propertyName );
			try {

				ret = p.getValue();
			}
			catch ( PropertyException readOnly ) {
				//ignore: return null
			}
		}
		return ret;
	}

	/**
	 * Include the description of the properties in the
	 * given response. This should be in an HTML form.
	 * @param response
	 *
	 */

	public void showPropertiesInForm ( HttpServletResponse response )
	{
		if ( inspector == null ) return;
        try
        {
            PrintWriter out = response.getWriter();
            Property[] props = filterProperties ( inspector.getProperties() );
            out.println ( "<table>");
            for ( int i = 0 ; i < props.length ; i++ )
            {
            	out.println ( "<tr>");

            	String name = props[i].getName();
            	out.println ( "<td>" + name + "</td>");
            	out.println ( "<td>" );
            	Class clazz = PrimitiveClasses.getWrapperClass ( props[i].getType() );
            	if ( props[i].getAllowedValues() != null ) {
            		out.println ( "<select name=\""+ PAR_NAME_PREFIX+name+"\" >" );

            		String[] values = props[i].getAllowedValues();
            		for ( int j = 0 ; j < values.length ; j++ ){
            			out.println ( "<option value=\""+values[j]+"\">"+values[j]);
            		}
            		out.println ( "</select>");
            		//select list
            	}
            	else if ( Boolean.class.equals ( clazz ) ) {
            		//check box
            		out.println ( "<input type=\"checkbox\" value=\"true\" name=\""+PAR_NAME_PREFIX+BOOLEAN_PREFIX+name+"\"");

            		try {

            			Boolean b = ( Boolean ) props[i].getValue();

            			if ( b.booleanValue() )  out.println ( " checked ");
            		}
            		catch ( Exception ignore ) {
            			//ignore.printStackTrace();
            			//don't mess up the GUI display for this!
            			//so don't throw anything
            		}

            		out.println ( ">");
            	}
            	else {
            		out.print ( "<input type=\"text\" name=\""+PAR_NAME_PREFIX+name+"\" value=\"" );

            		try {

            			Object value = props[i].getValue();
            			out.print ( value.toString() );

            		}
            		catch ( NullPointerException noValue ) {
            			//ignore silently to avoid logs being filled
            		}
            		catch ( Exception ignore ) {

            			//ignore.printStackTrace();
            			//don't mess up GUI for this
            			//so don't throw anything
            		}
					out.println ( "\" >");

            	}
            	out.println ( "</td>");
            	out.println ( "</tr>");
            }
            out.println ( "</table>");
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            //throw new ServletException ( e );
            //IGNORE OR CLIENT JSPs WILL FAIL!
        }

	}

	/**
	 * Parse the name, value pairs from the given
	 * request. This request should be the POST of
	 * a form generated by showProperties.
	 * @param request
	 */

	public void parseProperties ( HttpServletRequest request )
	throws ServletException, ReadOnlyException
	{
		if ( inspector == null ) return;
		Enumeration pars = request.getParameterNames();

		parsedProperties = new HashMap();

        while ( pars.hasMoreElements() ) {
        	boolean bool = false; //true if boolean property
        	String name = ( String ) pars.nextElement();
        	if ( name.startsWith ( PAR_NAME_PREFIX ) ) {
        		String propertyName = name.substring ( PAR_NAME_PREFIX.length() );

        		try
                {
                    if ( propertyName.startsWith( BOOLEAN_PREFIX ) ) {
                    	bool = true;
                    	propertyName = propertyName.substring ( BOOLEAN_PREFIX.length() );
                    }
                    //Property p = inspector.getProperty ( propertyName );
                    if ( request.getParameter ( name ) != null && !request.getParameter( name ).equals ("") ) {

                    	if ( ! bool ) {
                    		setProperty ( propertyName, request.getParameter ( name ));
                    		parsedProperties.put ( propertyName, request.getParameter ( name ) );
                    	}
                    	else {
                    		//boolean, so set to true since the value is not null -> checked in form
                    		setProperty ( propertyName, "true" );
							parsedProperties.put ( propertyName, new Boolean ( true ) );
                    	}
                    }
                    else if ( bool ) {
                    	//null parameter value means unchecked -> set false
                    	setProperty ( propertyName, "false" );
						parsedProperties.put ( propertyName, new Boolean ( false ) );
                    }
                }
                catch (Exception e1)
                {
                    //ingore: if class name has changed (due to edits) the
                    //this can happen. DONT crash the UI for this
                    e1.printStackTrace();
                }

        	}
        }
	}

	public void setProperty ( String name , String value )
	throws ReadOnlyException, PropertyException
	{
			if ( inspector == null )
				throw new IllegalStateException ( "Not initialized");

			Property p = inspector.getProperty ( name );
			p.getEditor().setStringValue ( value );
			p.setValue(p.getEditor().getEditedObject());
	}

//
//    public void setProperty(String property, String value)
//    throws PropertyException
//    {
//		Property p = inspector.getProperty ( property );
//		p.getEditor().setStringValue(value);
//
//    }

}
