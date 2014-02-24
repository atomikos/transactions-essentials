package com.atomikos.icatch.jta.hibernate4.jpa;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;


public interface PersonService {
  
  public abstract Person create(Person p);
  
  public abstract Person createAndFailWithCompletion1(Person p) throws IllegalStateException, RollbackException, SystemException;
  
  public abstract Person createAndFailWithCompletion2(Person p) throws IllegalStateException, RollbackException, SystemException;
  
}