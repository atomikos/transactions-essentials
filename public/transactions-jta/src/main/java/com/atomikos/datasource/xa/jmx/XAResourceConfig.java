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

package com.atomikos.datasource.xa.jmx;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

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
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(XAResourceConfig.class);

	private static final long serialVersionUID = -2778887014312506596L;

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
