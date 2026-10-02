package jms;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.XAConnectionFactory;
import javax.transaction.SystemException;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import jdbc.Bank;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class JmsBankTest {

	
	@Configuration
	static class ContextConfiguration {

		@Bean(initMethod="start", destroyMethod="stop")
		public BrokerService brokerService() throws Exception {
			BrokerService broker = new BrokerService();
			broker.setPersistenceAdapter(new org.apache.activemq.store.memory.MemoryPersistenceAdapter());
			broker.addConnector("vm://localhost");
			return broker;
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
			return new ActiveMQXAConnectionFactory("vm://localhost");
		}
		
		@Bean 
		public Queue queue() {
			return new org.apache.activemq.command.ActiveMQQueue("BANK_QUEUE");
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

		String url = "vm://localhost";
		ActiveMQQueue queue = new ActiveMQQueue();
		queue.setPhysicalName("BANK_QUEUE");
		ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
		cf.setBrokerURL(url);
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

		Thread.sleep(10000);
		long newBalance = bank.getBalance ( account );
        System.out.println ( "New balance of account "+account+" is: " + newBalance );
        assertEquals(newBalance,balance-amount );
        pool.stop();
	}

	
}
