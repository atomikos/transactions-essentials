//$Id: TestXADataSource.java,v 1.1.1.1.4.1 2007/04/12 14:52:05 guy Exp $
//$Log: TestXADataSource.java,v $
//Revision 1.1.1.1.4.1  2007/04/12 14:52:05  guy
//Added DataSourceBean test
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
//Revision 1.7  2004/11/10 14:20:56  guy
//Made counts static, to be able to LoadTest JDBC.
//
//Revision 1.6  2004/10/25 09:10:23  guy
//Added tests.
//
//Revision 1.5  2004/10/12 13:04:32  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.4  2004/10/12 08:26:07  guy
//*** empty log message ***
//
//Revision 1.3  2004/10/01 08:56:51  guy
//Added tests.
//
//Revision 1.2  2002/03/20 12:10:35  guy
//Added generic test functionality.
//
//Revision 1.1  2002/03/19 16:48:58  guy
//Added test resources for the jdbc package.
//

package com.atomikos.jdbc;
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

}
