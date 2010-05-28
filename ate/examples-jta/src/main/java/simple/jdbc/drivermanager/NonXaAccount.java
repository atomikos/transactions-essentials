
package simple.jdbc.drivermanager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;

 /**
  *
  *
  *A simple program that uses JDBC-level integration with
  *Transactions.  Although only one database is 
  *accessed, it shows all important steps for programming
  *with Transactions.
  *
  *Usage: java NonXaAccount <account number> <operation> [<amount>]<br>
  *where:<br> 
  *account number is an integer between 0 and 99<br>
  *and operation is one of (balance, owner, withdraw, deposit).<br>
  *In case of withdraw and deposit, an extra (integer) amount is expected.
  */
  
public class NonXaAccount
{
      //the unique resource identifier; change if needed.
      private static String resourceName = "NonXaDB";
	
      //the user name in the database; change if needed.
      private static String user = "sa";
      
      //the password for the user; change if needed.
      //the current settings are empty strings, for HypersonicSQL
      private static String passwd = "";

      //the full name of the JDBC driver class
      //change if required
      private static String driverClassName = "org.apache.derby.jdbc.EmbeddedDriver";

      //the URL to connect with; this should be a valid DriverManager URL
      //change if needed
      private static String connectUrl = "jdbc:derby:NonXaAccountDB";

         
     //the data source, set by getDataSource
      private static AtomikosNonXADataSourceBean ds  = null;

      
      /**
        *Setup DB tables if needed.
        */
        
      private static void checkTables()
      throws Exception
      {
            boolean error = false;
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
            catch ( Exception e ) {
            
                error = true; 
                throw e;
            }
            finally {
                closeConnection ( conn , error );
               
            }
            
            //That concludes setup
           
      }
      
            
       /**
        *Gets the datasource instance.
        *
        *@return DataSource The data source.
        */
      
      private static DataSource getDataSource()
      {
          //Setup of NonXADataSource
          //as an alternative to constructing a new instance,
          //this could also be a lookup in JNDI

          if ( ds == null ) {
              //Get an Atomikos non-XA datasource instance; either by
              //constructing one or by lookup in JNDI where available.
              //NOTE: for the sake of this minimal example we don't
              //use JNDI. However, the NonXADataSourceBean can be
              //bound in JNDI whenever required for your application.
              ds = new AtomikosNonXADataSourceBean();
              ds.setUniqueResourceName( resourceName );
             // ds.setUser ( user );
              //ds.setPassword ( passwd );
              ds.setUrl ( connectUrl );
              ds.setDriverClassName ( driverClassName );
              //OPTIONAL pool size
              ds.setPoolSize ( 1 );
              //OPTIONAL timeout in secs between pool cleanup tasks
              ds.setBorrowConnectionTimeout ( 60 );

              //NOTE: the datasource can be bound in JNDI where available
          }
          
          return ds;
      }
      
       /**
        *Utility method to start a transaction and
        *get a connection. 
        *@return Connection The connection.
        */
        
      private static Connection getConnection()
      throws Exception
      {
          DataSource ds = getDataSource();
          Connection conn = null;
          //Retrieve or construct the UserTransaction
          //(the result can be bound in JNDI where available)
          UserTransaction utx = new UserTransactionImp();
          
          //First, create a transaction
          utx.begin();
          conn = ds.getConnection();
          
          return conn;
          
      }
      
      /**
        *Utility method to close the connection and
        *terminate the transaction. 
        *This method does all XA related tasks
        *and should be called within a transaction.
        *When it returns, the transaction will be terminated.
        *@param conn The connection.
        *@param error Indicates if an error has
        *occurred or not. If true, the transaction will be rolled back.
        *If false, the transaction will be committed.
        */
        
      private static void closeConnection ( Connection conn , boolean error )
      throws Exception
      {
          if ( conn != null ) conn.close();
          
          //get the UserTransaction by constructing a new
          //instace of by JNDI lookup where available
          UserTransaction utx = new UserTransactionImp();
                    
          if ( error )
                  utx.rollback();
              else 
                  utx.commit(); 
                  
      }
      
      
      private static long getBalance ( int account )
      throws Exception
      {
          long res = -1;
          boolean error = false;
          Connection conn = null;
          
          try {
              conn = getConnection();
              Statement s = conn.createStatement();
              String query = "select balance from Accounts where account='"
                          +"account"+account+"'";
              ResultSet rs = s.executeQuery ( query );
              if ( rs == null || !rs.next() ) 
                  throw new Exception ( "Account not found: " + account );
              res = rs.getLong ( 1 );
              s.close();
          }
          catch ( Exception e ) {
              error = true; 
              throw e;
          }
          finally {
              closeConnection ( conn , error );
          }
          return res;
      }
      
      private static  String getOwner ( int account )
      throws Exception
      {
          String res = null;
          boolean error = false;
          Connection conn = null;
          
          try {
              conn = getConnection();
              Statement s = conn.createStatement();
              String query = "select owner from Accounts where account='account" 
                                + account+"'";
              ResultSet rs = s.executeQuery ( query );
              if ( rs == null || !rs.next() ) 
                  throw new Exception ( "Account not found: " +account );
              res = rs.getString ( 1 );
              s.close();
          }
          catch ( Exception e ) {
              error = true; 
              throw e;
          }
          finally {
              closeConnection ( conn , error );
          }
          return res;
      }
      
      private static void withdraw ( int account , int amount )
      throws Exception
      {
          boolean error = false;
          Connection conn = null;
          
          try {
              conn = getConnection();
              Statement s = conn.createStatement();
              
              String sql = "update Accounts set balance = balance - " 
                  + amount + " where account ='account"+account+"'";
              s.executeUpdate ( sql );
              s.close();
          }
          catch ( Exception e ) {
              error = true; 
              throw e;
          }
          finally {
              closeConnection ( conn , error );
             
          }
            
      }
  
  
      public static void main ( String[] args )
      {
          try {
              //test if DB data has to be created
              checkTables();
              
              if ( args.length < 2 || args.length >3 ) {
                  System.err.println ( 
                  "Arguments required: <acc. number> <operation> [<amount>]" );
                  System.exit ( 1 );
              }
              
              //get account number
              int accno = new Integer ( args[0] ).intValue();
              if ( accno < 0 || accno > 99 ) {
                  System.err.println ( 
                  "Account number should be between 0 and 99." );
                  System.exit ( 1 );
              }
              
              //get operation
              String op = args[1];
              
              if ( op.equals ( "balance" ) ) {
                  long bal = getBalance ( accno );
                  System.out.println ( "Balance of account " + accno + " is: " + bal );
              }
              else if ( op.equals ( "owner" ) ) {
                  String owner = getOwner ( accno );
                  System.out.println ( "Owner of account " + accno + " is: " + owner );
              }
              else {
                  //get amount
                  if ( args.length < 3 ) {
                      System.err.println ( "Missing argument: amount." );
                      System.exit ( 1 );
                  }
                  int amount = new Integer ( args[2] ).intValue();
                  if ( op.equals ( "withdraw" ) ) 
                     withdraw ( accno , amount );   
                  else withdraw ( accno , amount * (-1) );
              }
              
              
          }
          catch ( Exception e ) {
              e.printStackTrace(); 
          }

          //Simple, automatic initialization has the minor drawback of not exiting by itself
          //since the JDBC pools and the transaction service are still running background
          //threads.
          System.exit ( 0 );
          
      }
  
}
