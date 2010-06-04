package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;


 /**
  * Demonstration of how Atomikos and Spring can make
  * regular Java classes transactional. Note that there is no
  * explicit dependency on Atomikos nor Spring in this code.
  *
  */

public class Bank 
{

    private DataSource dataSource;

    
    public Bank() {}

    /**
     * This method allows the DataSource to be set.
     * Spring's configuration facilities will call
     * this method for us.
     */
    
    public void setDataSource ( DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    //
    //Utility methods
    //

    private DataSource getDataSource()
    {
        return this.dataSource;
    }


    private Connection getConnection()
    throws SQLException
    {
        Connection ret = null;
        if ( getDataSource() != null ) {
            ret = getDataSource().getConnection();
        }
        return ret;
    }

    private void closeConnection ( Connection c )
    throws SQLException
    {
        if ( c != null ) c.close();
    }
    
    public void checkTables()
        throws SQLException
    {
        
        Connection conn = null;
        try {
            conn = getConnection();
            Statement s = conn.createStatement();
            try {
                s.executeQuery ( "select * from Accounts" );
            }
            catch ( SQLException ex ) {
                //table not there => create it
                System.err.println ( "Creating Accounts table..." );
                s.executeUpdate ( "create table Accounts ( " +
                                  " account VARCHAR ( 20 ), owner VARCHAR(300), balance DECIMAL (19,0) )" );
                for ( int i = 0; i < 100 ; i++ ) {
                    s.executeUpdate ( "insert into Accounts values ( " +
                                      "'account"+i +"' , 'owner"+i +"', 10000 )" );
                }
            }
            s.close();
        }
        finally {
            closeConnection ( conn );

        }

        //That concludes setup

    }

    
    //
    //Business methods are below
    //

    public long getBalance ( int account )
    throws SQLException
    {
        
        long res = -1;
        Connection conn = null;

        try {
            conn = getConnection();
            Statement s = conn.createStatement();
            String query = "select balance from Accounts where account='"
                +"account"+account+"'";
            ResultSet rs = s.executeQuery ( query );
            if ( rs == null || !rs.next() ) 
                throw new SQLException ( "Account not found: " + account );
            res = rs.getLong ( 1 );
            s.close();
        }
        finally {
            closeConnection ( conn );
        }
        return res;
        
    }

    public void withdraw ( int account , int amount )
        throws Exception
    {

        
        Connection conn = null;

        try {
            conn = getConnection();
            Statement s = conn.createStatement();

            String sql = "update Accounts set balance = balance - " 
                + amount + " where account ='account"+account+"'";
            s.executeUpdate ( sql );
            s.close();
        }
        finally {
            closeConnection ( conn );

        }

    }
    
    
}
