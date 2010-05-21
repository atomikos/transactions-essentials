package com.atomikos.icatch.tcc;

import java.util.Properties;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.TestParticipant;
import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.TestLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.system.Configuration;

public class UserTccServiceManagerTestJUnit 
extends TransactionServiceTestCase 
{
	private static TestTccService service = new TestTccService();
	
	private UserTransactionService uts;

	private TSInitInfo info;
	
	private UserTccServiceManager tm;
	
	private CompositeTransactionManager ctm;
	private CompositeTransaction ct;
	
	private long timeout;
	
	private TestLogAdministrator admin;
	

	public UserTccServiceManagerTestJUnit(String name) {
		super(name);
	}
	
	protected void setUp() {
		super.setUp();
		uts = new UserTransactionServiceImp();

		info = uts.createTSInitInfo();
		Properties properties = info.getProperties();
		properties.setProperty(AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME,
				"TccManagerTestJUnit");
		properties.setProperty(AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME,
				getTemporaryOutputDir());
		properties.setProperty(AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME,
				getTemporaryOutputDir());
		properties
				.setProperty(AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME, "DEBUG");
		properties.setProperty(AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME, "25000");
		
		admin = new TestLogAdministrator();
		uts.registerLogAdministrator ( admin );
		
		uts.init(info);
		service.reset();
		UserTccServiceManager.doRegisterForRecovery (service) ;
		tm = new UserTccServiceManager();
		timeout = 1000;
		ctm = uts.getCompositeTransactionManager();
	}

	protected void tearDown() 
	{
		uts.shutdown(true);
		super.tearDown();
	}
	
	public void testRegisterWithNullServiceAndCompleted()
	{

		String id = tm.register (null , timeout );
		assertNotNull ( id );
		ct = ctm.getCompositeTransaction();
		assertNotNull (ct );
		TestParticipant tp = new TestParticipant();
		ct.addParticipant( tp );
		assertFalse (tp.isTerminated());
		tm.completed ( id );
		sleep();
		assertTrue ( tp.isTerminated() );
		assertNull ( ctm.getCompositeTransaction() );
		assertFalse ( service.isConfirmed( id ));
		assertFalse ( service.isCanceled(id));
	}
	
	public void testRegisterWithNullServiceAndFailed()
	{
		String id = tm.register (null , timeout );
		assertNotNull ( id );
		ct = ctm.getCompositeTransaction();
		assertNotNull (ct );
		TestParticipant tp = new TestParticipant();
		ct.addParticipant( tp );
		assertFalse (tp.isTerminated());
		tm.failed ( id );
		sleep();
		assertTrue ( tp.isTerminated() );
		assertNull ( ctm.getCompositeTransaction() );
		assertFalse ( service.isConfirmed( id ));
		assertFalse ( service.isCanceled(id));
	}
	
	public void testRegisterWithNullServiceAndSuspendResume()
	{
		String id = tm.register (null , timeout );
		assertNotNull ( id );
		ct = ctm.getCompositeTransaction();
		assertNotNull (ct );
		TestParticipant tp = new TestParticipant();
		ct.addParticipant( tp );
		assertFalse (tp.isTerminated());
		tm.suspend ( id );
		assertNull ( ctm.getCompositeTransaction() );
		tm.resume ( id );
		tm.completed ( id );
		
	}
	
	public void testCompleteWithoutPreExistingActivity()
	throws Exception
	{
		String id = tm.register ( service , timeout );
		assertNotNull ( id );
		tm.completed ( id );
		sleep();
		assertTrue ( service.isConfirmed(id));
		assertFalse ( service.isCanceled(id));
	}
	
	public void testCompleteWithPreExistingActivity()
	throws Exception
	{
		String parent = tm.register ( service , timeout );
		String child = tm.register (service , timeout );
		tm.completed ( child );
		assertFalse ( service.isConfirmed( child ));
		assertFalse ( service.isCanceled(child));
		tm.completed ( parent );
		sleep();
		assertTrue ( service.isConfirmed( child ));
		assertFalse ( service.isCanceled(child));
		assertTrue ( service.isConfirmed( parent ));
		assertFalse ( service.isCanceled(parent));
	}	
	
	public void testFailedWithoutPreExistingActivity()
	throws Exception
	{
		
		String id = tm.register ( service , timeout );
		assertNotNull ( id );
		tm.failed ( id );
		sleep();
		assertFalse ( service.isConfirmed(id));
		assertTrue ( service.isCanceled(id));
	}
	
	public void testFailedWithPreExistingActivity()
	throws Exception
	{
		String parent = tm.register ( service , timeout * 5 );
		String child = tm.register ( service , timeout );
		tm.failed ( child );
		sleep();
		assertFalse ( service.isConfirmed( child ));
		assertTrue ( service.isCanceled(child));
		tm.completed ( parent );
		sleep();
		assertFalse ( service.isConfirmed( child ));
		assertTrue ( service.isCanceled(child));
		if ( service.isCanceled(parent)) {
			failTest ( "parent canceled" );
		}
		assertTrue ( service.isConfirmed( parent ));
		
	}
	
	public void testSuspendResume()
	throws Exception
	{
		
		String first = tm.register (service , timeout );
		tm.suspend ( first );
		
		String second = tm.register ( service , timeout );
		tm.completed ( second );
		sleep();
		assertTrue ( service.isConfirmed(second));
		assertFalse ( service.isCanceled(second));
		
		tm.resume(first);
		tm.failed(first);
		sleep();
		sleep();
		sleep();
		assertFalse( service.isConfirmed(first));
		assertTrue ( service.isCanceled(first));
		
	}
	
	public void testSuspendFailsAfterTermination()
	throws Exception
	{
		String id = tm.register ( service , timeout );
		tm.failed( id );
		sleep();
		try {
			tm.suspend( id );
			failTest ( "Suspend works after timeout");
		}
		catch ( IllegalStateException ok ) {}
		assertTrue ( service.isCanceled(id));
		assertFalse ( service.isConfirmed(id));
	}
	
	
	

	public void testNoCancelWhenTrying()
	throws Exception
	{
		String id = tm.register ( service , 2 * timeout );
		Thread.sleep ( timeout );
		assertFalse ( service.isCanceled(id));
		tm.completed(id);
		sleep();
		assertTrue ( service.isConfirmed (id));
	}
	
	public void testTimeoutLeavesHazard()
	throws Exception
	{
		String id = tm.register ( service , timeout );
		CompositeTransaction ct = uts.getCompositeTransactionManager().getCompositeTransaction();
		Thread.sleep( 3 * timeout );
	
		String[] tids = new String[] { ct.getCompositeCoordinator().getCoordinatorId() };
		
		AdminTransaction[] txs = admin.getLogControl().getAdminTransactions(  tids );
		if ( txs == null || txs.length == 0 ) failTest ( "No heuristics?" );
		
	}
	
//	public void testRestartKeepsTccResource()
//	throws Exception
//	{
//		String resName = service.toString();
//		
//		
//		assertNotNull ( Configuration.getResource ( resName ) );
//		
//		//shutdown and restart ts
//		uts.shutdown(true);
//		uts.init ( info );
//		
//		assertNotNull ( Configuration.getResource ( resName ) );
//	}
	

}
