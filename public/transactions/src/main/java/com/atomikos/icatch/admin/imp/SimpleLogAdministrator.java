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

package com.atomikos.icatch.admin.imp;

import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.admin.LogControl;

/**
 *
 *
 *
 *
 *
 * A simple log administrator that can be used for different UI technologies.
 */

public class SimpleLogAdministrator implements LogAdministrator
{

    private static SimpleLogAdministrator instance;

    public static synchronized SimpleLogAdministrator getInstance ()
    {
        if ( instance == null )
            instance = new SimpleLogAdministrator ();
        return instance;
    }

    private LogControl control;

    protected SimpleLogAdministrator ()
    {
        super ();
    }

    public void registerLogControl ( LogControl ctrl )
    {
        control = ctrl;

    }

    public void deregisterLogControl ( LogControl ctrl )
    {
        // commented out to tolerate Tomcat auto reload?
        // control = null;

    }

    public LogControl getLogControl ()
    {
        return control;
    }

}
