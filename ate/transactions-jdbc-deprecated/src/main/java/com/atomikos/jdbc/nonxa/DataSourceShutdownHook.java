package com.atomikos.jdbc.nonxa;


/**
 * 
 * 
 * 
 * 
 * 
 * 
 */
public class DataSourceShutdownHook extends Thread
{

    NonXADataSourceImp ds_;

    /**
     * Create a new instance for a given datasource.
     * 
     * @param ds
     *            The datasource, which will be closed at shutdown.
     */

    public DataSourceShutdownHook ( NonXADataSourceImp ds )
    {
        super ();
        ds_ = ds;
    }

    public void run ()
    {
        // called at shutdown time
        ds_.close ();
    }
}
