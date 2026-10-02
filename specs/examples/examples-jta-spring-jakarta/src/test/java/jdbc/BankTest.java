
package jdbc;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Properties;

import jakarta.transaction.SystemException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class BankTest {

	@Configuration
	static class ContextConfiguration {

		@Bean(initMethod="init", destroyMethod="close")
		public AtomikosDataSourceBean dataSource() {
			AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
			dataSource.setLocalTransactionMode(true);
			dataSource.setUniqueResourceName("JDBC");
			dataSource.setXaDataSourceClassName("org.apache.derby.jdbc.EmbeddedXADataSource");
			Properties xaProperties  = new Properties();
			xaProperties.put("databaseName", "db");
			xaProperties.put("createDatabase", "create");
			dataSource.setXaProperties(xaProperties);
			// set properties, etc.
			return dataSource;
		}
		
		@Bean(initMethod="init", destroyMethod="close")
		public UserTransactionManager userTransactionManager() throws SystemException {
			UserTransactionManager userTransactionManager = new UserTransactionManager();
			userTransactionManager.setTransactionTimeout(300);
			userTransactionManager.setForceShutdown(true);
			return userTransactionManager;
		}
		
		@Bean
		public JtaTransactionManager jtaTransactionManager() throws SystemException {
			JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
			jtaTransactionManager.setTransactionManager(userTransactionManager()); //ref="userTransactionManager"
			jtaTransactionManager.setUserTransaction(userTransactionManager()); //ref="userTransactionManager"
			return jtaTransactionManager;
		}
		
		@Bean
		public Bank bank() {
			return new Bank(dataSource()); //ref="dataSource"
		}
	}

	@Autowired
	Bank bank;

	@Test
	public void testWithdraw() throws SQLException, Exception {
		int account = 10;
		long balance = bank.getBalance(account);
		System.out.println("Balance of account " + account + " is: " + balance);
		int amount = 100;
		System.out.println("Withdrawing " + amount + " of account " + account + "...");

		bank.withdraw(account, amount);
		long balance2 = bank.getBalance(account);
		System.out.println("New balance of account " + account + " is: " + balance);
		assertEquals(balance2, balance - amount);

	}
}
