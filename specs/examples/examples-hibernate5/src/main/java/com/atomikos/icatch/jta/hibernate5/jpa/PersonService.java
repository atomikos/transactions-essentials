package com.atomikos.icatch.jta.hibernate5.jpa;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;


public interface PersonService {
  
  public Person create(Person p);
  
  public Person createAndFailWithCompletion(Person p) throws IllegalStateException, RollbackException, SystemException;
  
  
}