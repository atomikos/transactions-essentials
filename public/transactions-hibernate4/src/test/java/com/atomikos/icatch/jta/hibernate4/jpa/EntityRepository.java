package com.atomikos.icatch.jta.hibernate4.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

@Repository
@NoRepositoryBean
public interface EntityRepository<T> extends JpaRepository<T, Long> {
  
}
