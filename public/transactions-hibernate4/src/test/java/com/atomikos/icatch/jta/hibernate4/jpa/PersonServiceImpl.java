package com.atomikos.icatch.jta.hibernate4.jpa;

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
  private PersonDao dao;
  
  @Autowired
  private TransactionManager txMgr;
  
  /**
   * {@inheritDoc}
   */
  @Override
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
  @Override
  @Transactional
  public Person createAndFailWithCompletion1(Person p) throws IllegalStateException, RollbackException, SystemException {
    txMgr.getTransaction().registerSynchronization(new Synchronization() {
      
      /**
       * 
       */
      @Override
      public void beforeCompletion() {
        throw new IllegalStateException("FAIL1");
      }
      
      @Override
      public void afterCompletion(int status) {
        
      }
      
    });
    
    
    return dao.saveAndFlush(p);
  }
  
  /**
   * {@inheritDoc}
   * @throws SystemException 
   * @throws RollbackException 
   * @throws IllegalStateException 
   */
  @Override
  @Transactional
  public Person createAndFailWithCompletion2(Person p) throws IllegalStateException, RollbackException, SystemException {
    txMgr.getTransaction().registerSynchronization(new Synchronization() {
      
      /**
       * 
       */
      @Override
      public void beforeCompletion() {
        throw new IllegalStateException("FAIL2");
      }
      
      @Override
      public void afterCompletion(int status) {
        
      }
      
    });
    txMgr.getTransaction().registerSynchronization(new Synchronization() {
      
      /**
       * 
       */
      @Override
      public void beforeCompletion() {
        throw new IllegalStateException("FAIL3");
      }
      
      @Override
      public void afterCompletion(int status) {
        
      }
      
    });
    return dao.saveAndFlush(p);
  }
}
