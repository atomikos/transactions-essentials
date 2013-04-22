package com.atomikos.icatch.jta.hibernate3.mock;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAResource;

 /**
  *
  *
  *A test xa datasource implementation.
  *
  */

public class TestXADataSource
implements XADataSource
{
	
	public static boolean simulateDbDown = false;
  
    private static int count_;
    private XAResource xares_;
    
    private String lastUser;
    private String lastPassword;
    private int loginTimeout;
    
    
    
    public TestXADataSource()
    {
        resetStatistics(); 
    }
    
    public TestXADataSource ( XAResource xares )
    {
    	this();
    	xares_ = xares;
    }
    
      /**
       *@see javax.sql.XADataSource
       */
       
    public int getLoginTimeout()
    throws SQLException
    {
        return loginTimeout; 
    } 
    
     /**
       *@see javax.sql.XADataSource
       */
       
    public void setLoginTimeout ( int secs )
    throws SQLException
    {
         this.loginTimeout = secs;
    }
    
     /**
       *@see javax.sql.XADataSource
       */
       
    public void setLogWriter ( PrintWriter pw )
    throws SQLException
    {
      
    }
    
     /**
       *@see javax.sql.XADataSource
       */
       
    public PrintWriter getLogWriter()
    throws SQLException
    {
        return null; 
    }
  
     /**
       *@see javax.sql.XADataSource
       */
       
    public synchronized XAConnection getXAConnection()
    throws SQLException
    {
    	if ( simulateDbDown ) throw new SQLException ( "Simulated DB down" );
    	XAConnection ret = null; 
    	if ( xares_ == null )
    		ret = new TestXAConnection();
    	else ret = new TestXAConnection ( xares_ );
        count_++;
        return ret; 
    }
    
     /**
       *@see javax.sql.XADataSource
       */
       
    public XAConnection getXAConnection ( String user , String pw )
    throws SQLException
    {
        TestXAConnection ret = ( TestXAConnection ) getXAConnection();
        ret.setUser ( user );
        ret.setPassword ( pw );
        setLastUser ( user );
        setLastPassword ( pw );
        return ret;
    }
    
     /**
      *Reset the statistic information.
      */
      
    public static void resetStatistics()
    {
    	//System.out.println ( "Resetting statistics");
        count_ = 0; 
    }
    
     /**
      *Get the number of times a connection was
      *gotten from this instance. This can be used
      *to test the pool functionality.
      *
      *@return int The count.
      */
      
    public static int getConnectionCount()
    {
        return count_; 
    }
    /**
     * @return
     */
    public String getLastPassword()
    {
        return lastPassword;
    }

    /**
     * @return
     */
    public String getLastUser()
    {
        return lastUser;
    }

    /**
     * @param string
     */
    public void setLastPassword(String string)
    {
        lastPassword = string;
    }

    /**
     * @param string
     */
    public void setLastUser(String string)
    {
        lastUser = string;
    }
    
    public boolean isWrapperFor(Class<?> iface) {
    	return false;
    }
    
    public <T> T unwrap(Class<T> iface) throws SQLException {
    	throw new SQLException();
    }

}
