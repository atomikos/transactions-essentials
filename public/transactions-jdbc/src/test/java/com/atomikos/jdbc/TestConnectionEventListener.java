package com.atomikos.jdbc;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

/**
 * 
 * 
 * 
 * 
 *
 * A test listener for asserting that our pooled connections really
 * notify.
 */
public class TestConnectionEventListener implements ConnectionEventListener
{

	private boolean notified_ = false;
    /**
     * @see javax.sql.ConnectionEventListener#connectionClosed(javax.sql.ConnectionEvent)
     */
    public void connectionClosed(ConnectionEvent arg0)
    {
        notified_ = true;
        
    }

    /**
     * @see javax.sql.ConnectionEventListener#connectionErrorOccurred(javax.sql.ConnectionEvent)
     */
    public void connectionErrorOccurred(ConnectionEvent arg0)
    {
       	notified_ = true;
        
    }

	public boolean isNotified()
	{
		return notified_;
	}
   
}
