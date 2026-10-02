package payment;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;

public class PaymentJdbc {

	private static final String resourceName = "paymentDB";


	// The transaction service
	private UserTransactionService uts = null;

	private AtomikosDataSourceBean ds = null;

	private Connection getConnection() throws Exception {
		DataSource ds = getDataSource();
		Connection conn = null;
		// retrieve the TM
		TransactionManager tm = new UserTransactionManager();
		tm.setTransactionTimeout(5000);
		// First, create a transaction
		// NOTE: for a remote invocation, this will be a SUBtransaction!
		tm.begin();
		conn = ds.getConnection();

		return conn;

	}

	private DataSource getDataSource() {
		if (ds == null) {
			ds = new AtomikosDataSourceBean();
			ds.setLocalTransactionMode(true);
			ds.setUniqueResourceName(resourceName);
			ds.setXaDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
			Properties xaProperties = new Properties();
			xaProperties.setProperty("URL", "jdbc:h2:mem:payment");
			xaProperties.setProperty("user", "sa");
			ds.setXaProperties(xaProperties);
			// OPTIONAL pool size
			ds.setPoolSize(5);
			// OPTIONAL timeout in secs between pool cleanup tasks
			ds.setBorrowConnectionTimeout(60);

			// NOTE: the datasource can be bound in JNDI where available
		}

		return ds;
	}

	

	private static void closeConnection(Connection conn, boolean error)
			throws Exception {
		if (conn != null)
			conn.close();

		// retrieve the TM
		TransactionManager tm = new UserTransactionManager();

		if (error)
			tm.rollback();
		else
			tm.commit();

	}

	public void populateDb() throws Exception {
		boolean error = false;
		Connection conn = null;

		// we need to use the usertransactionservice to
		// be able to export/import txs
		uts = new UserTransactionServiceImp();
		uts.init();

		try {
			conn = getConnection();
			Statement s = conn.createStatement();
			try {
				s.executeQuery("select * from Payments");
			} catch (SQLException ex) {
				// table not there => create it
				System.err.println("Creating Payments table...");
				s.executeUpdate("create table Payments (  cardno VARCHAR ( 20 ), amount DECIMAL (19,0) )");
				for (int i = 0; i < 100; i++) {
					s.executeUpdate("insert into Payments values ( " + "'card"+ i + "' , 0 )");
				}
			}
			s.close();
		} catch (Exception e) {
			error = true;
			throw e;
		} finally {
			closeConnection(conn, error);

		}
	}

	public boolean performPayment(Payment payment) throws Exception {
		boolean error = false;

		Connection conn = null;

		try {
			conn = getConnection();
			Statement s = conn.createStatement();
			String sql = "update Payments set amount = amount + "
					+ payment.getAmount() + " where cardno ='" + payment.getCardno()
					+ "'";
			int count = s.executeUpdate(sql);
			if (count == 0)
				throw new Exception("Invalid card: " + payment.getCardno());

			s.close();
		} catch (Exception e) {
			error = true;
			throw e;
		} finally {
			try {
				closeConnection(conn, error);
			} catch (Exception e) {
				error = true;
			}
		}
		return error;
	}
}
