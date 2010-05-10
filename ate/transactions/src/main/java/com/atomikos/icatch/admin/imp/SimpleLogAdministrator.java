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
