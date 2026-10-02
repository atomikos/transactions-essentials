package com.atomikos.osgi.sample.internal.dao.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import com.atomikos.osgi.sample.internal.dao.AccountDao;

public class AccountDaoImpl implements AccountDao {

	private DataSource dataSource;
	private UserTransaction userTransaction;

	
	public String getOwner(int accno) throws Exception {
		String res = null;
		Connection conn = getConnection();

		Statement s = conn.createStatement();
		String query = "select owner from Accounts where account='" + "account" + accno + "'";
		ResultSet rs = s.executeQuery(query);
		if (rs == null || !rs.next())
			throw new Exception("Account not found: " + accno);
		res = rs.getString(1);
		s.close();
		closeConnection(conn, false);
		return res;
	}

	public void setDataSource(DataSource dataSource) {

		if (dataSource != null) {
			this.dataSource = dataSource;
		}
	}

	
	public void setUserTransaction(UserTransaction userTransaction) {
		this.userTransaction = userTransaction;
	}
	
	public void withdraw(int accno, int amount) throws Exception {

		Connection conn = getConnection();

		Statement s = conn.createStatement();
		String sql = "update Accounts set balance = balance - " + amount + " where account ='account" + accno + "'";
		s.executeUpdate(sql);
		s.close();
		closeConnection(conn, false);

	}
	

	public long getBalance(int accno) throws Exception {
		long res = 0;
		Connection conn = getConnection();

		Statement s = conn.createStatement();
		String query = "select balance from Accounts where account='" + "account" + accno + "'";
		ResultSet rs = s.executeQuery(query);
		if (rs == null || !rs.next())
			throw new Exception("Account not found: " + accno);
		res = rs.getLong(1);
		s.close();
		closeConnection(conn, false);
		return res;
	}


	
	/**
	 * Setup DB tables if needed.
	 */

	public void checkTables()

	{
		boolean error = false;
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (Exception noConnect) {
			noConnect.printStackTrace();
			System.err.println("Failed to connect.");
			System.err.println("PLEASE MAKE SURE THAT DERBY IS INSTALLED AND RUNNING");
			throw new RuntimeException(noConnect);
		}
		try {

			Statement s = conn.createStatement();
			try {
				s.executeQuery("select * from Accounts");
			} catch (SQLException ex) {
				// table not there => create it
				System.err.println("Creating Accounts table...");
				s.executeUpdate("create table Accounts ( "
						+ " account VARCHAR ( 20 ), owner VARCHAR(300), balance DECIMAL (19,0) )");
				for (int i = 0; i < 100; i++) {
					s.executeUpdate("insert into Accounts values ( " + "'account" + i + "' , 'owner" + i + "', 10000 )");
				}
			}
			s.close();
		} catch (Exception e) {
			error = true;
			throw new RuntimeException(e);
		} finally {
			closeConnection(conn, error);

		}

		// That concludes setup

	}

	/**
	 * Utility method to close the connection and terminate the transaction.
	 * This method does all XA related tasks and should be called within a
	 * transaction. When it returns, the transaction will be terminated.
	 * 
	 * @param conn
	 *            The connection.
	 * @param error
	 *            Indicates if an error has occurred or not. If true, the
	 *            transaction will be rolled back. If false, the transaction
	 *            will be committed.
	 */

	private void closeConnection(Connection conn, boolean error) {
		try {
			if (conn != null)
				conn.close();

			if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
				if (error)
					userTransaction.rollback();
				else
					userTransaction.commit();
			} else
				System.out.println("WARNING: closeConnection called outside a tx");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Utility method to start a transaction and get a connection.
	 * 
	 * @return Connection The connection.
	 */

	private Connection getConnection() {
		DataSource ds = getDataSource();
		Connection conn = null;

		// Retrieve of construct the UserTransaction
		// (can be bound in JNDI where available)
		try {
			userTransaction.setTransactionTimeout(60);

			// First, create a transaction
			userTransaction.begin();
			conn = ds.getConnection();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return conn;

	}

	public DataSource getDataSource() {
		if (dataSource != null) {
			return dataSource;
		}
		// else :
		throw new RuntimeException("No dataSource available, please come back later...");
	}

	
}
