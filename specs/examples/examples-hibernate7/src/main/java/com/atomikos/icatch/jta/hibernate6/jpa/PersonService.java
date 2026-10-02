package com.atomikos.icatch.jta.hibernate6.jpa;

import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;


public interface PersonService {
  
  public Person create(Person p);
  
  public Person createAndFailWithCompletion(Person p) throws IllegalStateException, RollbackException, SystemException;
  
  
}
