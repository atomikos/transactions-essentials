package com.atomikos.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 *
 *
 *A filter for restricting which members (fields, constructors, methods)
 *are returned by the ClassInspector.
 */

public interface MemberFilter
{
    /**
     * Test if the filter accepts a field.
     * @param field  The field
     * @return  boolean True iff acceptable.
     */

    public boolean acceptsField ( Field field );

    /**
     * Test if the filter accepts a method.
     * @param method  The method.
     * @return  boolean True iff acceptable.
     */

    public boolean acceptsMethod ( Method method );

    /**
     * Test if the filter accepts this constructor.
     *
     * @param constructor  The constructor.
     * @return  boolean True iff acceptable.
     */

    public boolean acceptsConstructor ( Constructor constructor );
}
