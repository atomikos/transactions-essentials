
package jdbc;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Properties;

import javax.transaction.SystemException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.testcontainers.containers.PostgreSQLContainer;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;

@ExtendWith(SpringExtension.class)
@Transactional
public class BankTest {

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

	@BeforeAll
	static void beforeAll() {
		postgres.start();
	}

	@AfterAll
	static void afterAll() {
		postgres.stop();
	}

	@Configuration
	static class ContextConfiguration {

		@Bean(initMethod="init", destroyMethod="close")
		AtomikosDataSourceBean dataSource() {
			AtomikosDataSourceBean ds = new AtomikosDataSourceBean(); 
			ds.setUniqueResourceName("BankTest"); 
			ds.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource"); 
			Properties p = new Properties(); 
			p.setProperty ( "user" , postgres.getUsername() ); 
			p.setProperty ( "password" , postgres.getPassword() ); 
			p.setProperty ( "serverName" , postgres.getHost() ); 
			p.setProperty ( "databaseName" , postgres.getDatabaseName() ); 
			p.setProperty ( "portNumber" , String.valueOf(postgres.getMappedPort(5432)) ); 
			ds.setXaProperties ( p ); 
			ds.setPoolSize(5);
			return ds;
		}
		
		@Bean(initMethod="init", destroyMethod="close")
		UserTransactionManager userTransactionManager() throws SystemException {
			UserTransactionManager userTransactionManager = new UserTransactionManager();
			userTransactionManager.setTransactionTimeout(300);
			userTransactionManager.setForceShutdown(true);
			return userTransactionManager;
		}
		
		@Bean
		JtaTransactionManager jtaTransactionManager() throws SystemException {
			JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
			jtaTransactionManager.setTransactionManager(userTransactionManager()); //ref="userTransactionManager"
			jtaTransactionManager.setUserTransaction(userTransactionManager()); //ref="userTransactionManager"
			return jtaTransactionManager;
		}
		
		@Bean
		Bank bank() {
			return new Bank(dataSource()); //ref="dataSource"
		}
	}

	@Autowired
	Bank bank;

	@Test
	@Rollback(false)
	public void testWithdraw() throws SQLException, Exception {
		int account = 10;
		long balance = bank.getBalance(account);
		System.out.println("Balance of account " + account + " is: " + balance);
		int amount = 100;
		System.out.println("Withdrawing " + amount + " of account " + account + "...");

		bank.withdraw(account, amount);
		long balance2 = bank.getBalance(account);
		System.out.println("New balance of account " + account + " is: " + balance2);
		assertEquals(balance2, balance - amount);
	}
}
