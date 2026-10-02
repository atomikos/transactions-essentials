package jms;

import static org.awaitility.Awaitility.await;

import java.util.Properties;

import javax.jms.MapMessage;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.transaction.SystemException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.testcontainers.activemq.ArtemisContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.atomikos.jms.extra.MessageDrivenContainer;

import jdbc.Bank;

@ExtendWith(SpringExtension.class)
@Transactional
public class JmsBankTest {

	private static final String BANK_QUEUE_NAME = "BANK_QUEUE";
	
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
			.withCopyFileToContainer(
					MountableFile.forClasspathResource("init-db.sql"), "/docker-entrypoint-initdb.d/init-db.sql");

	static ArtemisContainer artemis = new ArtemisContainer("apache/activemq-artemis:2.30.0-alpine")
		    .withEnv("ANONYMOUS_LOGIN", "true");
		    
	@BeforeAll
	static void beforeAll() {
		postgres.start();
		artemis.start();
	}
	
	@AfterAll
	static void afterAll() {
		postgres.stop();
		artemis.stop();
	}
	
	@Configuration
	static class ContextConfiguration {

		@Bean(initMethod="init", destroyMethod="close")
		AtomikosDataSourceBean dataSource() {
			AtomikosDataSourceBean ds = new AtomikosDataSourceBean(); 
			ds.setUniqueResourceName("JmsBankTest"); 
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
		
		@Bean
		MessageDrivenBank messageDrivenBank() {
			return new MessageDrivenBank(bank());
		}
		
		@Bean(destroyMethod="close")
		AtomikosConnectionFactoryBean atomikosConnectionFactoryBean(){
			AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
			atomikosConnectionFactoryBean.setUniqueResourceName("QUEUE_BROKER");
			atomikosConnectionFactoryBean.setXaConnectionFactory(new ActiveMQXAConnectionFactory(artemis.getBrokerUrl()));
			return atomikosConnectionFactoryBean;
		}
		
		@Bean(initMethod="start", destroyMethod="stop")
		MessageDrivenContainer messageDrivenContainer() {
			MessageDrivenContainer messageDrivenContainer = new MessageDrivenContainer();
			messageDrivenContainer.setAtomikosConnectionFactoryBean(atomikosConnectionFactoryBean());
			messageDrivenContainer.setTransactionTimeout(10);
			messageDrivenContainer.setMessageListener(messageDrivenBank());
			messageDrivenContainer.setDestination(new ActiveMQQueue(BANK_QUEUE_NAME));
			return messageDrivenContainer;
		}
	}
	
	@Autowired
	Bank bank;

	@Autowired
	MessageDrivenContainer messageDrivenContainer;

	@Test
	public void testWithdrawViaJms() throws Exception {
		int account = 10;
		int amount=100;

		long balance = bank.getBalance ( account );
        System.out.println ( "Balance of account "+account+" is: " + balance );
		
		try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory()) {
			cf.setBrokerURL(artemis.getBrokerUrl());
			QueueConnection c = cf.createQueueConnection();
			QueueSession session = c.createQueueSession(true, 0);
			QueueSender sender = session.createSender(new ActiveMQQueue(BANK_QUEUE_NAME));
			MapMessage m = session.createMapMessage();

			m.setIntProperty("account", account);
			m.setIntProperty("amount", 100);
			sender.send(m);
			session.commit();
			session.close();
			c.close();
		}
		await().until(() -> {
			long newBalance = bank.getBalance ( account );
	        System.out.println ( "New balance of account "+account+" is: " + newBalance );
			return newBalance == balance-amount ;
		});
		messageDrivenContainer.stop(); //clean stop
	}
	
}
