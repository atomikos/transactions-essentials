package com.atomikos.jdbc;


/**
 * 
 * 
 * 
 * 
 * 
 * A shutdown hook for closing the JtaDataSourceImp. This class is useful for
 * cases where application-level shutdown is impractical.
 */
public class DataSourceShutdownHook extends Thread
{
    JtaDataSourceImp ds_;

    /**
     * Create a new instance for a given datasource.
     * 
     * @param ds
     *            The datasource, which will be closed at shutdown.
     */

    public DataSourceShutdownHook ( JtaDataSourceImp ds )
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
