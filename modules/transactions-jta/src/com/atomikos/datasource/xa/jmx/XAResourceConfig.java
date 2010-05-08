package com.atomikos.datasource.xa.jmx;

import java.io.Serializable;

import javax.management.ObjectName;

/**
 * 
 * 
 * 
 * A configuration class for XAResources. Instances can be serialized in order
 * to persist specific preferences.
 * 
 * 
 * 
 */
public class XAResourceConfig implements Serializable
{

    private boolean acceptAllXAResources;

    private ObjectName name;

    private boolean useWeakCompare;

    public XAResourceConfig ()
    {
        super ();

    }

    /**
     * @return
     */
    public ObjectName getName ()
    {
        return name;
    }

    /**
     * @return
     */
    public boolean usesWeakCompare ()
    {
        return useWeakCompare;
    }

    /**
     * @param name
     */
    public void setName ( ObjectName name )
    {
        this.name = name;
    }

    /**
     * @param b
     */
    public void setUseWeakCompare ( boolean b )
    {
        useWeakCompare = b;
    }

    /**
     * @param val
     */
    public void setAcceptAllXAResources ( boolean val )
    {
        acceptAllXAResources = val;

    }

    /**
     * @return
     */
    public boolean acceptsAllXAResources ()
    {
        return acceptAllXAResources;
    }

}
