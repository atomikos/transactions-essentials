package com.atomikos.examples.hibernate;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.atomikos.examples.hibernate.entities.User;
import com.atomikos.jdbc.AtomikosDataSourceBean;


public class HibernateAtomikosDataSourceIntegrationTest {
	
	private static UserTransaction userTransaction = new com.atomikos.icatch.jta.UserTransactionImp();
	private static AtomikosDataSourceBean dataSource1;
	private static AtomikosDataSourceBean dataSource2;

	private static SessionFactory sf1;
	private static SessionFactory sf2;
	@BeforeClass
	public static void configure() throws Exception {
		dataSource1 = createDataSource("derby1","users1");
		sf1 = new Configuration().configure("/hibernate1.cfg.xml").buildSessionFactory();
		
		dataSource2 = createDataSource("derby2","users2");
		sf2 = new Configuration().configure("/hibernate2.cfg.xml").buildSessionFactory();
		
	}

	@Test
	public void performCommit() throws SystemException, NotSupportedException {
		userTransaction.setTransactionTimeout(60);
		userTransaction.begin();
		
		try {
			Session session1 = sf1.getCurrentSession();
			persistUser(session1, "user1");
			Session session2 = sf2.getCurrentSession();
			persistUser(session2, "user2");

			userTransaction.commit();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			userTransaction.rollback();
		}
		
		Session session1 = sf1.openSession();
		assertUsernameIs(session1,"user1");
		
		Session session2 = sf2.openSession();
		assertUsernameIs(session2,"user2");
		
		System.err.println("Both databases updated successfully");
	}
	@Test
	public void performRollback() throws SystemException, NotSupportedException {
		//need to be sure a User is present.
		performCommit();
		
		User user = null;
		userTransaction.setTransactionTimeout(60);
		userTransaction.begin();
		
		try {
			Session session = sf1.getCurrentSession();			

			user = (User) session.get(User.class, 1L);

			user.setName("bad name");;
			session.persist(user);
			userTransaction.rollback();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			userTransaction.rollback();
		}
		
		Session session = sf1.openSession();
		assertUsernameIs(session,"user1");
		
		
		System.err.println("performRollback");
	}

	private void assertUsernameIs(Session session, String username) {
		User user = (User) session.get(User.class, 1L);
		Assert.assertEquals(username,user.getName());
	}

	@AfterClass
	public static void closeAll() {
		sf1.close();
		sf2.close();
		dataSource1.close();
		dataSource2.close();
	}

	/**
	 * Perform some work. In this scenario, we get a session from the specified
	 * SessionFactory then we create a new user with the specified username
	 * then list all users.  
	 */
	private  void persistUser(SessionFactory sf, String username) {
		Session session = sf.getCurrentSession();
		persistUser(session, username);
		listUsers(session);
		session.close();
	}

	/**
	 * Dump all users to stdout using the specified Hibernate session. 
	 */
	private  void listUsers(Session session) {
		List users = session.createQuery("from User").list();
		for (int i=0; i<users.size() ;i++) {
			User user = (User) users.get(i);
			
			System.out.println(user.toString());
		}
	}

	/**
	 * Create a new user with specified name using the specified Hibernate session.
	 */
	private  void persistUser(Session session, String userName) {
		User u = new User();
		u.setName(userName);
		session.save(u);
	}


	private static AtomikosDataSourceBean createDataSource(String resourceName, String databaseName) throws SQLException {
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		ds.setUniqueResourceName(resourceName);
		ds.setXaDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
		Properties props = new Properties();
		props.put("url","jdbc:h2:~/"+ databaseName);
		props.put("user", databaseName);
		ds.setXaProperties(props);
		ds.setPoolSize(3);
		ds.setLocalTransactionMode(true);
		ds.init();
		return ds;
	}
	

}
