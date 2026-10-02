package jms;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.atomikos.jms.extra.MessageDrivenContainer;

import jakarta.jms.MapMessage;
import jakarta.jms.Queue;
import jakarta.jms.QueueConnection;
import jakarta.jms.QueueSender;
import jakarta.jms.QueueSession;
import jakarta.jms.XAConnectionFactory;
import jakarta.transaction.SystemException;
import jdbc.Bank;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class JmsBankTest {

	private static final String BROKER_URL = "vm://0";	
	@org.springframework.context.annotation.Configuration
	static class ContextConfiguration {

		@Bean(initMethod="start", destroyMethod="stop")
		public EmbeddedActiveMQ brokerService() throws Exception {
			EmbeddedActiveMQ embedded = new EmbeddedActiveMQ();
			Configuration config = new ConfigurationImpl();
			config.setSecurityEnabled(false);
			config.addAcceptorConfiguration("in-vm", BROKER_URL);
			config.setPersistenceEnabled(false);
			embedded.setConfiguration(config);
			return embedded;
		}
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
		
		@Bean
		@DependsOn("brokerService")		
		public XAConnectionFactory xaFactory() {
			return new ActiveMQXAConnectionFactory(BROKER_URL);
		}
		
		@Bean 
		public Queue queue() {
			return new ActiveMQQueue("BANK_QUEUE");
		}
		
		@Bean
		public MessageDrivenBank messageDrivenBank() {
			return new MessageDrivenBank(bank());
		}
		
		@Bean(destroyMethod="close")
		public AtomikosConnectionFactoryBean atomikosConnectionFactoryBean(){
			AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
			atomikosConnectionFactoryBean.setUniqueResourceName("QUEUE_BROKER");
			atomikosConnectionFactoryBean.setXaConnectionFactory(xaFactory());
			return atomikosConnectionFactoryBean;
		}
		
		@Bean(initMethod="start", destroyMethod="stop")
		public MessageDrivenContainer messageDrivenContainer() {
			MessageDrivenContainer messageDrivenContainer = new MessageDrivenContainer();
			messageDrivenContainer.setAtomikosConnectionFactoryBean(atomikosConnectionFactoryBean());
			messageDrivenContainer.setTransactionTimeout(10);
			messageDrivenContainer.setMessageListener(messageDrivenBank());
			messageDrivenContainer.setDestination(queue());
			return messageDrivenContainer;
		}
		
	}
	
	@Autowired
	@Qualifier("bank")
	Bank bank;

	@Autowired
	@Qualifier("messageDrivenContainer")
	MessageDrivenContainer pool;

	@Test
	public void testWithdrawViaJms() throws Exception {
		int account = 10;
		int amount=100;

		long balance = bank.getBalance ( account );
        System.out.println ( "Balance of account "+account+" is: " + balance );

		ActiveMQQueue queue = new ActiveMQQueue("BANK_QUEUE");

		ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
		cf.setBrokerURL(BROKER_URL);
		QueueConnection c = cf.createQueueConnection();

		QueueSession session = c.createQueueSession(true, 0);
		QueueSender sender = session.createSender(queue);
		MapMessage m = session.createMapMessage();

		m.setIntProperty("account", account);
		m.setIntProperty("amount", 100);
		sender.send(m);
		session.commit();
		session.close();
		c.close();
		cf.close();
		Thread.sleep(2000);
		long newBalance = bank.getBalance ( account );
        System.out.println ( "New balance of account "+account+" is: " + newBalance );
        assertEquals(newBalance,balance-amount );
        pool.stop();
	}

	
}
