
package jdbc.drivermanager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import jakarta.transaction.UserTransaction;

import org.junit.BeforeClass;
import org.junit.Test;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosNonXADataSourceBean;

import org.junit.Assert;

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

public class NonXaJdbcIntegrationTest
{
	
	  private static final int DEFAULT_ACCOUNT_ID = 50;

      //the unique resource identifier; change if needed.
      private static String resourceName = "NonXaDB";

      //the full name of the JDBC driver class
      //change if required
      private static String driverClassName = "org.h2.Driver";

      //the URL to connect with; this should be a valid DriverManager URL
      //change if needed
      private static String connectUrl = "jdbc:h2:~/NonXaDB";

     //the data source, set by getDataSource
      private static AtomikosNonXADataSourceBean ds  = null;

      @BeforeClass
      public static void createAccountsTableIfNecessary()
      throws Exception
      {
            boolean error = false;
            Connection conn = null;
            try {
                  conn = getConnectionWithinNewTransaction();
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
                closeConnectionAndEndTransaction ( conn , error );

            }

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
              ds.setUrl ( connectUrl );
              ds.setDriverClassName ( driverClassName );
              ds.setPoolSize ( 1 ); //optional
              ds.setBorrowConnectionTimeout ( 60 ); //optional

              //NOTE: the datasource can be bound in JNDI where available
          }

          return ds;
      }

      private static Connection getConnectionWithinNewTransaction()
      throws Exception
      {
          DataSource ds = getDataSource();
          Connection conn = null;
          //Retrieve or construct the UserTransaction
          //(the result can be bound in JNDI where available)
          UserTransaction utx = new UserTransactionImp();

          utx.begin();
          conn = ds.getConnection();

          return conn;

      }


      private static void closeConnectionAndEndTransaction ( Connection conn , boolean rollback )
      throws Exception
      {
          if ( conn != null ) conn.close();

          //get the UserTransaction by constructing a new
          //instance or by JNDI lookup where available
          UserTransaction utx = new UserTransactionImp();

          if ( rollback ) utx.rollback();
          else utx.commit();

      }


      public static long getBalance ( int account )
      throws Exception
      {
          long res = -1;
          boolean error = false;
          Connection conn = null;

          try {
              conn = getConnectionWithinNewTransaction();
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
              closeConnectionAndEndTransaction ( conn , error );
          }
          return res;
      }

      public static  String getOwner ( int account )
      throws Exception
      {
          String res = null;
          boolean error = false;
          Connection conn = null;

          try {
              conn = getConnectionWithinNewTransaction();
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
              closeConnectionAndEndTransaction ( conn , error );
          }
          return res;
      }

      public static void withdraw ( int account , int amount )
      throws Exception
      {
          boolean error = false;
          Connection conn = null;

          try {
              conn = getConnectionWithinNewTransaction();
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
              closeConnectionAndEndTransaction ( conn , error );

          }

      }


      public static void main ( String[] args )
      {
          try {
              //test if DB data has to be created
              createAccountsTableIfNecessary();

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
