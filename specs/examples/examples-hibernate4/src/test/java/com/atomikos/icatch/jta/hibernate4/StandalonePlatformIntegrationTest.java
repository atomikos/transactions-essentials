package com.atomikos.icatch.jta.hibernate4;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.icatch.jta.hibernate4.jpa.Person;
import com.atomikos.icatch.jta.hibernate4.jpa.PersonService;
import com.atomikos.icatch.jta.hibernate4.jpa.PersonServiceImpl;
import com.atomikos.jdbc.AtomikosDataSourceBean;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
//@ContextConfiguration(locations = "classpath:spring/spring-atomikos-standalone.xml")
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class })
public class StandalonePlatformIntegrationTest {

	@EnableJpaRepositories(basePackages="com.atomikos.icatch.jta.hibernate4.jpa")
	@Configuration
	@EnableTransactionManagement
	static class ContextConfiguration {

		@Bean
		public JdbcDataSource xaReferent() {
			JdbcDataSource jdbcDataSource = new JdbcDataSource();
			jdbcDataSource.setUrl("jdbc:h2:~/test-db;MODE=PostgreSQL;MVCC=TRUE;DB_CLOSE_DELAY=-1");
			jdbcDataSource.setUser("sa");
			return jdbcDataSource;
		}
		@Bean(initMethod = "init", destroyMethod = "close")
		public AtomikosDataSourceBean dataSource() {
			AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
			dataSource.setLocalTransactionMode(true);
			dataSource.setUniqueResourceName("atomikos-standalone");
			dataSource.setXaDataSource(xaReferent());
			return dataSource;
		}

		@Bean(initMethod = "init", destroyMethod = "close")
		public TransactionManager txManager() throws SystemException {
			return new UserTransactionManager();
		}
		
		@Bean
		public UserTransaction userTx() throws SystemException {
			return  new UserTransactionImp();
		}

		@Bean
		public JtaTransactionManager transactionManager() throws SystemException {
			JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
			jtaTransactionManager.setTransactionManager(txManager()); 
			jtaTransactionManager.setUserTransaction(userTx()); 
			return jtaTransactionManager;
		}
		
		@Bean
		   public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		      LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		      em.setJtaDataSource(dataSource());
		      em.setPackagesToScan("com.atomikos.icatch.jta.hibernate4.jpa");
		      
		      HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		      vendorAdapter.setDatabase(Database.H2);
		      em.setJpaVendorAdapter(vendorAdapter);
		      Map<String, String> jpaProperties = new HashMap<>();
		      jpaProperties.put("hibernate.current_session_context_class","jta");
		      jpaProperties.put("javax.persistence.transactionType","jta");
		      jpaProperties.put("hibernate.transaction.jta.platform","com.atomikos.icatch.jta.hibernate4.AtomikosPlatform");
		      jpaProperties.put("hibernate.hbm2ddl.auto","create");
		      jpaProperties.put("hibernate.search.autoregister_listeners","false");
		      em.setJpaPropertyMap(jpaProperties );
		 
		      return em;
		   }
			@Bean
			public PersonService service() {
				return new PersonServiceImpl();
			}
		
	}

	@Autowired
	private PersonService service;

	@Test
	public void testCommit() {
		Person p = new Person();
		p.setName("Atomikos-J2EE");
		p = service.create(p);
		Assert.assertNotNull(p.getId());
	}

	@Test(expected = UnexpectedRollbackException.class)
	public void testFailureUponHibernateFlushYieldsRollback()
			throws IllegalStateException, RollbackException, SystemException {
		Person p = new Person();
		p.setName("Atomikos-J2EE");
		p = service.createAndFailWithCompletion(p);
	}

}
