package com.atomikos.icatch.standalone;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.atomikos.datasource.TestRecoverableResource;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.TSMetaData;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionService;
import com.atomikos.icatch.config.imp.TSInitInfoImp;
import com.atomikos.icatch.imp.TransactionServiceTestCase;

public class Issue10038TestJUnit extends TransactionServiceTestCase {

	private UserTransactionService uts;
	
	public Issue10038TestJUnit(String name) {
		super(name);
	}

	protected void setUp() {
		super.setUp();

	}


	protected void tearDown() {
		
		super.tearDown();
	}
	
	public void test()
	{
		uts = new AbstractUserTransactionService() {

			public TSMetaData getTSMetaData() {
				return null;
			}

			public TSInitInfo createTSInitInfo() {
				return new TSInitInfoImp();
			}
			
			public UserTransaction getUserTransaction(){
				return null;
			}
			
			public TransactionManager getTransactionManager(){
				return null;
				
			}
		};
		TestRecoverableResource res = new TestRecoverableResource ( "issue 10038" );
		res.setFailOnClose();
		uts.registerResource ( res );
		uts.init ( uts.createTSInitInfo() );
		//shutdown force shoult not throw any exception
		uts.shutdown ( true );
	}
	

}
