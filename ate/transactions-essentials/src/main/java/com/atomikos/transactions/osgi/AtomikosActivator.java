package com.atomikos.transactions.osgi;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.atomikos.icatch.jta.UserTransactionManager;


public class AtomikosActivator implements BundleActivator {
	private UserTransactionManager utm = new UserTransactionManager();
	private ServiceRegistration utmRegistration;
	private ServiceRegistration userTransactionRegistration;
	private com.atomikos.icatch.jta.UserTransactionImp userTransaction= new com.atomikos.icatch.jta.UserTransactionImp();
	public void start(BundleContext context) throws Exception {
		
		utm.init();
		utm.setForceShutdown(false);
		userTransaction.setTransactionTimeout(3000);
		utmRegistration=	context.registerService(javax.transaction.TransactionManager.class.getName(), utm, null);
		userTransactionRegistration=	context.registerService(javax.transaction.UserTransaction.class.getName(), userTransaction, null);
		
		
		
	}

	public void stop(BundleContext context) throws Exception {
		utm.close();
		utmRegistration.unregister();
		userTransactionRegistration.unregister();
	}

}
