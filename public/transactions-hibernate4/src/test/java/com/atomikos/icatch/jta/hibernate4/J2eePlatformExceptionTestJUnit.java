package com.atomikos.icatch.jta.hibernate4;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.UnexpectedRollbackException;

import com.atomikos.icatch.jta.hibernate4.jpa.Person;
import com.atomikos.icatch.jta.hibernate4.jpa.PersonService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/spring-atomikos-j2ee.xml")
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class })
public class J2eePlatformExceptionTestJUnit {
  
  private static final Logger LOG = LoggerFactory.getLogger(J2eePlatformExceptionTestJUnit.class);
  
  @Autowired
  private PersonService service;
  
  @Test
  public void test() {
    Person p = new Person();
    p.setName("Atomikos-J2EE");
    p = service.create(p);
    Assert.assertNotNull(p.getId());
  }
  
  @Test(expected = UnexpectedRollbackException.class)
  public void testFail1() throws IllegalStateException, RollbackException, SystemException {
    Person p = new Person();
    p.setName("Atomikos-J2EE");
    p = service.createAndFailWithCompletion1(p);
  }
  
  @Test
  public void testFail2() throws IllegalStateException, RollbackException, SystemException {
    Person p = new Person();
    p.setName("Atomikos-J2EE");
    try {
      p = service.createAndFailWithCompletion2(p);
    } catch (UnexpectedRollbackException e) {
      // the first exception gets transported, others got logged @error level
      Assert.assertEquals("FAIL3", e.getCause().getCause().getMessage());
      LOG.error("This is the trace the user can log to see the actual first error.", e);
    }
  }
}
