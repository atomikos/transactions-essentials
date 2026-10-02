package com.atomikos.icatch.jta.hibernate5;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.UnexpectedRollbackException;

import com.atomikos.icatch.jta.hibernate5.jpa.Person;
import com.atomikos.icatch.jta.hibernate5.jpa.PersonService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/spring-atomikos-j2ee.xml")
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class })
public class J2eePlatformIntegrationTest {
    
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
  public void testFailureUponHibernateFlushYieldsRollback() throws IllegalStateException, RollbackException, SystemException {
    Person p = new Person();
    p.setName("Atomikos-J2EE");
    p = service.createAndFailWithCompletion(p);
  }
  
}
