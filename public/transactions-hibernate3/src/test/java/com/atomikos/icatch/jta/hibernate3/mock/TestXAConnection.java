package com.atomikos.icatch.jta.hibernate3.mock;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

 /**
  *
  *
  *A test connection for testing the JTA interfaces for JDBC.
  */

class TestXAConnection
implements XAConnection
{
      
      private Vector listeners_;
      private XAResource xares_;
      private String user_;
      private String password_;
      
      
      TestXAConnection() 
      {
          listeners_ = new Vector(); 
          xares_ = null;
          //System.out.println ( "Created new XAConnection: " + this );
      }
      
      TestXAConnection ( XAResource xares )
      {
      	 this();
      	 xares_ = xares;
      }
      
       /**
        *Notify the connection listeners.
        *@param exception If null, then connectionClosed will be called.
        *Otherwise, connectionErrorOccurred.
        */
        
      private synchronized void notifyListeners ( SQLException exception )
      {
          ConnectionEvent ce = 
              new ConnectionEvent  ( this , exception );
          Enumeration enumm = listeners_.elements();
          while ( enumm.hasMoreElements() ) {
              ConnectionEventListener l = 
                  ( ConnectionEventListener ) enumm.nextElement();
               
              //System.out.println ( "Notifying listener " + l );
              if  ( exception != null )
                  l.connectionErrorOccurred ( ce );
              else
                  l.connectionClosed ( ce );
              
          }
      }
      
       /**
        *Callback method for indicating that the connection handle 
        *was closed.
        */
        
      void connectionClosed()
      {
          notifyListeners ( null ); 
      }
      
       /**
        *Callback for indication of an error in the connection handle.
        */
        
      void connectionError ( SQLException err )
      {
            notifyListeners (  err );
      }
      
       /**
        *@see javax.sql.XAConnection
        */
        
      public synchronized void addConnectionEventListener (
          ConnectionEventListener lstnr )
      {
      	
      	 //System.out.println ( this + " adding listener " + lstnr );
          if ( !listeners_.contains ( lstnr ) )
              listeners_.addElement ( lstnr );
              
      }  
      
      
      /**
        *@see javax.sql.XAConnection
        */
        
      public synchronized void removeConnectionEventListener (
          ConnectionEventListener lstnr )
      {
          listeners_.removeElement ( lstnr );
              
      }  
      
      
      /**
        *@see javax.sql.XAConnection
        */
        
      public void close() throws SQLException
      {
           
      }
      
      /**
        *@see javax.sql.XAConnection
        */
        
        
      public Connection getConnection() throws SQLException
      {
            return new TestConnection ( this );
      }
      
      /**
        *@see javax.sql.XAConnection
        */
        
      public XAResource getXAResource() 
      throws SQLException
      {
      	  XAResource ret = null;
      	  
          if ( xares_ == null )
          	ret = new TestXAResource();
          else ret = xares_;
          
          return ret;
      }
      
      public boolean equals ( Object o )
      {
          boolean ret = false;
          
          ret = ( o == this );
          
          return ret; 
      }
      
    
      
    /**
     * @return
     */
    public String getPassword()
    {
        return password_;
    }

    /**
     * @return
     */
    public String getUser()
    {
        return user_;
    }

    /**
     * @param string
     */
    public void setPassword(String string)
    {
        password_ = string;
    }

    /**
     * @param string
     */
    public void setUser(String string)
    {
        user_ = string;
    }

	public void addStatementEventListener(StatementEventListener listener) {
		throw new UnsupportedOperationException();
	}

	public void removeStatementEventListener(StatementEventListener listener) {
		throw new UnsupportedOperationException();
	}
}

