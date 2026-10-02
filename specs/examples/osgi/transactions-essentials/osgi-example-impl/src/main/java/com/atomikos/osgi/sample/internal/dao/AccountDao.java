package com.atomikos.osgi.sample.internal.dao;

public interface AccountDao {

	long getBalance(int accno) throws Exception ;

	String getOwner(int accno) throws Exception ;

	void withdraw(int accno, int amount) throws Exception;

}
