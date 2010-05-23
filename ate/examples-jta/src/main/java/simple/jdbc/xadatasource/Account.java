package simple.jdbc.xadatasource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;

 /**
  *
  *
  *A simple program that uses JDBC-level integration with
  *TransactionsEssentials.  Although only one database is 
  *accessed, it shows all important steps for programming
  *with TransactionsEssentials.
  *
  *Usage: java Account <account number> <operation> [<amount>]<br>
  *where:<br> 
  *account number is an integer between 0 and 99<br>
  *and operation is one of (balance, owner, withdraw, deposit).<br>
  *In case of withdraw and deposit, an extra (integer) amount is expected.
  */
  
public class Account
{
      
      //the globally unique resource name for the DB; change if needed
      private static String resourceName = "samples/jdbc/ExamplesJdbcPooledDatabase";
      
      //the data source, set by getDataSource
      private static AtomikosDataSourceBean ds  = null;

      
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
            }
            catch ( Exception noConnect ) {
            	noConnect.printStackTrace();
                System.err.println ( "Failed to connect." );
                System.err.println ( "PLEASE MAKE SURE THAT FIRSTSQL IS INSTALLED AND RUNNING" );
                throw noConnect;
            }
            try {
                  
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

    
      private static DataSource getDataSource()
      {

          if ( ds == null ) {
              //Find or construct a datasource instance;
              //this could equally well be a JNDI lookup
              //where available. To keep it simple, this
              //demo merely constructs a new instance.
              ds = new AtomikosDataSourceBean();
              //REQUIRED: the full name of the XA datasource class
              //ds.setXaDataSourceClassName ( "COM.FirstSQL.Dbcp.DbcpXADataSource" );
              ds.setXaDataSourceClassName ( "org.apache.derby.jdbc.EmbeddedXADataSource");
              Properties properties = new Properties();
              properties.put("databaseName",  "db");
              //properties.put("databaseName",  "db;create=true");
              ds.setXaProperties(properties);
              //REQUIRED: properties to set on the XA datasource class
//              ds.getXaProperties().setProperty("user", "demo");
//              ds.getXaProperties().setProperty("portNumber", "8000");
              //REQUIRED: unique resource name for transaction recovery configuration
              ds.setUniqueResourceName ( resourceName );
              //OPTIONAL: what is the pool size?
              ds.setPoolSize ( 1 );
              //OPTIONAL: how long until the pool thread checks liveness of connections?
              ds.setBorrowConnectionTimeout ( 60 );

              //NOTE: the resulting datasource can be bound in JNDI where available
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

          //Retrieve of construct the UserTransaction
          //(can be bound in JNDI where available)
          UserTransaction utx = new UserTransactionImp();
          utx.setTransactionTimeout ( 60 );
          
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
          
          UserTransaction utx = new UserTransactionImp();
          if ( utx.getStatus() != Status.STATUS_NO_TRANSACTION ) {
              if ( error )
                  utx.rollback();
              else 
                  utx.commit();
          }
          else System.out.println ( "WARNING: closeConnection called outside a tx" );
                  
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

          //Force exit since the TM and JDBC threads are still running in the background
          System.exit ( 0 );
          
      }
  
}
