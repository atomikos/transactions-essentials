package com.atomikos.icatch.msg;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.CompositeTransactionAdaptor;
import com.atomikos.icatch.imp.PropagationImp;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.system.Configuration;

public class MessageBased2PCTestJUnit extends TransactionServiceTestCase 
{
	
	  private static final String TARGETURI = "TestUri";
	  private static final String TARGETADDRESS = "TestAddress";
	  private static final String SENDERURI = "SenderUri";
	  private static final String SENDERADDRESS = "SenderAddress";
	  private static final int FORMAT = TransactionMessage.FORMAT_UNKNOWN;
	  private static final int PROTOCOL = Transport.UNKNOWN_PROTOCOL;    
	
	private UserTransactionService uts = null;
	
	private TransactionService ts;
	private CompositeTransactionManager ctm;
	private TestTransport transport;
    private TestTransport coordinatorTransport;
    private TestTransport participantTransport;
	private CompositeTransaction ct, parent;
	private CommitServer cs;
	private TestMsgCoordinator tmc;
	private MessageRecoveryCoordinator mrc;
	private TestMsgParticipant tmp;
	private MessageParticipant mp;
	private Stack lineage;
	private PropagationImp propagation;
	private String rootUri, partUri, localRootUri;
	private long timeout;
	
	public MessageBased2PCTestJUnit(String name) {
		super(name);
	}
	
	protected void setUp()
	{
		super.setUp();
		timeout = 500;
		uts =
			new com.atomikos.icatch.standalone.
				UserTransactionServiceFactory().getUserTransactionService( new Properties() );
		TSInitInfo info = uts.createTSInitInfo();
		info.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG");
		info.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
    		info.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
		uts.init ( info );
		ctm = Configuration.getCompositeTransactionManager();
		ts = Configuration.getTransactionService();
		transport = new TestTransport ( TARGETADDRESS , SENDERADDRESS , Configuration.getConsole() );
		coordinatorTransport = new TestTransport ( TARGETADDRESS , SENDERADDRESS , Configuration.getConsole() );
        participantTransport = new TestTransport ( TARGETADDRESS , SENDERADDRESS , Configuration.getConsole() );
        
        transport.setRequestTranspor ( participantTransport );
        transport.setReplyTransport ( coordinatorTransport );
        coordinatorTransport.setRequestTranspor ( transport );
        participantTransport.setReplyTransport ( transport );
        
        Transport[] transports = new Transport[1];
         transports[0] = transport;
         cs = new CommitServer();
         cs.init ( ts , transports );
         cs.init ( true , new Properties() );
	}
	
	
	protected void tearDown()
	{
		uts.shutdown ( true );
		super.tearDown();
	}
	
	public void testOnePhaseCommit()
	throws Exception
	{
	        rootUri = "root4";
	        partUri = "part4";
	        //because the test uses the same transport for ALL
	        //parties, we use a different ID to represent the 
	        //locally imported root. Otherwise, delivery 
	        //problems will occur.
	        localRootUri = "Imported:" + rootUri;
	        
	        tmc = new TestMsgCoordinator ( coordinatorTransport , 
	            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
	            localRootUri );
	            
	        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_ONE_PHASE );
	        mrc = new MessageRecoveryCoordinator ( 
	            rootUri, transport.getParticipantAddress() , transport );
	        parent = new CompositeTransactionAdaptor ( 
	            localRootUri , true , mrc );
	        lineage = new Stack();
	        lineage.push ( parent );
	        propagation = new PropagationImp ( lineage, true , timeout );
	        ct = ts.recreateCompositeTransaction ( propagation , false , false );
	        
	        tmp = new TestMsgParticipant ( participantTransport , 
	            partUri+":proxy" , transport.getParticipantAddress() , 
	            transport.getParticipantAddress() , localRootUri );
	        tmp.init();
	        
	        mp = new MessageParticipant ( 
	            partUri+ ":proxy" , transport.getParticipantAddress() , transport , 
	            null , false , false );
	        
	        ct.addParticipant ( mp );
	        ct.getTransactionControl().getTerminator().commit();
	        
	        
	        //now, start the show...
	        tmc.process();
	        
	        //assert that the proper msgs were received in the
	        //test msg participant...
	        
	        if ( tmp.hasReceivedPrepare() )
	            failTest ( "Participant received  prepare?" );
	        if ( !tmp.hasReceivedCommit() )
	            failTest ( "Participant received no commit?" );
	        if ( tmp.hasReceivedRollback() )
	            failTest ( "Participant received rollback?" );
	        if ( tmp.hasReceivedForget() )
	            failTest ( "Participant received forget?" );
	        
	        //assert that the coordinator has received the right 
	        //messages
	        
	        if ( tmc.hasReceivedPrepared() )
	            failTest ( "Coordinator received  prepared?" );
	         if ( ! tmc.hasReceivedCommitted() )
	            failTest ( "Coordinator received no committed?" );
	         if ( tmc.hasReceivedRolledback() )
	            failTest ( "Coordinator received rolledback?" );
	         if ( tmc.getError() >=0 )
	            failTest ( "Coordinator received error " + tmc.getError() );
	}
	
	public void test2PCWithReadOnly()
	throws Exception
	{
	       rootUri = "root5";
	        partUri = "part5";
	        //because the test uses the same transport for ALL
	        //parties, we use a different ID to represent the 
	        //locally imported root. Otherwise, delivery 
	        //problems will occur.
	        localRootUri = "Imported:" + rootUri;
	        
	        tmc = new TestMsgCoordinator ( coordinatorTransport , 
	            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
	            localRootUri );
	        
	        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_AFTER_PREPARE );

	        mrc = new MessageRecoveryCoordinator ( 
	            rootUri, transport.getParticipantAddress() , transport );
	        parent = new CompositeTransactionAdaptor ( 
	            localRootUri , true , mrc );
	        lineage = new Stack();
	        lineage.push ( parent );
	        propagation = new PropagationImp ( lineage, true , timeout  );
	        ct = ts.recreateCompositeTransaction ( propagation , false , false );
	        
	        tmp = new TestMsgParticipant ( participantTransport , 
	        		partUri+":proxy" , transport.getParticipantAddress() , 
	            transport.getParticipantAddress() , localRootUri );
	        tmp.setBehaviour ( TestMsgParticipant.READ_ONLY );
	        tmp.init();
	        
	        mp = new MessageParticipant ( 
	            partUri+ ":proxy" , transport.getParticipantAddress() , transport , 
	            null , false , false );
	        
	        ct.addParticipant ( mp );
	        ct.getTransactionControl().getTerminator().commit();
	        
	        
	        //now, start the show...
	        tmc.process();
	        
	        //assert that the proper msgs were received in the
	        //test msg participant...
	        
	        if ( !tmp.hasReceivedPrepare() )
	            failTest ( "Participant received no prepare?" );
	        if ( tmp.hasReceivedCommit() )
	            failTest ( "Participant received commit?" );
	        if ( tmp.hasReceivedRollback() )
	            failTest ( "Participant received rollback?" );
	        if ( tmp.hasReceivedForget() )
	            failTest ( "Participant received forget?" );
	        
	        //assert that the coordinator has received the right 
	        //messages
	        
	        if ( !tmc.hasReceivedPrepared() )
	            failTest ( "Coordinator received no prepared?" );
	         if ( tmc.hasReceivedCommitted() )
	            failTest ( "Coordinator received committed?" );
	         if ( tmc.hasReceivedRolledback() )
	            failTest ( "Coordinator received rolledback?" );
	         if ( tmc.getError() >=0 )
	            failTest ( "Coordinator received error " + tmc.getError() );
	}
	
	public void test2PCWithNoVote()
	throws Exception
	{
	        rootUri = "root6";
	        partUri = "part6";
	        //because the test uses the same transport for ALL
	        //parties, we use a different ID to represent the 
	        //locally imported root. Otherwise, delivery 
	        //problems will occur.
	        localRootUri = "Imported:" + rootUri;
	        
	        tmc = new TestMsgCoordinator ( coordinatorTransport , 
	            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
	            localRootUri );
	        
	        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_AFTER_PREPARE );

	        mrc = new MessageRecoveryCoordinator ( 
	            rootUri, transport.getParticipantAddress() , transport );
	        parent = new CompositeTransactionAdaptor ( 
	            localRootUri , true , mrc );
	        lineage = new Stack();
	        lineage.push ( parent );
	        propagation = new PropagationImp ( lineage, true , timeout  );
	        ct = ts.recreateCompositeTransaction ( propagation , false , false );
	        
	        tmp = new TestMsgParticipant ( participantTransport , 
	        		partUri+":proxy" , transport.getParticipantAddress() , 
	            transport.getParticipantAddress() , localRootUri );
	        tmp.setBehaviour ( TestMsgParticipant.ROLLED_BACK );
	        tmp.init();
	        
	        mp = new MessageParticipant ( 
	            partUri+ ":proxy" , transport.getParticipantAddress() , transport , 
	            null , false , false );
	        
	        ct.addParticipant ( mp );
	        ct.getTransactionControl().getTerminator().commit();
	        
	        
	        //now, start the show...
	        tmc.process();
	        
	        //assert that the proper msgs were received in the
	        //test msg participant...
	        
	        if ( !tmp.hasReceivedPrepare() )
	            failTest ( "Participant received no prepare?" );
	        if ( tmp.hasReceivedCommit() )
	            failTest ( "Participant received commit?" );
	        if ( tmp.hasReceivedRollback() )
	            failTest ( "Participant received rollback?" );
	        if ( tmp.hasReceivedForget() )
	            failTest ( "Participant received forget?" );
	        
	        //assert that the coordinator has received the right 
	        //messages
	        
	        if ( tmc.hasReceivedPrepared() )
	            failTest ( "Coordinator received prepared?" );
	         if ( tmc.hasReceivedCommitted() )
	            failTest ( "Coordinator received committed?" );
	         if ( tmc.hasReceivedRolledback() )
	            failTest ( "Coordinator received rolledback?" );
	         if ( tmc.getError() < 0 )
	            failTest ( "Coordinator received no error?" );
	}
	
	public void test2CPWithHeuristicAbortParticipant()
	throws Exception
	{
        rootUri = "root7";
        partUri = "part7";
        //because the test uses the same transport for ALL
        //parties, we use a different ID to represent the 
        //locally imported root. Otherwise, delivery 
        //problems will occur.
        localRootUri = "Imported:" + rootUri;
        
        tmc = new TestMsgCoordinator ( coordinatorTransport , 
            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
            localRootUri );
        
        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_AFTER_PREPARE );

        mrc = new MessageRecoveryCoordinator ( 
            rootUri, transport.getParticipantAddress() , transport );
        parent = new CompositeTransactionAdaptor ( 
            localRootUri , true , mrc );
        lineage = new Stack();
        lineage.push ( parent );
        propagation = new PropagationImp ( lineage, true , timeout );
        ct = ts.recreateCompositeTransaction ( propagation , false , false );
        
        tmp = new TestMsgParticipant ( participantTransport , 
        		partUri+":proxy" , transport.getParticipantAddress() , 
            transport.getParticipantAddress() , localRootUri );
        tmp.setBehaviour ( TestMsgParticipant.HEURISTIC_ABORT );
        tmp.init();
        
        mp = new MessageParticipant ( 
            partUri+ ":proxy" , transport.getParticipantAddress() , transport , 
            null , false , false );
        
        ct.addParticipant ( mp );
        ct.getTransactionControl().getTerminator().commit();
        
        
        //now, start the show...
        tmc.process();
        
        //assert that the proper msgs were received in the
        //test msg participant...
        
        if ( !tmp.hasReceivedPrepare() )
            failTest ( "Participant received no prepare?" );
        if ( ! tmp.hasReceivedCommit() )
            failTest ( "Participant received no commit?" );
        if ( tmp.hasReceivedRollback() )
            failTest ( "Participant received rollback?" );
        //by default, NO forget propagation is done!
        if ( tmp.hasReceivedForget() )
            failTest ( "Participant received forget?" );
        
        //assert that the coordinator has received the right 
        //messages
        
        if ( !tmc.hasReceivedPrepared() )
            failTest ( "Coordinator received no prepared?" );
         if ( tmc.hasReceivedCommitted() )
            failTest ( "Coordinator received committed?" );
         if ( tmc.hasReceivedRolledback() )
            failTest ( "Coordinator received rolledback?" );
         if ( tmc.getError() != ErrorMessage.HEUR_ROLLBACK_ERROR )
            failTest ( "Coordinator received no hrb error?" );
	}
	
	public void test2PCWithHeuristicMixedParticipant()
	throws Exception
	{
	    
        rootUri = "root8";
        partUri = "part8";
        //because the test uses the same transport for ALL
        //parties, we use a different ID to represent the 
        //locally imported root. Otherwise, delivery 
        //problems will occur.
        localRootUri = "Imported:" + rootUri;
        
        tmc = new TestMsgCoordinator ( coordinatorTransport , 
            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
            localRootUri );
        
        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_AFTER_PREPARE );

        mrc = new MessageRecoveryCoordinator ( 
            rootUri, transport.getParticipantAddress() , transport );
        parent = new CompositeTransactionAdaptor ( 
            localRootUri , true , mrc );
        lineage = new Stack();
        lineage.push ( parent );
        propagation = new PropagationImp ( lineage, true , timeout );
        ct = ts.recreateCompositeTransaction ( propagation , false , false );
        
        tmp = new TestMsgParticipant ( participantTransport , 
        		partUri+":proxy" , transport.getParticipantAddress() , 
            transport.getParticipantAddress() , localRootUri );
        tmp.setBehaviour ( TestMsgParticipant.HEURISTIC_MIXED );
        tmp.init();
        
        mp = new MessageParticipant ( 
            partUri+ ":proxy" , transport.getParticipantAddress() , transport , 
            null , false , false );
        
        ct.addParticipant ( mp );
        ct.getTransactionControl().getTerminator().commit();
        
        
        //now, start the show...
        tmc.process();
        
        //assert that the proper msgs were received in the
        //test msg participant...
        
        if ( !tmp.hasReceivedPrepare() )
            failTest ( "Participant received no prepare?" );
        if ( ! tmp.hasReceivedCommit() )
            failTest ( "Participant received no commit?" );
        if ( tmp.hasReceivedRollback() )
            failTest ( "Participant received rollback?" );
        //by default, NO forget propagation is done!
        if ( tmp.hasReceivedForget() )
            failTest ( "Participant received forget?" );
        
        //assert that the coordinator has received the right 
        //messages
        
        if ( !tmc.hasReceivedPrepared() )
            failTest ( "Coordinator received no prepared?" );
         if ( tmc.hasReceivedCommitted() )
            failTest ( "Coordinator received committed?" );
         if ( tmc.hasReceivedRolledback() )
            failTest ( "Coordinator received rolledback?" );
         if ( tmc.getError() != ErrorMessage.HEUR_MIXED_ERROR )
            failTest ( "Coordinator received no hm error?" );
	}
	
	public void test2PCWithHeuristicHazardParticipant() throws Exception
	{
	      rootUri = "root9";
	        partUri = "part9";
	        //because the test uses the same transport for ALL
	        //parties, we use a different ID to represent the 
	        //locally imported root. Otherwise, delivery 
	        //problems will occur.
	        localRootUri = "Imported:" + rootUri;
	        
	        tmc = new TestMsgCoordinator ( coordinatorTransport , 
	            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
	            localRootUri );
	        
	        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_AFTER_PREPARE );

	        mrc = new MessageRecoveryCoordinator ( 
	            rootUri, transport.getParticipantAddress() , transport );
	        parent = new CompositeTransactionAdaptor ( 
	            localRootUri , true , mrc );
	        lineage = new Stack();
	        lineage.push ( parent );
	        propagation = new PropagationImp ( lineage, true , timeout  );
	        ct = ts.recreateCompositeTransaction ( propagation , false , false );
	        
	        tmp = new TestMsgParticipant ( participantTransport , 
	        		partUri+":proxy" , transport.getParticipantAddress() , 
	            transport.getParticipantAddress() , localRootUri );
	        tmp.setBehaviour ( TestMsgParticipant.HEURISTIC_HAZARD );
	        tmp.init();
	        
	        mp = new MessageParticipant ( 
	            partUri+ ":proxy" , transport.getParticipantAddress() , transport , 
	            null , false , false );
	        
	        ct.addParticipant ( mp );
	        ct.getTransactionControl().getTerminator().commit();
	        
	        
	        //now, start the show...
	        tmc.process();
	        
	        //assert that the proper msgs were received in the
	        //test msg participant...
	        
	        if ( !tmp.hasReceivedPrepare() )
	            failTest ( "Participant received no prepare?" );
	        if ( ! tmp.hasReceivedCommit() )
	            failTest ( "Participant received no commit?" );
	        if ( tmp.hasReceivedRollback() )
	            failTest ( "Participant received rollback?" );
	        //by default, NO forget propagation is done!
	        if ( tmp.hasReceivedForget() )
	            failTest ( "Participant received forget?" );
	        
	        //assert that the coordinator has received the right 
	        //messages
	        
	        if ( !tmc.hasReceivedPrepared() )
	            failTest ( "Coordinator received no prepared?" );
	         if ( tmc.hasReceivedCommitted() )
	            failTest ( "Coordinator received committed?" );
	         if ( tmc.hasReceivedRolledback() )
	            failTest ( "Coordinator received rolledback?" );
	         if ( tmc.getError() != ErrorMessage.HEUR_HAZARD_ERROR )
	            failTest ( "Coordinator received no hm error?" );
	        
	}
	
	public void testOnePhaseCommitMessage()
	throws Exception
	{
		testCommitMessage ( true );
	}
	
	public void testTwoPhaseCommitMessage()
	throws Exception
	{
		testCommitMessage ( false );
	}
	
	public void testRollbackBeforePrepare()
	throws Exception
	{
        rootUri = "root3";
        partUri = "part3";
        //because the test uses the same transport for ALL
        //parties, we use a different ID to represent the 
        //locally imported root. Otherwise, delivery 
        //problems will occur.
        localRootUri = "Imported:" + rootUri;
        
        tmc = new TestMsgCoordinator ( coordinatorTransport , 
            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
            localRootUri );
            
        tmc.setBehaviour ( TestMsgCoordinator.ROLLBACK_BEFORE_PREPARE );
        mrc = new MessageRecoveryCoordinator ( 
            rootUri, transport.getParticipantAddress() , transport );
        parent = new CompositeTransactionAdaptor ( 
            localRootUri , true , mrc );
        lineage = new Stack();
        lineage.push ( parent );
        propagation = new PropagationImp ( lineage, true , timeout );
        ct = ts.recreateCompositeTransaction ( propagation , false , false );
        
        tmp = new TestMsgParticipant ( participantTransport , 
        		partUri+":proxy" , transport.getParticipantAddress() , 
            transport.getParticipantAddress() , localRootUri );
        tmp.init();
        
        mp = new MessageParticipant ( 
            partUri+ ":proxy" , transport.getParticipantAddress() , transport , 
            null , false , false );
        
        ct.addParticipant ( mp );
        ct.getTransactionControl().getTerminator().commit();
        
        
        //now, start the show...
        tmc.process();
        
        //assert that the proper msgs were received in the
        //test msg participant...
        
        if (  tmp.hasReceivedPrepare() )
            failTest ( "Participant received prepare?" );
        if ( tmp.hasReceivedCommit() )
            failTest ( "Participant received commit?" );
        if ( !tmp.hasReceivedRollback() )
            failTest ( "Participant received no rollback?" );
        if ( tmp.hasReceivedForget() )
            failTest ( "Participant received forget?" );
        
        //assert that the coordinator has received the right 
        //messages
        
        if ( tmc.hasReceivedPrepared() )
            failTest ( "Coordinator received prepared?" );
         if ( tmc.hasReceivedCommitted() )
            failTest ( "Coordinator received committed?" );
         if ( !tmc.hasReceivedRolledback() )
            failTest ( "Coordinator received no rolledback?" );
         if ( tmc.getError() >=0 )
            failTest ( "Coordinator received error " + tmc.getError() );
	}
	
	public void testNormal2PCWithRollbackAfterPrepare()
	throws Exception
	{
	       rootUri = "root2";
	        partUri = "part2";
	        //because the test uses the same transport for ALL
	        //parties, we use a different ID to represent the 
	        //locally imported root. Otherwise, delivery 
	        //problems will occur.
	        localRootUri = "Imported:" + rootUri;
	        
	        tmc = new TestMsgCoordinator ( coordinatorTransport , 
	            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
	            localRootUri );
	            
	        tmc.setBehaviour ( TestMsgCoordinator.ROLLBACK_AFTER_PREPARE );
	        mrc = new MessageRecoveryCoordinator ( 
	            rootUri, transport.getParticipantAddress() , transport );
	        parent = new CompositeTransactionAdaptor ( 
	            localRootUri , true , mrc );
	        lineage = new Stack();
	        lineage.push ( parent );
	        propagation = new PropagationImp ( lineage, true , timeout  );
	        ct = ts.recreateCompositeTransaction ( propagation , false , false );
	        
	        tmp = new TestMsgParticipant ( participantTransport , 
	        		partUri+":proxy" , transport.getParticipantAddress() , 
	            transport.getParticipantAddress() , localRootUri );
	        tmp.init();
	        
	        mp = new MessageParticipant ( 
	            partUri+ ":proxy" , transport.getParticipantAddress() , transport , 
	            null , false , false );
	       
	        //fill in dummy cascade data to test this branch too
	        Hashtable cascadeList = new Hashtable();
	        cascadeList.put ( TARGETURI , new Integer(1) );
	       
	        mp.setCascadeList(cascadeList );
	        mp.setGlobalSiblingCount(1);
	        
	        ct.addParticipant ( mp );
	        ct.getTransactionControl().getTerminator().commit();
	        
	        
	        //now, start the show...
	        tmc.process();
	        
	        //assert that the proper msgs were received in the
	        //test msg participant...
	        
	        if ( ! tmp.hasReceivedPrepare() )
	            failTest ( "Participant received no prepare?" );
	        if ( tmp.hasReceivedCommit() )
	            failTest ( "Participant received  commit?" );
	        if ( !tmp.hasReceivedRollback() )
	            failTest ( "Participant received no rollback?" );
	        if ( tmp.hasReceivedForget() )
	            failTest ( "Participant received forget?" );
	        
	        //assert that the coordinator has received the right 
	        //messages
	        
	        if ( ! tmc.hasReceivedPrepared() )
	            failTest ( "Coordinator received no prepared?" );
	         if ( tmc.hasReceivedCommitted() )
	            failTest ( "Coordinator received  committed?" );
	         if ( ! tmc.hasReceivedRolledback() )
	            failTest ( "Coordinator received no rolledback?" );
	         if ( tmc.getError() >=0 )
	            failTest ( "Coordinator received error " + tmc.getError() );
	}
	
	public void testNormal2PC()
	throws Exception
	{
        String rootUri = "root1";
        String partUri = "part1";
        //because the test uses the same transport for ALL
        //parties, we use a different ID to represent the 
        //locally imported root. Otherwise, delivery 
        //problems will occur.
        String localRootUri = "Imported:" + rootUri;
        
        TestMsgCoordinator tmc = new TestMsgCoordinator ( coordinatorTransport , 
            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
            localRootUri );
            
        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_AFTER_PREPARE );
        MessageRecoveryCoordinator mrc = new MessageRecoveryCoordinator ( 
            rootUri, transport.getParticipantAddress() , transport );
        parent = new CompositeTransactionAdaptor ( 
            localRootUri , true , mrc );
        lineage = new Stack();
        lineage.push ( parent );
        propagation = new PropagationImp ( lineage, true , timeout  );
        ct = ts.recreateCompositeTransaction ( propagation , false , false );
        
        tmp = new TestMsgParticipant ( participantTransport , 
        		partUri+":proxy" , transport.getParticipantAddress() , 
            transport.getParticipantAddress() , localRootUri );
        tmp.init();
        
        mp = new MessageParticipant ( 
            partUri + ":proxy" , transport.getParticipantAddress() , transport , 
            null , false , false );
        
        ct.addParticipant ( mp );
        ct.getTransactionControl().getTerminator().commit();
        
        
        //now, start the show...
        tmc.process();
        
        //assert that the proper msgs were received in the
        //test msg participant...
        
        if ( ! tmp.hasReceivedPrepare() )
            failTest ( "Participant received no prepare?" );
        if ( !tmp.hasReceivedCommit() )
            failTest ( "Participant received no commit?" );
        if ( tmp.hasReceivedRollback() )
            failTest ( "Participant received rollback?" );
        if ( tmp.hasReceivedForget() )
            failTest ( "Participant received forget?" );
        
        //assert that the coordinator has received the right 
        //messages
        
        if ( ! tmc.hasReceivedPrepared() )
            failTest ( "Coordinator received no prepared?" );
         if ( ! tmc.hasReceivedCommitted() )
            failTest ( "Coordinator received no committed?" );
         if ( tmc.hasReceivedRolledback() )
            failTest ( "Coordinator received rolledback?" );
         if ( tmc.getError() >=0 )
            failTest ( "Coordinator received error " + tmc.getError() );
	}

    /**
     * Test message functionality.
     * @throws Exception
     */
    private void testCommitMessage ( boolean onePhase )
    throws Exception
    {
    	CommitMessage msg = new CommitMessageImp
    		( PROTOCOL , FORMAT ,
    		TARGETADDRESS , TARGETURI ,
    		SENDERADDRESS , SENDERURI , onePhase );
    	
    	if ( msg.getFormat() != FORMAT )
    		failTest ("getFormat fails");
    	if ( msg.getMessageType() != TransactionMessage.COMMIT_MESSAGE )
    		failTest ( "getMessageType fails ");
    	if ( msg.getProtocol() != PROTOCOL )
    		failTest ( "getProtocol fails");
    	if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			failTest ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			failTest ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			failTest ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			failTest ( "getTargetUri fails");
		
		if ( msg.isOnePhase() != onePhase )
			failTest ( "isOnePhase fails");
    }
    
	public void testForgetMessage()
	throws Exception
	{
		ForgetMessageImp msg = new ForgetMessageImp (
			PROTOCOL , FORMAT , TARGETADDRESS, TARGETURI,
			SENDERADDRESS , SENDERURI 
		);
    	
		if ( msg.getFormat() != FORMAT )
			failTest ("getFormat fails");
		if ( msg.getMessageType() != TransactionMessage.FORGET_MESSAGE )
			failTest ( "getMessageType fails ");
		if ( msg.getProtocol() != PROTOCOL )
			failTest ( "getProtocol fails");
		if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			failTest ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			failTest ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			failTest ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			failTest ( "getTargetUri fails");
		
		 	
	}
    
    public void testErrorMessage()
    throws Exception
    {
    	ErrorMessageImp msg = new ErrorMessageImp (
    		PROTOCOL , FORMAT , TARGETADDRESS, TARGETURI,
    		SENDERADDRESS , SENDERURI , 3
    	);
    	
		if ( msg.getFormat() != FORMAT )
			failTest ("getFormat fails");
		if ( msg.getMessageType() != TransactionMessage.ERROR_MESSAGE )
			failTest ( "getMessageType fails ");
		if ( msg.getProtocol() != PROTOCOL )
			failTest ( "getProtocol fails");
		if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			failTest ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			failTest ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			failTest ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			failTest ( "getTargetUri fails");
		
		if ( msg.getErrorCode() != 3 )
			failTest ( "getErrorCode fails");    	
    }
    
    public void testPreparedMessageWithReadOnlyAndDefaultIsCommit()
    throws Exception
    {
    		testPreparedMessage ( true , false );
    		
    }

    public void testPreparedMessageWithReadOnlyAndDefaultIsRollback()
    throws Exception
    {
    		testPreparedMessage ( true , true );
    		
    		
    }
    
    public void testPreparedMessageWithDefaultIsCommit()
    throws Exception
    {
    	testPreparedMessage ( false , false );
    }
    
    public void testPreparedMessageWithDefaultIsRollback()
    throws Exception
    {
    	testPreparedMessage ( false , true );
    }
    
	private void testPreparedMessage ( 
		boolean readOnly , boolean defaultIsRollback )
	throws Exception
	{
		PreparedMessageImp msg = new PreparedMessageImp (
			PROTOCOL , FORMAT , TARGETADDRESS, TARGETURI,
			SENDERADDRESS , SENDERURI , 
			readOnly , defaultIsRollback
		);
    	
		if ( msg.getFormat() != FORMAT )
			failTest ("getFormat fails");
		if ( msg.getMessageType() != TransactionMessage.PREPARED_MESSAGE )
			failTest ( "getMessageType fails ");
		if ( msg.getProtocol() != PROTOCOL )
			failTest ( "getProtocol fails");
		if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			failTest ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			failTest ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			failTest ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			failTest ( "getTargetUri fails");
		
		if ( msg.isReadOnly() != readOnly )
			failTest ( "isReadOnly fails");
		if ( msg.defaultIsRollback() != defaultIsRollback )
			failTest ( "defaultIsRollback fails"); 	
	}
	
	public void testPrepareMessageWithOrphanInfo()
	throws Exception
	{
		testPrepareMessage ( true );
	}
	
	public void testPrepareMessageWithoutOrphanInfo()
	throws Exception
	{
		testPrepareMessage ( false );
	}
	
	private void testPrepareMessage ( boolean orphans )
	throws Exception
	{
		PrepareMessageImp msg = null;
		
		if ( orphans ) {
				CascadeInfo[] info = new CascadeInfo[1];
				info[0] = new CascadeInfo();
				info[0].count = 1;
				info[0].participant = TARGETURI;
				msg = new PrepareMessageImp (
					PROTOCOL, FORMAT, TARGETADDRESS,
					TARGETURI , SENDERADDRESS , SENDERURI,
					1 , info
				 );
				 
				if ( msg.getGlobalSiblingCount() != 1) 
					failTest ("getGlobalSiblingCount fails");
				if ( msg.getCascadeInfo() == null || 
					msg.getCascadeInfo().length != 1 )
					failTest ( "getCascadeInfo fails");
				info = msg.getCascadeInfo();
				if ( info[0].count != 1 )
					failTest ( "Wrong cascade count");
				if (! info[0].participant.equals ( TARGETURI ))
					failTest ("Wrong cascade participant");
				
						 
		}
		else {
			msg = new PrepareMessageImp (
								PROTOCOL, FORMAT, TARGETADDRESS,
								TARGETURI , SENDERADDRESS , SENDERURI);
		}
		
		if ( msg.getFormat() != FORMAT )
			failTest ("getFormat fails");
		if ( msg.getMessageType() != TransactionMessage.PREPARE_MESSAGE )
			failTest ( "getMessageType fails ");
		if ( msg.getProtocol() != PROTOCOL )
			failTest ( "getProtocol fails");
		if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			failTest ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			failTest ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			failTest ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			failTest ( "getTargetUri fails");

		if ( msg.hasOrphanInfo() != orphans )
			failTest ( "hasOrphanInfo fails");
		
	}
    
    public void testReplayMessage()
    throws Exception
    {
		ReplayMessageImp msg = new ReplayMessageImp (
			PROTOCOL , FORMAT , TARGETADDRESS, TARGETURI,
			SENDERADDRESS , SENDERURI 
		);
    	
		if ( msg.getFormat() != FORMAT )
			failTest ("getFormat fails");
		if ( msg.getMessageType() != TransactionMessage.REPLAY_MESSAGE )
			failTest ( "getMessageType fails ");
		if ( msg.getProtocol() != PROTOCOL )
			failTest ( "getProtocol fails");
		if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			failTest ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			failTest ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			failTest ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			failTest ( "getTargetUri fails");
			
		
    }

	public void testRollbackMessage()
	throws Exception
	{
		RollbackMessageImp msg = new RollbackMessageImp (
			PROTOCOL , FORMAT , TARGETADDRESS, TARGETURI,
			SENDERADDRESS , SENDERURI 
		);
    	
		if ( msg.getFormat() != FORMAT )
			failTest ("getFormat fails");
		if ( msg.getMessageType() != TransactionMessage.ROLLBACK_MESSAGE )
			failTest ( "getMessageType fails ");
		if ( msg.getProtocol() != PROTOCOL )
			failTest ( "getProtocol fails");
		if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			failTest ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			failTest ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			failTest ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			failTest ( "getTargetUri fails");
			
		
	}
	
	public void testCommittedStateMessage()
	throws Exception
	{
		testStateMessage ( new Boolean ( true ) );
	}
	
	public void testAbortedStateMessage()
	throws Exception
	{
		testStateMessage ( new Boolean ( false ) );
	}
	
	public void testUnknownStateMessage()
	throws Exception
	{
		testStateMessage ( null );
	}
	
	private void testStateMessage ( Boolean committed )
	throws Exception
	{
		StateMessageImp msg = new StateMessageImp (
			PROTOCOL , FORMAT , TARGETADDRESS, TARGETURI,
			SENDERADDRESS , SENDERURI , committed
		);
    	
		if ( msg.getFormat() != FORMAT )
			failTest ("getFormat fails");
		if ( msg.getMessageType() != TransactionMessage.STATE_MESSAGE )
			failTest ( "getMessageType fails ");
		if ( msg.getProtocol() != PROTOCOL )
			failTest ( "getProtocol fails");
		if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			failTest ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			failTest ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			failTest ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			failTest ( "getTargetUri fails");
			
		
		if ( msg.hasCommitted() != committed )
			failTest ( "hasCommitted fails");
		
	}	
	
	
}
