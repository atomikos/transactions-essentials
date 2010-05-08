//$Id: TestXAConnection.java,v 1.2 2006/09/19 08:03:56 guy Exp $
//$Log: TestXAConnection.java,v $
//Revision 1.2  2006/09/19 08:03:56  guy
//FIXED 10050
//
//Revision 1.1.1.1  2006/08/29 10:01:13  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:56  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:20  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2004/10/25 09:10:23  guy
//Added tests.
//
//Revision 1.6  2004/10/12 13:04:32  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: TestXAConnection.java,v 1.2 2006/09/19 08:03:56 guy Exp $
//Revision 1.5  2004/10/01 08:56:51  guy
//$Id: TestXAConnection.java,v 1.2 2006/09/19 08:03:56 guy Exp $
//Added tests.
//$Id: TestXAConnection.java,v 1.2 2006/09/19 08:03:56 guy Exp $
//
//$Id: TestXAConnection.java,v 1.2 2006/09/19 08:03:56 guy Exp $
//Revision 1.4  2004/09/30 09:56:25  guy
//$Id: TestXAConnection.java,v 1.2 2006/09/19 08:03:56 guy Exp $
//Added tests.
//$Id: TestXAConnection.java,v 1.2 2006/09/19 08:03:56 guy Exp $
//
//$Id: TestXAConnection.java,v 1.2 2006/09/19 08:03:56 guy Exp $
//Revision 1.3  2003/03/11 06:42:28  guy
//$Id: TestXAConnection.java,v 1.2 2006/09/19 08:03:56 guy Exp $
//Merged in changes from transactionsJTA100 branch.&
//$Id: TestXAConnection.java,v 1.2 2006/09/19 08:03:56 guy Exp $
//
//Revision 1.2.4.1  2002/12/27 07:55:31  guy
//Added facility for testing exclusive connection mode.
//
//Revision 1.2  2002/03/20 12:10:35  guy
//Added generic test functionality.
//
//Revision 1.1  2002/03/19 16:48:58  guy
//Added test resources for the jdbc package.
//

package com.atomikos.jdbc;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.TestXAResource;

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
}

