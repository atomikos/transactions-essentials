package com.atomikos.examples.jpa.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.atomikos.examples.jpa.Account;
import com.atomikos.examples.jpa.TestDao;

@Transactional(readOnly=false)
@Repository("testDao")
public class TestDaoImpl implements TestDao {
	@PersistenceContext(unitName = "atomikosPersistenceUnit")
	private EntityManager em;

	@Transactional(readOnly=false,propagation=Propagation.REQUIRES_NEW)
	public Account create(Account a) {

		em.persist(a);

		return a;
	}

	/* (non-Javadoc)
	 * @see com.atomikos.examples.jpa.TestDao#retreive(int)
	 */
	public Account retreive(int id) {

		Query query = em.createQuery("select acc from Account acc where acc.account=:id");

		query.setParameter("id", id);
		return (Account) query.getSingleResult();

	}
}
