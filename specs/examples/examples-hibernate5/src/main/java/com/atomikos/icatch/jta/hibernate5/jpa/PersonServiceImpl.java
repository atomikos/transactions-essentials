package com.atomikos.icatch.jta.hibernate5.jpa;

import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PersonServiceImpl implements PersonService {
  
  @Autowired
  private PersonRepository dao;
  
  @Autowired
  private TransactionManager txMgr;
  /**
   * {@inheritDoc}
   */
  
  @Transactional
  public Person create(Person p) {
    return dao.saveAndFlush(p);
  }
  
  /**
   * {@inheritDoc}
   * @throws SystemException 
   * @throws RollbackException 
   * @throws IllegalStateException 
   */
  
  @Transactional
  public Person createAndFailWithCompletion(Person p) throws IllegalStateException, RollbackException, SystemException {
    simulateHibernateFlushFailure();  
    return dao.saveAndFlush(p);
  }

  private void simulateHibernateFlushFailure() throws RollbackException,
		SystemException {
	txMgr.getTransaction().registerSynchronization(new Synchronization() {
      
      public void beforeCompletion() {
        throw new IllegalStateException("Simulated Hibernate flush failure");
      }
      
      public void afterCompletion(int status) {       
      }
      
    });
}
  
}
