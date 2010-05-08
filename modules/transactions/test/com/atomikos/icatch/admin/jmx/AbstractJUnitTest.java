package com.atomikos.icatch.admin.jmx;
import javax.management.ObjectName;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicParticipant;
import com.atomikos.icatch.ReadOnlyParticipant;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.imp.TransactionServiceTestCase;

/**
 * 
 * 
 * 
 * 
 *
 * A test class for the JMX package
 */

public abstract class AbstractJUnitTest
extends TransactionServiceTestCase
{
	private TestMBeanServer mBeanServer_;
	
	private UserTransactionService uts_;
	
	private JmxTransactionService jmxService_;
	
	public AbstractJUnitTest ( String name ) 
	{
	    super ( name );
	}
	
	protected abstract UserTransactionService startUp();
	
	public void setUp() 
	{
	    super.setUp();
		mBeanServer_ = new TestMBeanServer();
		jmxService_ = new JmxTransactionService();
		try {
            jmxService_.preRegister( mBeanServer_ , null);
        } catch (Exception e) {
            throw new RuntimeException ( e );
           
        }
		uts_ = startUp();
		
	}
	
	public void tearDown() 
	{
	    super.tearDown();
		uts_.shutdown ( true );
	}
	
	
	public void testHeuristicRollback() throws Exception
	{
		CompositeTransactionManager ctm = uts_.getCompositeTransactionManager();
		CompositeTransaction ct = ctm.createCompositeTransaction(1000);
		String root = ct.getCompositeCoordinator().getCoordinatorId();
		StringHeuristicMessage msg = new StringHeuristicMessage ( "Test");
		HeuristicParticipant p = new HeuristicParticipant ( msg );
		p.setFailMode( HeuristicParticipant.FAIL_HEUR_ROLLBACK );
		ct.addParticipant ( p );
		ct.addParticipant ( new ReadOnlyParticipant() );
	
		try {
			System.out.println ( "About to commit a heuristic case. This will take a while...");
			ct.getTransactionControl().getTerminator().commit();
		}
		catch ( HeurRollbackException normal ) {
			//normal.printStackTrace();
		}
		//System.out.println ( "Commit done...");
		
		//now, assert that the JMX does the right thing
		ObjectName[] transactions = jmxService_.getTransactions();
		ObjectName found = null;
		JmxTransactionMBean mBean = null;
		int i = 0;
		//System.out.println ( "Looking for JMX beans...");
		while ( found == null && i < transactions.length ) {
			
			mBean = 
			( JmxTransactionMBean ) mBeanServer_.getMBean(transactions[i]);
			if ( root.equals ( mBean.getTid() ) ) {
				found = transactions[i];
			} 
			i++;
		}
		
		if ( found == null ) throw new Exception ( "JMX does not contain heuristic problem case?");
		
		//System.out.println ( "Found JMX bean");
		//here we are if found ->assert type of bean
		if ( ! ( mBean instanceof JmxHeuristicTransactionMBean ) ) 
			throw new Exception ( "Wrong bean type: " + mBean.getClass().getName());
		JmxHeuristicTransactionMBean heuristicBean =
			( JmxHeuristicTransactionMBean ) mBean;
		
		if ( ! "HEURISTIC ROLLBACK".equals ( mBean.getState() ) )
			throw new Exception ( "Wrong state for MBean: " + mBean.getState() );	
		
		if ( mBean.getHeuristicMessages() == null ) 
			throw new Exception ( "MBean has no heuristic messages?");
	
		
		if ( ! heuristicBean.getCommitted() ) 
			throw new Exception ( "MBean does not reflect commit decision?");
		
		//System.out.println ( "Forgetting heuristic...");
		heuristicBean.forceForget();
		//System.out.println ( "Forget done");
		if ( mBeanServer_.isRegistered( found )) 
			throw new Exception ( "MBean registered after forget?");
		
		//assert that the heuristic is really gone
		transactions = jmxService_.getTransactions();
		if ( ! ( transactions == null || transactions.length==0 ))
			throw new Exception ("ERROR: forget does not delete heuristic?");		
	}
	
	

}
