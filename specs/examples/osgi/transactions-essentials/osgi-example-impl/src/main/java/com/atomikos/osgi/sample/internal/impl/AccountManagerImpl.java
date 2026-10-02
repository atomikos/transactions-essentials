package com.atomikos.osgi.sample.internal.impl;

import com.atomikos.osgi.sample.AccountManager;
import com.atomikos.osgi.sample.internal.dao.AccountDao;

public class AccountManagerImpl implements AccountManager {

	
	public AccountManagerImpl() {
	System.out.println("AccountManagerImpl");
	}
	private AccountDao accountDao;

	public long getBalance(int accno) throws Exception {

		// Do stuff here...
		return accountDao.getBalance(accno);
	}

	public void withdraw(int accno, int amount) throws Exception {
		// Do stuff here...
				accountDao.withdraw( accno,  amount) ;

	}

	public String getOwner(int accno) throws Exception {

		// Do stuff here...
		return accountDao.getOwner(accno);
	}
	
	public void setAccountDao(AccountDao accountDao) {
		this.accountDao=accountDao;
		
	}

}
