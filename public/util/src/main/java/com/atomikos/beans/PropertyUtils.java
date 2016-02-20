/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.beans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Smart reflection helper without the need for GUI classes (works on headless servers too).
 * <p>Extracted from the BTM project and re-licensed under Apache 2.0 license.<br/>
 * &copy; Bitronix 2005, 2006, 2007</p>
 *
 * @author lorban
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class PropertyUtils 
{
	
	/**
	 * Gets all implemented interfaces of a class. 
	 */
	
	public static Set<Class<?>> getAllImplementedInterfaces ( Class clazz ) 
	{
		Set ret = null;
		
		if ( clazz.getSuperclass() != null ) {
			//if superclass exists: first add the superclass interfaces!!!
			ret = getAllImplementedInterfaces ( clazz.getSuperclass() ); 
		}
		else {
			//no superclass: start with empty set
			ret = new HashSet();
		} 
		
		
		//add the interfaces in this class
		Class[] interfaces = clazz.getInterfaces();
		ret.addAll(Arrays.asList(interfaces));

		return ret;
	}

    /**
     * Sets a direct or indirect property (dotted property: prop1.prop2.prop3) on the target object. This method tries
     * to be smart in the way that intermediate properties currently set to null are set if it is possible to create
     * and set an object. Conversions from propertyValue to the proper destination type are performed when possible.
     * @param target the target object on which to set the property.
     * @param propertyName the name of the property to set.
     * @param propertyValue the value of the property to set.
     * @throws PropertyException if an error happened while trying to set the property.
     */
    public static void setProperty ( Object target, String propertyName, Object propertyValue ) throws PropertyException 
    {
        String[] propertyNames = propertyName.split("\\.");
        Object currentTarget = target;
        for (int i = 0; i < propertyNames.length -1; i++) {
            String name = propertyNames[i];
            Object result = callGetter(currentTarget, name);
            if (result == null) {
                Class propertyType = getPropertyType(target, name);
                try {
                    result = propertyType.newInstance();
                } catch (InstantiationException ex) {
                    throw new PropertyException("cannot set property '" + propertyName + "' - '" + name + "' is null and cannot be auto-filled", ex);
                } catch (IllegalAccessException ex) {
                    throw new PropertyException("cannot set property '" + propertyName + "' - '" + name + "' is null and cannot be auto-filled", ex);
                }
                callSetter(currentTarget, name, result);
            }
            currentTarget = result;
        }

        String lastPropertyName = propertyNames[propertyNames.length - 1];
        if (currentTarget instanceof Properties) {
            Properties p = (Properties) currentTarget;
            p.setProperty(lastPropertyName, propertyValue.toString());
        }
        else {
            setDirectProperty(currentTarget, lastPropertyName, propertyValue);
        }
    }

    /**
     * Builds a map of direct javabeans properties of the target object. Only read/write properties (ie: those who have
     * both a getter and a setter) are returned.
     * @param target the target object from which to get properties names.
     * @return a Map of String with properties names as key and their values
     * @throws PropertyException if an error happened while trying to get a property.
     */
    
	public static Map getProperties ( Object target ) throws PropertyException 
    {
        Map properties = new HashMap();
        Class clazz = target.getClass();
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String name = method.getName();
            if (method.getModifiers() == Modifier.PUBLIC && method.getParameterTypes().length == 0 && (name.startsWith("get") || name.startsWith("is")) && containsSetterForGetter(clazz, method)) {
                String propertyName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
                try {
                    Object propertyValue = method.invoke(target, (Object[]) null);
                    if (propertyValue != null && propertyValue instanceof Properties) {
                        Map propertiesContent = getNestedProperties(propertyName, (Properties) propertyValue);
                        properties.putAll(propertiesContent);
                    }
                    else {
                        properties.put(propertyName, propertyValue);
                    }
                } catch (IllegalAccessException ex) {
                    throw new PropertyException("cannot set property '" + propertyName + "' - '" + name + "' is null and cannot be auto-filled", ex);
                } catch (InvocationTargetException ex) {
                    throw new PropertyException("cannot set property '" + propertyName + "' - '" + name + "' is null and cannot be auto-filled", ex);
                }

            } // if
        } // for
        return properties;
    }

    /**
     * Gets a direct or indirect property (dotted property: prop1.prop2.prop3) on the target object.
     * @param target the target object from which to get the property.
     * @param propertyName the name of the property to get.
     * @return the value of the specified property.
     * @throws PropertyException if an error happened while trying to get the property.
     */
    public static Object getProperty ( Object target, String propertyName ) throws PropertyException 
    {
        String[] propertyNames = propertyName.split("\\.");
        Object currentTarget = target;
        for (int i = 0; i < propertyNames.length; i++) {
            String name = propertyNames[i];
            Object result = callGetter(currentTarget, name);
            if (result == null && i < propertyNames.length -1)
                throw new PropertyException("cannot get property '" + propertyName + "' - '" + name + "' is null");
            currentTarget = result;
        }

        return currentTarget;
    }

    /**
     * Sets a map of properties on the target object.
     * @param target the target object on which to set the properties.
     * @param properties a map of String/Object pairs.
     * @throws PropertyException if an error happened while trying to set a property.
     */
    public static void setProperties ( Object target, Map properties ) throws PropertyException 
    {
        Iterator it = properties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            Object value = entry.getValue();
            setProperty(target, name, value);
        }
    }

    /**
     * Sets a direct property on the target object. Conversions from propertyValue to the proper destination type
     * are performed whenever possible.
     * @param target the target object on which to set the property.
     * @param propertyName the name of the property to set.
     * @param propertyValue the value of the property to set.
     * @throws PropertyException if an error happened while trying to set the property.
     */
    private static void setDirectProperty ( Object target, String propertyName, Object propertyValue ) throws PropertyException 
    {
        Method setter = getSetter(target, propertyName);
        Class parameterType = setter.getParameterTypes()[0];
        try {
            if (propertyValue != null) {
                Object transformedPropertyValue = convert(propertyValue, parameterType);
                setter.invoke(target, new Object[] {transformedPropertyValue});
            }
            else {
                setter.invoke(target, new Object[] {null});
            }
        } catch (IllegalAccessException ex) {
            throw new PropertyException("property '" + propertyName + "' is not accessible", ex);
        } catch (InvocationTargetException ex) {
            throw new PropertyException("property '" + propertyName + "' access threw an exception", ex);
        }
    }

    private static Map getNestedProperties(String prefix, Properties properties) 
    {
        Map result = new HashMap();
        Iterator it = properties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            String value = (String) entry.getValue();
            result.put(prefix + '.' + name, value);
        }
        return result;
    }

    private static Object convert ( Object value, Class destinationClass )
    throws PropertyException
    {
        if (value.getClass() == destinationClass)
            return value;

        if (isPrimitiveType(value.getClass()))
            return value;
        
        if (value.getClass() == String.class && isPrimitiveType(destinationClass)) {
        	return convertStringToPrimitive((String) value, destinationClass);
        }

        if(Set.class.isAssignableFrom(destinationClass)){
        	if(value.getClass() == String.class) {
        		return convertStringToSet((String)value);
        	}
        }
       
        if(destinationClass.isAssignableFrom(value.getClass())){
        		return value;
        }
        
        throw new PropertyException("cannot convert values of type '" + value.getClass().getName() + "' into type '" + destinationClass + "'");
    }
    
    private static Set<String> convertStringToSet(String propertyValue) {
    	String[] elements=propertyValue.split(",");
    	Set<String> result= new HashSet<String>();
    	for (String element: elements) {
    		result.add(element);
		}

    	return result;
	}

    private static Object convertStringToPrimitive(String value, Class destinationClass) { 
		if ((destinationClass == int.class || destinationClass == Integer.class)) {
            return new Integer(value);
        }

        if ((destinationClass == boolean.class || destinationClass == Boolean.class)) {
            return Boolean.valueOf(value);
        }
        
        if ((destinationClass == short.class || destinationClass == Short.class)) {
            return new Short(value);
        }
        
        if ((destinationClass == byte.class || destinationClass == Byte.class)) {
            return new Byte(value);
        }
        
        if ((destinationClass == long.class || destinationClass == Long.class)) {
            return new Long(value);
        }
        
        if ((destinationClass == float.class || destinationClass == Float.class)) {
            return new Float(value);
        }
        
        if ((destinationClass == double.class || destinationClass == Double.class)) {
            return new Double(value);
        }
        
        if ((destinationClass == char.class || destinationClass == Character.class)) {
            return value.charAt(0);
        }
        
        return null;
	}

	private static boolean isPrimitiveType(Class clazz) {
		return  clazz == int.class || clazz == Integer.class || 
				clazz == boolean.class || clazz == Boolean.class ||
				clazz == byte.class || clazz == Byte.class || 
				clazz == short.class || clazz == Short.class ||
				clazz == long.class || clazz == Long.class ||
				clazz == float.class || clazz == Float.class ||
				clazz == double.class || clazz == Double.class ||
				clazz == char.class || clazz == Character.class;
	}

    private static void callSetter ( Object target, String propertyName, Object parameter) throws PropertyException 
    {
        Method setter = getSetter(target, propertyName);
        try {
            setter.invoke(target, new Object[] {parameter});
        } catch (IllegalAccessException ex) {
            throw new PropertyException("property '" + propertyName + "' is not accessible", ex);
        } catch (InvocationTargetException ex) {
            throw new PropertyException("property '" + propertyName + "' access threw an exception", ex);
        }
    }

    private static Object callGetter ( Object target, String propertyName ) throws PropertyException 
    {
        Method getter = getGetter(target, propertyName);
        try {
            return getter.invoke(target, (Object[]) null);
        } catch (IllegalAccessException ex) {
            throw new PropertyException("property '" + propertyName + "' is not accessible", ex);
        } catch (InvocationTargetException ex) {
            throw new PropertyException("property '" + propertyName + "' access threw an exception", ex);
        }
    }

    private static Method getSetter ( Object target, String propertyName ) throws PropertyException 
    {
        if (propertyName == null || "".equals(propertyName))
            throw new PropertyException("encountered invalid null or empty property name");
        String setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Method[] methods = target.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().equals(setterName)  &&  method.getReturnType().equals(void.class)  &&  method.getParameterTypes().length == 1) {
                return method;
            }
        }
        throw new PropertyException("no writeable property '" + propertyName + "' in class '" + target.getClass().getName() + "'");
    }

    private static Method getGetter ( Object target, String propertyName ) throws PropertyException 
    {
        String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Method[] methods = target.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().equals(getterName)  &&  !method.getReturnType().equals(void.class)  &&  method.getParameterTypes().length == 0) {
                return method;
            }
        }
        throw new PropertyException("no readable property '" + propertyName + "' in class '" + target.getClass().getName() + "'");
    }

    private static boolean containsSetterForGetter ( Class clazz, Method getterMethod ) throws PropertyException 
    {
        String getterMethodName = getterMethod.getName();
        String setterMethodName;

        if (getterMethodName.startsWith("get"))
            setterMethodName = "set" + getterMethodName.substring(3);
        else if (getterMethodName.startsWith("is"))
            setterMethodName = "set" + getterMethodName.substring(2);
        else
            throw new PropertyException("method '" + getterMethodName + "' is not a getter, no setter can be found");

        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().equals(setterMethodName))
                return true;
        }
        return false;
    }

    private static Class getPropertyType ( Object target, String propertyName ) throws PropertyException 
    {
        String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Method[] methods = target.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().equals(getterName)  &&  !method.getReturnType().equals(void.class)  &&  method.getParameterTypes().length == 0) {
                return method.getReturnType();
            }
        }
        throw new PropertyException("no property '" + propertyName + "' in class '" + target.getClass().getName() + "'");
    }

}
