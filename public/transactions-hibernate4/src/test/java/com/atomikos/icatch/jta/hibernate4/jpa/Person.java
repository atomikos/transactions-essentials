package com.atomikos.icatch.jta.hibernate4.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Person {
  
  
  @Id
  @GeneratedValue
  private Long id;
  
  
  private String name;
  
  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }
  
  
  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  
  
  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }
  
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
}
