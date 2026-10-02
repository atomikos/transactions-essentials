package com.atomikos.osgi.sample;

public interface AccountManager {

	long getBalance(int accno) throws Exception;

	void withdraw(int account, int amount) throws Exception;

	String getOwner(int accno) throws Exception;

	
}
