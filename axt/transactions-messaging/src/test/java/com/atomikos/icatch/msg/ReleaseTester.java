package com.atomikos.icatch.msg;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;

import com.atomikos.diagnostics.Console;
import com.atomikos.diagnostics.PrintStreamConsole;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.CompositeTransactionAdaptor;
import com.atomikos.icatch.imp.PropagationImp;
import com.atomikos.icatch.system.Configuration;

 /**
  *Copyright &copy; 2002, Guy Pardon. All rights reserved.
  *
  *A release tester for the message-based commitment
  *package. This package is the core of all practical
  *message commit protocols such as BTP, WS-T, ...
  *When invoked as the main class, this tester will
  *perform the entire release test with a standalone TM
  *and a TestMessageFactory instance.
  */
  
public class ReleaseTester
{
    
    private static final String TARGETURI = "TestUri";
    private static final String TARGETADDRESS = "TestAddress";
    private static final String SENDERURI = "SenderUri";
    private static final String SENDERADDRESS = "SenderAddress";
    private static final int FORMAT = TransactionMessage.FORMAT_UNKNOWN;
    private static final int PROTOCOL = Transport.UNKNOWN_PROTOCOL;    
    
    /**
     * Test message functionality.
     * @throws Exception
     */
    private static void testCommitMessage ( boolean onePhase )
    throws Exception
    {
    	CommitMessage msg = new CommitMessageImp
    		( PROTOCOL , FORMAT ,
    		TARGETADDRESS , TARGETURI ,
    		SENDERADDRESS , SENDERURI , onePhase );
    	
    	if ( msg.getFormat() != FORMAT )
    		throw new Exception ("getFormat fails");
    	if ( msg.getMessageType() != TransactionMessage.COMMIT_MESSAGE )
    		throw new Exception ( "getMessageType fails ");
    	if ( msg.getProtocol() != PROTOCOL )
    		throw new Exception ( "getProtocol fails");
    	if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			throw new Exception ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			throw new Exception ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			throw new Exception ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			throw new Exception ( "getTargetUri fails");
		
		if ( msg.isOnePhase() != onePhase )
			throw new Exception ( "isOnePhase fails");
    }
    
	private static void testForgetMessage()
	throws Exception
	{
		ForgetMessageImp msg = new ForgetMessageImp (
			PROTOCOL , FORMAT , TARGETADDRESS, TARGETURI,
			SENDERADDRESS , SENDERURI 
		);
    	
		if ( msg.getFormat() != FORMAT )
			throw new Exception ("getFormat fails");
		if ( msg.getMessageType() != TransactionMessage.FORGET_MESSAGE )
			throw new Exception ( "getMessageType fails ");
		if ( msg.getProtocol() != PROTOCOL )
			throw new Exception ( "getProtocol fails");
		if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			throw new Exception ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			throw new Exception ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			throw new Exception ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			throw new Exception ( "getTargetUri fails");
		
		 	
	}
    
    private static void testErrorMessage()
    throws Exception
    {
    	ErrorMessageImp msg = new ErrorMessageImp (
    		PROTOCOL , FORMAT , TARGETADDRESS, TARGETURI,
    		SENDERADDRESS , SENDERURI , 3
    	);
    	
		if ( msg.getFormat() != FORMAT )
			throw new Exception ("getFormat fails");
		if ( msg.getMessageType() != TransactionMessage.ERROR_MESSAGE )
			throw new Exception ( "getMessageType fails ");
		if ( msg.getProtocol() != PROTOCOL )
			throw new Exception ( "getProtocol fails");
		if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			throw new Exception ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			throw new Exception ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			throw new Exception ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			throw new Exception ( "getTargetUri fails");
		
		if ( msg.getErrorCode() != 3 )
			throw new Exception ( "getErrorCode fails");    	
    }

	private static void testPreparedMessage ( 
		boolean readOnly , boolean defaultIsRollback )
	throws Exception
	{
		PreparedMessageImp msg = new PreparedMessageImp (
			PROTOCOL , FORMAT , TARGETADDRESS, TARGETURI,
			SENDERADDRESS , SENDERURI , 
			readOnly , defaultIsRollback
		);
    	
		if ( msg.getFormat() != FORMAT )
			throw new Exception ("getFormat fails");
		if ( msg.getMessageType() != TransactionMessage.PREPARED_MESSAGE )
			throw new Exception ( "getMessageType fails ");
		if ( msg.getProtocol() != PROTOCOL )
			throw new Exception ( "getProtocol fails");
		if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			throw new Exception ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			throw new Exception ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			throw new Exception ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			throw new Exception ( "getTargetUri fails");
		
		if ( msg.isReadOnly() != readOnly )
			throw new Exception ( "isReadOnly fails");
		if ( msg.defaultIsRollback() != defaultIsRollback )
			throw new Exception ( "defaultIsRollback fails"); 	
	}
	
	private static void testPrepareMessage ( boolean orphans )
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
					throw new Exception ("getGlobalSiblingCount fails");
				if ( msg.getCascadeInfo() == null || 
					msg.getCascadeInfo().length != 1 )
					throw new Exception ( "getCascadeInfo fails");
				info = msg.getCascadeInfo();
				if ( info[0].count != 1 )
					throw new Exception ( "Wrong cascade count");
				if (! info[0].participant.equals ( TARGETURI ))
					throw new Exception ("Wrong cascade participant");
				
						 
		}
		else {
			msg = new PrepareMessageImp (
								PROTOCOL, FORMAT, TARGETADDRESS,
								TARGETURI , SENDERADDRESS , SENDERURI);
		}
		
		if ( msg.getFormat() != FORMAT )
			throw new Exception ("getFormat fails");
		if ( msg.getMessageType() != TransactionMessage.PREPARE_MESSAGE )
			throw new Exception ( "getMessageType fails ");
		if ( msg.getProtocol() != PROTOCOL )
			throw new Exception ( "getProtocol fails");
		if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			throw new Exception ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			throw new Exception ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			throw new Exception ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			throw new Exception ( "getTargetUri fails");

		if ( msg.hasOrphanInfo() != orphans )
			throw new Exception ( "hasOrphanInfo fails");
		
	}
    
    private static void testReplayMessage()
    throws Exception
    {
		ReplayMessageImp msg = new ReplayMessageImp (
			PROTOCOL , FORMAT , TARGETADDRESS, TARGETURI,
			SENDERADDRESS , SENDERURI 
		);
    	
		if ( msg.getFormat() != FORMAT )
			throw new Exception ("getFormat fails");
		if ( msg.getMessageType() != TransactionMessage.REPLAY_MESSAGE )
			throw new Exception ( "getMessageType fails ");
		if ( msg.getProtocol() != PROTOCOL )
			throw new Exception ( "getProtocol fails");
		if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			throw new Exception ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			throw new Exception ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			throw new Exception ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			throw new Exception ( "getTargetUri fails");
			
		
    }

	private static void testRollbackMessage()
	throws Exception
	{
		RollbackMessageImp msg = new RollbackMessageImp (
			PROTOCOL , FORMAT , TARGETADDRESS, TARGETURI,
			SENDERADDRESS , SENDERURI 
		);
    	
		if ( msg.getFormat() != FORMAT )
			throw new Exception ("getFormat fails");
		if ( msg.getMessageType() != TransactionMessage.ROLLBACK_MESSAGE )
			throw new Exception ( "getMessageType fails ");
		if ( msg.getProtocol() != PROTOCOL )
			throw new Exception ( "getProtocol fails");
		if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			throw new Exception ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			throw new Exception ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			throw new Exception ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			throw new Exception ( "getTargetUri fails");
			
		
	}
	
	private static void testStateMessage ( Boolean committed )
	throws Exception
	{
		StateMessageImp msg = new StateMessageImp (
			PROTOCOL , FORMAT , TARGETADDRESS, TARGETURI,
			SENDERADDRESS , SENDERURI , committed
		);
    	
		if ( msg.getFormat() != FORMAT )
			throw new Exception ("getFormat fails");
		if ( msg.getMessageType() != TransactionMessage.STATE_MESSAGE )
			throw new Exception ( "getMessageType fails ");
		if ( msg.getProtocol() != PROTOCOL )
			throw new Exception ( "getProtocol fails");
		if ( !msg.getSenderAddress().equals ( SENDERADDRESS ) )
			throw new Exception ("getSenderAddress fails");
		if ( !msg.getSenderURI().equals ( SENDERURI))
			throw new Exception ("getSenderURI fails");
		if ( !msg.getTargetAddress().equals ( TARGETADDRESS))
			throw new Exception ( "getTargetAddress fails");
		if ( !msg.getTargetURI().equals (TARGETURI )) 
			throw new Exception ( "getTargetUri fails");
			
		
		if ( msg.hasCommitted() != committed )
			throw new Exception ( "hasCommitted fails");
		
	}	
    
    public static void testMessages () throws Exception
    {
    	testCommitMessage ( true );
    	testCommitMessage ( false );
    	testErrorMessage();
    	testForgetMessage();
    	testPreparedMessage ( false , false );
    	testPreparedMessage ( false , true );
    	testPreparedMessage ( true , false );
    	testPreparedMessage ( true , true );
    	testPrepareMessage ( false );
    	testPrepareMessage ( true );
    	testReplayMessage();
    	testRollbackMessage();
    	testStateMessage ( null );
    	testStateMessage ( new Boolean ( true ));
    	testStateMessage ( new Boolean ( false ));
    }
    
     /**
      *Perform a test with the given MessageFactory.
      *@param address The address of the transport.
      *@param factory The factory to use.
      *@param uts The user transaction service to use.
      *@param console The console to log to. Null if none.
      *@exception Exception On error.
      */
      
    public static void test ( 
        String address, 
        UserTransactionService uts,
        Console console )
    throws Exception
    {
        TransactionService ts = null;
        CompositeTransactionManager ctm = null;
        MessageRecoveryCoordinator mrc =  null;
        MessageParticipant mp = null;
        TestMsgCoordinator tmc = null;
        TestMsgParticipant tmp = null;
        TestTransport transport = null;
        TSInitInfo info = null;
        CompositeTransaction ct = null;
        CompositeTransaction parent = null;
        String rootUri = null;
        String partUri = null;
        String localRootUri = null;
        Stack lineage = null;
        Propagation propagation = null;
        
        
        
        //
        //SETUP TRANSPORT AND TS
        //
        
        transport = new TestTransport ( address , address, console );
        CommitServer cs = new CommitServer();
        
        info = uts.createTSInitInfo();
        info.registerResource ( transport );
        uts.init ( info );
        ctm = uts.getCompositeTransactionManager();
        ts = Configuration.getTransactionService();
        Transport[] transports = new Transport[1];
        transports[0] = transport;
        

		cs.init ( ts , transports );
		cs.init ( true , new Properties() );
		
        //
        //CASE 1: test normal behaviour of both coordinator and participant
        //
        
        rootUri = "root1";
        partUri = "part1";
        //because the test uses the same transport for ALL
        //parties, we use a different ID to represent the 
        //locally imported root. Otherwise, delivery 
        //problems will occur.
        localRootUri = "Imported:" + rootUri;
        
        tmc = new TestMsgCoordinator ( transport , 
            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
            localRootUri );
            
        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_AFTER_PREPARE );
        mrc = new MessageRecoveryCoordinator ( 
            rootUri, transport.getParticipantAddress() , transport );
        parent = new CompositeTransactionAdaptor ( 
            localRootUri , true , mrc );
        lineage = new Stack();
        lineage.push ( parent );
        propagation = new PropagationImp ( lineage, true , 5000 );
        ct = ts.recreateCompositeTransaction ( propagation , false , false );
        
        tmp = new TestMsgParticipant ( transport , 
            partUri , transport.getParticipantAddress() , 
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
            throw new Exception ( "Participant received no prepare?" );
        if ( !tmp.hasReceivedCommit() )
            throw new Exception ( "Participant received no commit?" );
        if ( tmp.hasReceivedRollback() )
            throw new Exception ( "Participant received rollback?" );
        if ( tmp.hasReceivedForget() )
            throw new Exception ( "Participant received forget?" );
        
        //assert that the coordinator has received the right 
        //messages
        
        if ( ! tmc.hasReceivedPrepared() )
            throw new Exception ( "Coordinator received no prepared?" );
         if ( ! tmc.hasReceivedCommitted() )
            throw new Exception ( "Coordinator received no committed?" );
         if ( tmc.hasReceivedRolledback() )
            throw new Exception ( "Coordinator received rolledback?" );
         if ( tmc.getError() >=0 )
            throw new Exception ( "Coordinator received error " + tmc.getError() );
        
        //
        //CASE 2: normal participant, coordinator rollback after prepare
        //
        
        rootUri = "root2";
        partUri = "part2";
        //because the test uses the same transport for ALL
        //parties, we use a different ID to represent the 
        //locally imported root. Otherwise, delivery 
        //problems will occur.
        localRootUri = "Imported:" + rootUri;
        
        tmc = new TestMsgCoordinator ( transport , 
            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
            localRootUri );
            
        tmc.setBehaviour ( TestMsgCoordinator.ROLLBACK_AFTER_PREPARE );
        mrc = new MessageRecoveryCoordinator ( 
            rootUri, transport.getParticipantAddress() , transport );
        parent = new CompositeTransactionAdaptor ( 
            localRootUri , true , mrc );
        lineage = new Stack();
        lineage.push ( parent );
        propagation = new PropagationImp ( lineage, true , 5000 );
        ct = ts.recreateCompositeTransaction ( propagation , false , false );
        
        tmp = new TestMsgParticipant ( transport , 
            partUri , transport.getParticipantAddress() , 
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
            throw new Exception ( "Participant received no prepare?" );
        if ( tmp.hasReceivedCommit() )
            throw new Exception ( "Participant received  commit?" );
        if ( !tmp.hasReceivedRollback() )
            throw new Exception ( "Participant received no rollback?" );
        if ( tmp.hasReceivedForget() )
            throw new Exception ( "Participant received forget?" );
        
        //assert that the coordinator has received the right 
        //messages
        
        if ( ! tmc.hasReceivedPrepared() )
            throw new Exception ( "Coordinator received no prepared?" );
         if ( tmc.hasReceivedCommitted() )
            throw new Exception ( "Coordinator received  committed?" );
         if ( ! tmc.hasReceivedRolledback() )
            throw new Exception ( "Coordinator received no rolledback?" );
         if ( tmc.getError() >=0 )
            throw new Exception ( "Coordinator received error " + tmc.getError() );
        
        //
        //CASE 3: normal participant, but coordinator rollback before prepare
        //
        
        rootUri = "root3";
        partUri = "part3";
        //because the test uses the same transport for ALL
        //parties, we use a different ID to represent the 
        //locally imported root. Otherwise, delivery 
        //problems will occur.
        localRootUri = "Imported:" + rootUri;
        
        tmc = new TestMsgCoordinator ( transport , 
            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
            localRootUri );
            
        tmc.setBehaviour ( TestMsgCoordinator.ROLLBACK_BEFORE_PREPARE );
        mrc = new MessageRecoveryCoordinator ( 
            rootUri, transport.getParticipantAddress() , transport );
        parent = new CompositeTransactionAdaptor ( 
            localRootUri , true , mrc );
        lineage = new Stack();
        lineage.push ( parent );
        propagation = new PropagationImp ( lineage, true , 5000  );
        ct = ts.recreateCompositeTransaction ( propagation , false , false );
        
        tmp = new TestMsgParticipant ( transport , 
            partUri , transport.getParticipantAddress() , 
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
            throw new Exception ( "Participant received prepare?" );
        if ( tmp.hasReceivedCommit() )
            throw new Exception ( "Participant received commit?" );
        if ( !tmp.hasReceivedRollback() )
            throw new Exception ( "Participant received no rollback?" );
        if ( tmp.hasReceivedForget() )
            throw new Exception ( "Participant received forget?" );
        
        //assert that the coordinator has received the right 
        //messages
        
        if ( tmc.hasReceivedPrepared() )
            throw new Exception ( "Coordinator received prepared?" );
         if ( tmc.hasReceivedCommitted() )
            throw new Exception ( "Coordinator received committed?" );
         if ( !tmc.hasReceivedRolledback() )
            throw new Exception ( "Coordinator received no rolledback?" );
         if ( tmc.getError() >=0 )
            throw new Exception ( "Coordinator received error " + tmc.getError() );
        
        //
        //CASE 4: normal, but one-phase commit 
        //
        
        rootUri = "root4";
        partUri = "part4";
        //because the test uses the same transport for ALL
        //parties, we use a different ID to represent the 
        //locally imported root. Otherwise, delivery 
        //problems will occur.
        localRootUri = "Imported:" + rootUri;
        
        tmc = new TestMsgCoordinator ( transport , 
            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
            localRootUri );
            
        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_ONE_PHASE );
        mrc = new MessageRecoveryCoordinator ( 
            rootUri, transport.getParticipantAddress() , transport );
        parent = new CompositeTransactionAdaptor ( 
            localRootUri , true , mrc );
        lineage = new Stack();
        lineage.push ( parent );
        propagation = new PropagationImp ( lineage, true , 5000 );
        ct = ts.recreateCompositeTransaction ( propagation , false , false );
        
        tmp = new TestMsgParticipant ( transport , 
            partUri , transport.getParticipantAddress() , 
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
            throw new Exception ( "Participant received  prepare?" );
        if ( !tmp.hasReceivedCommit() )
            throw new Exception ( "Participant received no commit?" );
        if ( tmp.hasReceivedRollback() )
            throw new Exception ( "Participant received rollback?" );
        if ( tmp.hasReceivedForget() )
            throw new Exception ( "Participant received forget?" );
        
        //assert that the coordinator has received the right 
        //messages
        
        if ( tmc.hasReceivedPrepared() )
            throw new Exception ( "Coordinator received  prepared?" );
         if ( ! tmc.hasReceivedCommitted() )
            throw new Exception ( "Coordinator received no committed?" );
         if ( tmc.hasReceivedRolledback() )
            throw new Exception ( "Coordinator received rolledback?" );
         if ( tmc.getError() >=0 )
            throw new Exception ( "Coordinator received error " + tmc.getError() );
        
        //
        //CASE 5: Normal coordinator, readonly participant
        //
        
        rootUri = "root5";
        partUri = "part5";
        //because the test uses the same transport for ALL
        //parties, we use a different ID to represent the 
        //locally imported root. Otherwise, delivery 
        //problems will occur.
        localRootUri = "Imported:" + rootUri;
        
        tmc = new TestMsgCoordinator ( transport , 
            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
            localRootUri );
        
        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_AFTER_PREPARE );

        mrc = new MessageRecoveryCoordinator ( 
            rootUri, transport.getParticipantAddress() , transport );
        parent = new CompositeTransactionAdaptor ( 
            localRootUri , true , mrc );
        lineage = new Stack();
        lineage.push ( parent );
        propagation = new PropagationImp ( lineage, true , 5000 );
        ct = ts.recreateCompositeTransaction ( propagation , false , false );
        
        tmp = new TestMsgParticipant ( transport , 
            partUri , transport.getParticipantAddress() , 
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
            throw new Exception ( "Participant received no prepare?" );
        if ( tmp.hasReceivedCommit() )
            throw new Exception ( "Participant received commit?" );
        if ( tmp.hasReceivedRollback() )
            throw new Exception ( "Participant received rollback?" );
        if ( tmp.hasReceivedForget() )
            throw new Exception ( "Participant received forget?" );
        
        //assert that the coordinator has received the right 
        //messages
        
        if ( !tmc.hasReceivedPrepared() )
            throw new Exception ( "Coordinator received no prepared?" );
         if ( tmc.hasReceivedCommitted() )
            throw new Exception ( "Coordinator received committed?" );
         if ( tmc.hasReceivedRolledback() )
            throw new Exception ( "Coordinator received rolledback?" );
         if ( tmc.getError() >=0 )
            throw new Exception ( "Coordinator received error " + tmc.getError() );
        
        //
        //CASE 6: Normal coordinator, participant rolled back before prepare
        //
        
        rootUri = "root6";
        partUri = "part6";
        //because the test uses the same transport for ALL
        //parties, we use a different ID to represent the 
        //locally imported root. Otherwise, delivery 
        //problems will occur.
        localRootUri = "Imported:" + rootUri;
        
        tmc = new TestMsgCoordinator ( transport , 
            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
            localRootUri );
        
        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_AFTER_PREPARE );

        mrc = new MessageRecoveryCoordinator ( 
            rootUri, transport.getParticipantAddress() , transport );
        parent = new CompositeTransactionAdaptor ( 
            localRootUri , true , mrc );
        lineage = new Stack();
        lineage.push ( parent );
        propagation = new PropagationImp ( lineage, true , 5000 );
        ct = ts.recreateCompositeTransaction ( propagation , false , false );
        
        tmp = new TestMsgParticipant ( transport , 
            partUri , transport.getParticipantAddress() , 
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
            throw new Exception ( "Participant received no prepare?" );
        if ( tmp.hasReceivedCommit() )
            throw new Exception ( "Participant received commit?" );
        if ( tmp.hasReceivedRollback() )
            throw new Exception ( "Participant received rollback?" );
        if ( tmp.hasReceivedForget() )
            throw new Exception ( "Participant received forget?" );
        
        //assert that the coordinator has received the right 
        //messages
        
        if ( tmc.hasReceivedPrepared() )
            throw new Exception ( "Coordinator received prepared?" );
         if ( tmc.hasReceivedCommitted() )
            throw new Exception ( "Coordinator received committed?" );
         if ( tmc.hasReceivedRolledback() )
            throw new Exception ( "Coordinator received rolledback?" );
         if ( tmc.getError() < 0 )
            throw new Exception ( "Coordinator received no error?" );
        
        //
        //CASE 7: normal coordinator, participant heuristic abort
        //
        
        rootUri = "root7";
        partUri = "part7";
        //because the test uses the same transport for ALL
        //parties, we use a different ID to represent the 
        //locally imported root. Otherwise, delivery 
        //problems will occur.
        localRootUri = "Imported:" + rootUri;
        
        tmc = new TestMsgCoordinator ( transport , 
            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
            localRootUri );
        
        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_AFTER_PREPARE );

        mrc = new MessageRecoveryCoordinator ( 
            rootUri, transport.getParticipantAddress() , transport );
        parent = new CompositeTransactionAdaptor ( 
            localRootUri , true , mrc );
        lineage = new Stack();
        lineage.push ( parent );
        propagation = new PropagationImp ( lineage, true , 5000 );
        ct = ts.recreateCompositeTransaction ( propagation , false , false );
        
        tmp = new TestMsgParticipant ( transport , 
            partUri , transport.getParticipantAddress() , 
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
            throw new Exception ( "Participant received no prepare?" );
        if ( ! tmp.hasReceivedCommit() )
            throw new Exception ( "Participant received no commit?" );
        if ( tmp.hasReceivedRollback() )
            throw new Exception ( "Participant received rollback?" );
        //by default, NO forget propagation is done!
        if ( tmp.hasReceivedForget() )
            throw new Exception ( "Participant received forget?" );
        
        //assert that the coordinator has received the right 
        //messages
        
        if ( !tmc.hasReceivedPrepared() )
            throw new Exception ( "Coordinator received no prepared?" );
         if ( tmc.hasReceivedCommitted() )
            throw new Exception ( "Coordinator received committed?" );
         if ( tmc.hasReceivedRolledback() )
            throw new Exception ( "Coordinator received rolledback?" );
         if ( tmc.getError() != ErrorMessage.HEUR_ROLLBACK_ERROR )
            throw new Exception ( "Coordinator received no hrb error?" );
        
        //
        //CASE 8: normal coordinator, participant heuristic mixed
        //
        
        rootUri = "root8";
        partUri = "part8";
        //because the test uses the same transport for ALL
        //parties, we use a different ID to represent the 
        //locally imported root. Otherwise, delivery 
        //problems will occur.
        localRootUri = "Imported:" + rootUri;
        
        tmc = new TestMsgCoordinator ( transport , 
            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
            localRootUri );
        
        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_AFTER_PREPARE );

        mrc = new MessageRecoveryCoordinator ( 
            rootUri, transport.getParticipantAddress() , transport );
        parent = new CompositeTransactionAdaptor ( 
            localRootUri , true , mrc );
        lineage = new Stack();
        lineage.push ( parent );
        propagation = new PropagationImp ( lineage, true , 5000 );
        ct = ts.recreateCompositeTransaction ( propagation , false , false );
        
        tmp = new TestMsgParticipant ( transport , 
            partUri , transport.getParticipantAddress() , 
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
            throw new Exception ( "Participant received no prepare?" );
        if ( ! tmp.hasReceivedCommit() )
            throw new Exception ( "Participant received no commit?" );
        if ( tmp.hasReceivedRollback() )
            throw new Exception ( "Participant received rollback?" );
        //by default, NO forget propagation is done!
        if ( tmp.hasReceivedForget() )
            throw new Exception ( "Participant received forget?" );
        
        //assert that the coordinator has received the right 
        //messages
        
        if ( !tmc.hasReceivedPrepared() )
            throw new Exception ( "Coordinator received no prepared?" );
         if ( tmc.hasReceivedCommitted() )
            throw new Exception ( "Coordinator received committed?" );
         if ( tmc.hasReceivedRolledback() )
            throw new Exception ( "Coordinator received rolledback?" );
         if ( tmc.getError() != ErrorMessage.HEUR_MIXED_ERROR )
            throw new Exception ( "Coordinator received no hm error?" );
        
        
        //
        //CASE 9: normal coordinator, participant heuristic hazard
        //
        
        rootUri = "root9";
        partUri = "part9";
        //because the test uses the same transport for ALL
        //parties, we use a different ID to represent the 
        //locally imported root. Otherwise, delivery 
        //problems will occur.
        localRootUri = "Imported:" + rootUri;
        
        tmc = new TestMsgCoordinator ( transport , 
            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
            localRootUri );
        
        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_AFTER_PREPARE );

        mrc = new MessageRecoveryCoordinator ( 
            rootUri, transport.getParticipantAddress() , transport );
        parent = new CompositeTransactionAdaptor ( 
            localRootUri , true , mrc );
        lineage = new Stack();
        lineage.push ( parent );
        propagation = new PropagationImp ( lineage, true , 5000 );
        ct = ts.recreateCompositeTransaction ( propagation , false , false );
        
        tmp = new TestMsgParticipant ( transport , 
            partUri , transport.getParticipantAddress() , 
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
            throw new Exception ( "Participant received no prepare?" );
        if ( ! tmp.hasReceivedCommit() )
            throw new Exception ( "Participant received no commit?" );
        if ( tmp.hasReceivedRollback() )
            throw new Exception ( "Participant received rollback?" );
        //by default, NO forget propagation is done!
        if ( tmp.hasReceivedForget() )
            throw new Exception ( "Participant received forget?" );
        
        //assert that the coordinator has received the right 
        //messages
        
        if ( !tmc.hasReceivedPrepared() )
            throw new Exception ( "Coordinator received no prepared?" );
         if ( tmc.hasReceivedCommitted() )
            throw new Exception ( "Coordinator received committed?" );
         if ( tmc.hasReceivedRolledback() )
            throw new Exception ( "Coordinator received rolledback?" );
         if ( tmc.getError() != ErrorMessage.HEUR_HAZARD_ERROR )
            throw new Exception ( "Coordinator received no hm error?" );
        
        
        //
        //CASE 10: normal coordinator, participant heuristic commit
        //
        
        rootUri = "root10";
        partUri = "part10";
        //because the test uses the same transport for ALL
        //parties, we use a different ID to represent the 
        //locally imported root. Otherwise, delivery 
        //problems will occur.
        localRootUri = "Imported:" + rootUri;
        
        tmc = new TestMsgCoordinator ( transport , 
            rootUri, transport.getParticipantAddress(), transport.getParticipantAddress(), 
            localRootUri );
        
        tmc.setBehaviour ( TestMsgCoordinator.COMMIT_AFTER_PREPARE );

        mrc = new MessageRecoveryCoordinator ( 
            rootUri, transport.getParticipantAddress() , transport );
        parent = new CompositeTransactionAdaptor ( 
            localRootUri , true , mrc );
        lineage = new Stack();
        lineage.push ( parent );
        propagation = new PropagationImp ( lineage, true , 5000 );
        ct = ts.recreateCompositeTransaction ( propagation , false , false );
        
        tmp = new TestMsgParticipant ( transport , 
            partUri , transport.getParticipantAddress() , 
            transport.getParticipantAddress() , localRootUri );
        tmp.setBehaviour ( TestMsgParticipant.HEURISTIC_COMMIT );
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
            throw new Exception ( "Participant received no prepare?" );
        if ( ! tmp.hasReceivedCommit() )
            throw new Exception ( "Participant received no commit?" );
        if ( tmp.hasReceivedRollback() )
            throw new Exception ( "Participant received rollback?" );
        //by default, NO forget propagation is done!
        if ( tmp.hasReceivedForget() )
            throw new Exception ( "Participant received forget?" );
        
        //assert that the coordinator has received the right 
        //messages
        
        if ( !tmc.hasReceivedPrepared() )
            throw new Exception ( "Coordinator received no prepared?" );
         if ( !tmc.hasReceivedCommitted() )
            throw new Exception ( "Coordinator received no committed?" );
         if ( tmc.hasReceivedRolledback() )
            throw new Exception ( "Coordinator received rolledback?" );
         if ( tmc.getError() >=0 )
            throw new Exception ( "Coordinator received error?" );
        
        //
        //LASTLY, SHUTDOWN TS
        //
        
        uts.shutdown ( true );
        
    }
  
    public static void main ( String[] args )
    {
        if ( args.length != 1 ) {
            System.err.println ( "1 argument required: name of output file..." ); 
            System.exit ( 1 );
        }
        
        try {
            
            testMessages();
            //the default test is one with a TestMessageFactory 
            //and a standalone TS
            String outfile = args[0];
			UserTransactionService uts =
			new com.atomikos.icatch.standalone.
				UserTransactionServiceFactory().getUserTransactionService( new Properties() );
			TSInitInfo info = uts.createTSInitInfo();
			info.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
            OutputStream out = new FileOutputStream ( 
                args[0] , true );
            PrintStream ps = new PrintStream ( out );
            PrintStreamConsole console = new PrintStreamConsole ( ps );
            
            test ( "TestAddress" , uts, console );
            
        }
        catch ( Exception e ) {
            e.printStackTrace(); 
        }
       
        System.out.println ( "DONE!" );
    } 
}
