package jdbc.xadatasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.junit.Before;
import org.junit.Test;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;

import junit.framework.Assert;

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

public class XaJdbcIntegrationTest
{

      //the globally unique resource name for the DB; change if needed
      private static String resourceName = "simple.jdbc.xadatasource.AccountTest";

      //the data source, set by getDataSource
      private static AtomikosDataSourceBean ds  = null;


      /**
        *Setup DB tables if needed.
        */

      @Before
  	public   void checkTables()
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
                System.err.println ( "PLEASE MAKE SURE THAT DERBY IS INSTALLED AND RUNNING" );
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

              ds.setXaDataSourceClassName ( "org.apache.derby.jdbc.EmbeddedXADataSource");
              Properties properties = new Properties();
             properties.put("databaseName",  "db");
             properties.put("createDatabase", "create");
              ds.setXaProperties(properties);

              //REQUIRED: properties to set on the XA datasource class
             // ds.getXaProperties().setProperty("user", "demo");
              //REQUIRED: unique resource name for transaction recovery configuration
              ds.setUniqueResourceName ( resourceName );
              //OPTIONAL: what is the pool size?
              ds.setPoolSize ( 10 );
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



      public static long getBalance ( int account )
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


      public static void withdraw ( int account , int amount )
      throws Exception
      {
          boolean error = false;
          Connection conn = null;

          try {
              conn = getConnection();
              PreparedStatement s = conn.prepareStatement("update Accounts set balance = balance - ? where account = ?");
              s.setInt(1, amount);
              s.setString(2, "account"+account);
              s.execute();
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



	private static final int DEFAULT_ACCOUNT_ID = 50;


	@Test
	public void deposit50OnAccount50() throws Exception {
		long amount1 = getBalance(DEFAULT_ACCOUNT_ID);

		withdraw(DEFAULT_ACCOUNT_ID, -50);

		long amount2 = getBalance(DEFAULT_ACCOUNT_ID);

		Assert.assertEquals(amount1 + 50, amount2);

	}


	@Test
	public void withdraw50OnAccount50() throws Exception {
		long amount1 = getBalance(DEFAULT_ACCOUNT_ID);

		withdraw(DEFAULT_ACCOUNT_ID, 50);

		long amount2 = getBalance(DEFAULT_ACCOUNT_ID);

		Assert.assertEquals(amount1 - 50, amount2);

	}

}
