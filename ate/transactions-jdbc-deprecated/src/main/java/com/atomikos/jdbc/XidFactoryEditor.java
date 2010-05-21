package com.atomikos.jdbc;

import java.beans.PropertyEditorSupport;

/**
 * 
 * 
 * A reflection-based property editor for XidFactor. This allows the displaying
 * of an enummeration of possibilities for the XidFactory.
 */

public class XidFactoryEditor extends PropertyEditorSupport
{
    public XidFactoryEditor ()
    {
    }

    public String[] getTags ()
    {
        return new String[] { "Default" };
    }

    public String getAsText ()
    {
        return (String) getValue ();
    }

    public void setAsText ( String text ) throws IllegalArgumentException
    {
        setValue ( text );
    }
}
