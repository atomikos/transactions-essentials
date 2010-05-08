package com.atomikos.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;


/**
 *
 *
 *
 *An inspector class for finding and filtering information
 *about general classes (not just beans). The inspector
 *uses a MemberFilter to restrict the set of returned
 *methods, fields and constructors.
 */

public class ClassInspector
{
    private Class clazz_;
    //the class to inspect

    private MemberFilter memberFilter_;
    //for filtering the returned members



    public ClassInspector ( Class clazz )
    {
        clazz_ = clazz;
        memberFilter_ = new DefaultMemberFilter();
    }

    public Class getInspectedClass()
    {
        return clazz_;
    }

    public void setMemberFilter ( MemberFilter memberFilter )
    {
        memberFilter_ = memberFilter;
    }

    public MemberFilter getMemberFilter(){
        return memberFilter_;
    }

    /**
     * Get the methods for this class.
     * @return Method[] The methods that are accepted by the filter.
     */

    public Method[] getMethods()
    {
        Method[] allMethods = clazz_.getMethods();
        ArrayList list = new ArrayList();
        for ( int i = 0 ; i < allMethods.length ; i++ )  {
            if ( memberFilter_.acceptsMethod ( allMethods[i]))
                list.add ( allMethods[i]);
        }
        Method[] ret = ( Method[] ) list.toArray ( new Method[0]);

        return ret;
    }

    /**
     * Get the fields for this class.
     * @return Field[] The fields that are accepted by the filter.
     */

    public Field[] getFields()
    {
        Field[] allFields = clazz_.getFields();
        ArrayList list = new ArrayList();
        for ( int i = 0 ; i < allFields.length ; i++ )  {
            if ( memberFilter_.acceptsField ( allFields[i]))
                list.add ( allFields[i]);
        }
        Field[] ret = ( Field[] ) list.toArray ( new Field[0]);

        return ret;
    }

    /**
     *Get the constructors for this class.
     * @return Constructor[] The constructors that are accepted by the filter.
     */

    public Constructor[] getConstructors()
    {
        Constructor[] allConstructors = clazz_.getConstructors();
        ArrayList list = new ArrayList();
        for ( int i = 0 ; i < allConstructors.length ; i++ )  {
            if ( memberFilter_.acceptsConstructor ( allConstructors[i]))
                list.add ( allConstructors[i]);
        }
        Constructor[] ret = ( Constructor[] ) list.toArray ( new Constructor[0]);

        return ret;
    }

}
