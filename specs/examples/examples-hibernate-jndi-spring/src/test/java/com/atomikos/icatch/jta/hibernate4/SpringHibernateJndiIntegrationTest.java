package com.atomikos.icatch.jta.hibernate4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.atomikos.icatch.jta.hibernate4.jpa.Person;
import com.atomikos.icatch.jta.hibernate4.jpa.PersonRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/spring-atomikos-standalone.xml"})
public class SpringHibernateJndiIntegrationTest {
  
  @Autowired
  private PersonRepository personDao;
  
  @Test
  public void test() {
    Person p = new Person();
    p.setName("Atomikos-Standalone");
    p = personDao.save(p);
    Assert.assertNotNull(p.getId());
    p = personDao.findOne(p.getId());
    Assert.assertEquals("Atomikos-Standalone", p.getName());
  }
  
}
