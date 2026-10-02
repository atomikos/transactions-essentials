package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.transaction.annotation.Transactional;

/**
 * Demonstration of how Atomikos and Spring can make regular Java classes
 * transactional. Note that there is no explicit dependency on Atomikos.
 */
@Transactional
public class Bank {

	private final DataSource dataSource;

	public Bank(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@PostConstruct
	public void initDatabase() throws SQLException {
		try (Connection con = dataSource.getConnection();
				Statement s = con.createStatement();) {
			try {
				s.executeQuery("select * from Accounts");
			} catch (SQLException ex) {
				// table not there => create it
				System.err.println("Creating Accounts table...");
				s.executeUpdate(
						"create table Accounts (account VARCHAR ( 20 ), owner VARCHAR(300), balance DECIMAL (19,0) )");
				for (int i = 0; i < 100; i++) {
					s.executeUpdate(
							"insert into Accounts values ( " + "'account" + i + "' , 'owner" + i + "', 10000 )");
				}
			}
		}
	}

	//
	// Business methods are below
	//

	public long getBalance(int account) throws Exception {

		long res = -1;
		try (Connection con = dataSource.getConnection();
				Statement s = con.createStatement();) {
			String query = "select balance from Accounts where account='" + "account" + account + "'";
			ResultSet rs = s.executeQuery(query);
			if (rs == null || !rs.next())
				throw new SQLException("Account not found: " + account);
			res = rs.getLong(1);
		}

		return res;

	}

	public void withdraw(int account, int amount) throws Exception {

		try (Connection con = dataSource.getConnection();
				Statement s = con.createStatement();) {
			String sql = "update Accounts set balance = balance - " + amount + " where account ='account" + account + "'";
			s.executeUpdate(sql);
		}

	}

}
