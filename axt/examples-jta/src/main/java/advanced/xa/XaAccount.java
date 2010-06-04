package advanced.xa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.jdbc.JdbcTransactionalResource;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;

 /**
  *
  *
  *A demo program that uses XA-level integration with
  *TransactionsEssentials. Specific about this example is that it uses
  *explicit recovery of resources (i.e., resources are recovered
  *when they become available, instead of when the are first
  *used by the application). This works by registering an
  *Atomikos-specific adapter with the transaction service.
  *Also, this example shows how you can explicitly start and
  *stop the transaction service.  Although this example shows
  *JDBC, the same Atomikos-specific pattern applies to JMS or JCA.
  *
  *Usage: java XaAccount <account number> <operation> [<amount>]<br>
  *where:<br> 
  *account number is an integer between 0 and 99<br>
  *and operation is one of (balance, owner, withdraw, deposit).<br>
  *In case of withdraw and deposit, an extra (integer) amount is expected.
  */
  
public class XaAccount
{
      //the user name in the database; change if needed.
      //the current settings are empty strings
      //private static String user = "demo";
      
      //the password for the user; change if needed.
      //the current settings are empty strings, 
      //for cloudscape embedded database
      //private static String passwd = "";
      
      //the unique resource name for the DB; change if needed
      private static String resourceName = "samples/jdbc/ExamplesJdbcPooledDatabase";
      
      //the data source, set by getXADataSource
      private static XADataSource xads = null;

      //the handle to the transaction service
      private static UserTransactionService uts = null;

      
      /**
        *Initialize the TM, and setup DB tables if needed.
        */
        
      private static void setup()
      throws Exception
      {
            
            //STEP 1: DB-specific setup of XADataSource
    	 
    	  org.apache.derby.jdbc.EmbeddedXADataSource derbyDS = new org.apache.derby.jdbc.EmbeddedXADataSource();
    	  derbyDS.setDatabaseName("sampledb");
    	  derbyDS.setCreateDatabase("create");
    	  
            xads = derbyDS;
            
            //STEP 2: Create the Atomikos-specific
            //resource to register with the
            //transaction service; this is essential for
            //recovering at startup of TM (otherwise, the TM
            //has no idea which resources to recover until they
            //are enlisted at some time in the future - as in the
            //simple xa demo). 
            JdbcTransactionalResource resource =
                new JdbcTransactionalResource ( 
                resourceName , xads );

            //NOTE: there are equivalent resources available
            //for JMS (com.atomikos.datasource.xa.jms.JmsTransactionalResource)
            //and JCA (com.atomikos.datasource.xa.jca.JcaTransactionalResource)
            //you can use those for JMS or JCA integration
            
            //STEP 3: Register the resource with the transaction service
            //this is done through the UserTransaction handle.
            //All UserTransaction instances are equivalent and each
            //one can be used to register a resource at any time.
            uts = new UserTransactionServiceImp();
            uts.registerResource ( resource );
            
            //STEP 4: Initialize the UserTransactionService.
            //This will start the TM and recover
            //all registered resources; you could
            //call this 'eager recovery' (as opposed to 'lazy recovery'
            //for the simple xa demo).
            TSInitInfo info = uts.createTSInitInfo();
            //optionally set config properties on info; not shown here
            uts.init ( info );

            //Any resource added here will be recovered instantly.
            
            //From here on, the transaction service is running...
            
            //Below is application-specific setup
            TransactionManager tm = uts.getTransactionManager();
            tm.setTransactionTimeout ( 60 );
            
            
            boolean error = false;
            XAConnection xaconn = null;
            try {
                xaconn = getConnection();
            }
            catch ( Exception noConnect ) {
                System.err.println ( "Failed to connect." );
                System.err.println ( "PLEASE MAKE SURE THAT FIRSTSQL IS INSTALLED AND RUNNING" );
                throw noConnect;
            }

            try {
                  
                  Statement s = xaconn.getConnection().createStatement();
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
                if ( xaconn != null ) closeConnection ( xaconn , error );
               
            }
            
            //That concludes setup
           
      }
      
      
      /**
        *Shutdown the TM and the datasource.
        */
        
      private static void shutdown()
      throws Exception
      {
          
          if ( uts != null ) uts.shutdown ( true );
          
         
      }
            
      //
      //THE CODE BELOW SHOULD NOT BE CHANGED;
      //IT SHOULD WORK ON ANY JDBC COMPLIANT SYSTEM
      //
      
      /**
        *Gets the xa datasource instance. 
        *
        *@return XADataSource The data source.
        */
        
      private static XADataSource getXADataSource()
      throws Exception
      {
            return xads;
      }
      
       /**
        *Utility method to start a transaction and
        *get a connection. 
        *This method does all XA related tasks.
        *@return XAConnection The xa connection.
        */
        
      private static XAConnection getConnection()
      throws Exception
      {
          XADataSource xads = getXADataSource();
          XAConnection xaconn = null;
          //retrieve the TM
          TransactionManager tm = getTransactionManager();
          
          //First, create a transaction
          tm.begin();

                    
          //xaconn = xads.getXAConnection ( user , passwd );
          xaconn = xads.getXAConnection (  );
          XAResource xares = xaconn.getXAResource();
               
          
          //get the current tx
          Transaction tx = tm.getTransaction();
          //enlist
          tx.enlistResource ( xares );
          return xaconn;
          
      }
      
      /**
        *Utility method to close the connection and
        *terminate the transaction. 
        *This method does all XA related tasks
        *and should be called within a transaction.
        *When it returns, the transaction will be terminated.
        *@param xaconn The xa connection.
        *@param error Indicates if an error has
        *occurred or not. If true, the transaction will be rolled back.
        *If false, the transaction will be committed.
        */
        
      private static void closeConnection ( XAConnection xaconn , boolean error )
      throws Exception
      {
          int flag = XAResource.TMSUCCESS;
           XAResource xares = xaconn.getXAResource();
          //retrieve the TM
          TransactionManager tm = getTransactionManager();
          
          //get the current tx
          Transaction tx = tm.getTransaction();
          //closeConnection
          if ( error ) 
              flag = XAResource.TMFAIL;
          tx.delistResource ( xares , flag );
          //close the JDBC user connection
          xaconn.getConnection().close();
          
          if ( error )
                  tm.rollback();
              else 
                  tm.commit(); 
                  
          xaconn.close();
      }
      
      private static TransactionManager getTransactionManager()
      {
          return uts.getTransactionManager(); 
      }
      
    
    
      
      private static long getBalance ( int account )
      throws Exception
      {
          long res = -1;
          boolean error = false;
          TransactionManager tm = getTransactionManager();
          XAConnection xaconn = null;
          
          try {
              xaconn = getConnection();
              Statement s = xaconn.getConnection().createStatement();
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
              closeConnection ( xaconn , error );
          }
          return res;
      }
      
      private static  String getOwner ( int account )
      throws Exception
      {
          String res = null;
          boolean error = false;
          TransactionManager tm = getTransactionManager();
          XAConnection xaconn = null;
          
          try {
              xaconn = getConnection();
              Statement s = xaconn.getConnection().createStatement();
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
              closeConnection ( xaconn , error );
          }
          return res;
      }
      
      private static void withdraw ( int account , int amount )
      throws Exception
      {
          boolean error = false;
          TransactionManager tm = getTransactionManager();
          XAConnection xaconn = null;
          
          try {
              xaconn = getConnection();
              Statement s = xaconn.getConnection().createStatement();
              
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
              closeConnection ( xaconn , error );
             
          }
            
      }
  
  
      public static void main ( String[] args )
      {
          try {
              //test if DB data has to be created
              setup();
              
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
              
              shutdown();
              
          }
          catch ( Exception e ) {
              e.printStackTrace(); 
          }
          
      }
  
}
