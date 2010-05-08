package com.atomikos.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 *
 *
 *
 *A member filter implementation that accepts all be default.
 *Subclasses can override methods to refine acceptance.
 */

public class DefaultMemberFilter
        implements MemberFilter
{
    public boolean acceptsField ( Field field )
    {
        return true;
    }

    public boolean acceptsMethod ( Method method )
    {
        return true;
    }

    public boolean acceptsConstructor ( Constructor constructor )
    {
        return true;
    }
}
