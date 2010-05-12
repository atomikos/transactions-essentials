//$Id: ReleaseTester.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: ReleaseTester.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.2  2006/04/11 11:42:57  guy
//Extracted init properties as constants and replaced all literal references.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:33  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2005/05/09 08:07:23  guy
//Added tests for JCA package.
//
package com.atomikos.icatch.jca;

import java.util.Properties;

import javax.naming.Context;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.atomikos.datasource.xa.XID;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.HeuristicParticipant;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.TestParticipant;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * Tests for the JCA inbound transaction package.
 *
 * 
 */

public class ReleaseTester
{
	
	private static void testInboundTransaction()
	throws Exception
	{
	
		//
		//CASE 1: assert regular 2PC works
		//
		TransactionInflowHelper jcaHelper =
			new TransactionInflowHelper();
		Xid xid = new XID ( "test" , "xid" );
		InboundTransaction tx = 
			jcaHelper.importTransactionWithXid ( xid , 10000 , false );
		CompositeTransactionManager ctm = 
			Configuration.getCompositeTransactionManager();
		
		if ( ctm.getCompositeTransaction() != null )
			throw new Exception ( "Tx for thread after import");
			
		tx.resume();
		if ( ctm.getCompositeTransaction() == null )
			throw new Exception ( "No tx for thread after resume" );
			
		CompositeTransaction ct = ctm.getCompositeTransaction();
		ct.addParticipant ( new TestParticipant () );		
		
		tx.suspend();
		if ( ctm.getCompositeTransaction() != null )
					throw new Exception ( "Tx for thread after suspend");
		
		tx.end ( true );
		if ( ctm.getCompositeTransaction() != null )
					throw new Exception ( "Tx for thread after end");
		
		
		XATerminatorImp terminator = jcaHelper.getXATerminator();
		terminator.prepare ( xid );
		
		//assert that recover returns the xid
		Xid[] xids = terminator.recover( XAResource.TMSTARTRSCAN );
		boolean found = false;
		//System.out.println ( "Looking for: " + xid );
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
			//System.out.println ( "inspecting: " + xids[i] );
		}
		if ( ! found ) throw new Exception ( "Indoubt xid not recovered");
		
		
		
		terminator.commit ( xid , false );
		//assert that recover does NOT return the xid 
		
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( found ) throw new Exception ( "Committed xid recovered?");		
		
		try {
			tx.resume();
			throw new Exception ( "Resume works after end?" );
		}
		catch ( IllegalStateException normal ) {}
		
		//
		//CASE 2: assert that end ( false ) works
		//
		xid = new XID ( "test" , "end(false)");
		tx = jcaHelper.importTransactionWithXid ( xid , 1000 , false );
		tx.resume();
		tx.end ( false );
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( found ) throw new Exception ( "Ended(false) xid recovered?");		
		
		//
		//CASE 3: assert that prepare/rollback works
		//		
		
		xid = new XID ( "test" , "prepare/rollback");
		tx = jcaHelper.importTransactionWithXid ( xid , 1000 ,  false );
		tx.resume();
		ct = ctm.getCompositeTransaction();
		ct.addParticipant ( new TestParticipant () );
		tx.end ( true );
		
		int result = terminator.prepare ( xid );
		if ( result != XAResource.XA_OK )
			throw new Exception ( "prepare OK fails");
		terminator.rollback ( xid );
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( found ) throw new Exception ( "Prepare/rollback xid recovered?");		
				
		
		//
		//CASE 4: assert that rollback before prepare works
		//
		
		xid = new XID ( "test" , "rollback" );
		tx = jcaHelper.importTransactionWithXid ( xid , 1000 , false );
		tx.resume();
		tx.end ( true );
		terminator.rollback ( xid );
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( found ) throw new Exception ( "Rollback xid recovered?");		
		
		//
		//CASE 5A: assert that heuristic RB + forget works
		//		
		
		xid = new XID ( "test" , "forget" );
		tx = jcaHelper.importTransactionWithXid ( xid , 1000 , false );
		tx.resume();
		ct = ctm.getCompositeTransaction();
		HeuristicParticipant hp = new HeuristicParticipant( new StringHeuristicMessage ( "test"));
		hp.setFailMode ( HeuristicParticipant.FAIL_HEUR_ROLLBACK );
		ct.addParticipant ( hp );
		tx.end ( true );
		terminator.prepare ( xid );
		try {
			terminator.commit ( xid , false );
		}
		catch ( XAException normal ) {
			if ( normal.errorCode != XAException.XA_HEURRB )
				throw new Exception ( "Wrong errorCode on heuristic");	
		}
		
		//assert that xid is recovered
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( !found ) throw new Exception ( "Heuristic xid not recovered?");	
		

		terminator.forget ( xid );
		
		//assert xid is now gone
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( found ) throw new Exception ( "Forgotten xid recovered?");		

		//
		//CASE 5B: assert that heuristic HAZ + forget works
		//		
		
		xid = new XID ( "test" , "forgethazard" );
		tx = jcaHelper.importTransactionWithXid ( xid , 1000 , false );
		tx.resume();
		ct = ctm.getCompositeTransaction();
		hp = new HeuristicParticipant( new StringHeuristicMessage ( "test"));
		hp.setFailMode ( HeuristicParticipant.FAIL_HEUR_HAZARD );
		ct.addParticipant ( hp );
		tx.end ( true );
		terminator.prepare ( xid );
		try {
			terminator.commit ( xid , false );
		}
		catch ( XAException normal ) {
			if ( normal.errorCode != XAException.XA_HEURHAZ )
				throw new Exception ( "Wrong errorCode on heuristic");	
		}
		
		//assert that xid is recovered
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( !found ) throw new Exception ( "Heuristic xid not recovered?");	
		

		terminator.forget ( xid );
		
		//assert xid is now gone
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( found ) throw new Exception ( "Forgotten xid recovered?");		


		//
		//CASE 5C: assert that heuristic MIX + forget works
		//		
		
		xid = new XID ( "test" , "forget" );
		tx = jcaHelper.importTransactionWithXid ( xid , 1000 , false );
		tx.resume();
		ct = ctm.getCompositeTransaction();
		hp = new HeuristicParticipant( new StringHeuristicMessage ( "test"));
		hp.setFailMode ( HeuristicParticipant.FAIL_HEUR_MIXED );
		ct.addParticipant ( hp );
		tx.end ( true );
		terminator.prepare ( xid );
		try {
			terminator.commit ( xid , false );
		}
		catch ( XAException normal ) {
			if ( normal.errorCode != XAException.XA_HEURMIX )
				throw new Exception ( "Wrong errorCode on heuristic");	
		}
		
		//assert that xid is recovered
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( !found ) throw new Exception ( "Heuristic xid not recovered?");	
		

		terminator.forget ( xid );
		
		//assert xid is now gone
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( found ) throw new Exception ( "Forgotten xid recovered?");		

		//
		//CASE 5D: assert that RB+ heuristic MIX + forget works
		//		
		
		xid = new XID ( "test" , "rb-mix" );
		tx = jcaHelper.importTransactionWithXid ( xid , 1000 , false );
		tx.resume();
		ct = ctm.getCompositeTransaction();
		hp = new HeuristicParticipant( new StringHeuristicMessage ( "test"));
		hp.setFailMode ( HeuristicParticipant.FAIL_HEUR_MIXED );
		ct.addParticipant ( hp );
		tx.end ( true );
		terminator.prepare ( xid );
		try {
			terminator.rollback ( xid );
		}
		catch ( XAException normal ) {
			if ( normal.errorCode != XAException.XA_HEURMIX )
				throw new Exception ( "Wrong errorCode on heuristic");	
		}
		
		//assert that xid is recovered
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( !found ) throw new Exception ( "Heuristic xid not recovered?");	
		

		terminator.forget ( xid );
		
		//assert xid is now gone
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( found ) throw new Exception ( "Forgotten xid recovered?");		

		//
		//CASE 5E: assert that RB+ heuristic HAZ + forget works
		//		
		
		xid = new XID ( "test" , "rb-haz" );
		tx = jcaHelper.importTransactionWithXid ( xid , 1000 , false );
		tx.resume();
		ct = ctm.getCompositeTransaction();
		hp = new HeuristicParticipant( new StringHeuristicMessage ( "test"));
		hp.setFailMode ( HeuristicParticipant.FAIL_HEUR_HAZARD );
		ct.addParticipant ( hp );
		tx.end ( true );
		terminator.prepare ( xid );
		try {
			terminator.rollback ( xid );
		}
		catch ( XAException normal ) {
			if ( normal.errorCode != XAException.XA_HEURHAZ )
				throw new Exception ( "Wrong errorCode on heuristic");	
		}
		
		//assert that xid is recovered
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( !found ) throw new Exception ( "Heuristic xid not recovered?");	
		

		terminator.forget ( xid );
		
		//assert xid is now gone
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( found ) throw new Exception ( "Forgotten xid recovered?");		

		//
		//CASE 5E: assert that RB+ heuristic COM + forget works
		//		
		
		xid = new XID ( "test" , "rb-com" );
		tx = jcaHelper.importTransactionWithXid ( xid , 1000 , false );
		tx.resume();
		ct = ctm.getCompositeTransaction();
		hp = new HeuristicParticipant( new StringHeuristicMessage ( "test"));
		hp.setFailMode ( HeuristicParticipant.FAIL_HEUR_COMMIT );
		ct.addParticipant ( hp );
		tx.end ( true );
		terminator.prepare ( xid );
		try {
			terminator.rollback ( xid );
		}
		catch ( XAException normal ) {
			if ( normal.errorCode != XAException.XA_HEURCOM )
				throw new Exception ( "Wrong errorCode on heuristic");	
		}
		
		//assert that xid is recovered
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( !found ) throw new Exception ( "Heuristic xid not recovered?");	
		

		terminator.forget ( xid );
		
		//assert xid is now gone
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		found = false;
		for ( int i = 0 ; i < xids.length ; i++ ) {
			if ( xids[i].equals ( xid ) ) found = true;
		}
		if ( found ) throw new Exception ( "Forgotten xid recovered?");		
						

		//
		//CASE 6: assert that prepare/readonly works
		//
		
		xid = new XID ( "test" , "readonly");
		tx = jcaHelper.importTransactionWithXid ( xid , 1000 , false );
		tx.end ( true );
		if ( terminator.prepare ( xid ) != XAResource.XA_RDONLY )
			throw new Exception ( "prepare/readonly fails");
		
		//
		//CASE 7: assert that recover flags work OK
		//	
		
		xid = new XID ( "test" , "recovery");
		tx = jcaHelper.importTransactionWithXid ( xid , 1000 ,  false );
		tx.resume();
		ct = ctm.getCompositeTransaction();
		ct.addParticipant ( new TestParticipant () );
		tx.end ( true );
		
		terminator.prepare ( xid );
		xids = terminator.recover ( XAResource.TMSTARTRSCAN );
		if ( xids == null || xids.length == 0 )
			throw new Exception ( "Recovery failure");
		
		xids = terminator.recover ( XAResource.TMNOFLAGS );
		if ( xids == null || xids.length != 0 )
			throw new Exception ( "Recovery fails with no flags");
		terminator.rollback ( xid);		

	}
	
	private static void testAutoStartup()
	throws Exception
	{
		TransactionInflowHelper jcaHelper = new TransactionInflowHelper();
		jcaHelper.setAutoStartup ( true );
		if ( ! jcaHelper.getAutoStartup() )
			throw new Exception ( "getAutoStartup failure");
		jcaHelper.setAutoStartup ( false );
		if ( jcaHelper.getAutoStartup() )
			throw new Exception ( "getAutoStartup failure");
	}
	
	
	
	
	public static void test() throws Exception
	{

		UserTransactionService uts =
				new com.atomikos.icatch.trmi.UserTransactionServiceFactory().
					getUserTransactionService ( new Properties() );
		TSInitInfo info = uts.createTSInitInfo();
		Properties properties = info.getProperties();
		properties.setProperty (
								com.atomikos.icatch.config.UserTransactionServiceImp.NO_FILE_PROPERTY_NAME , new String() );
		properties.setProperty ( 
								AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "JcaTestTransactionManager" );
		properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
		properties.setProperty ( Context.PROVIDER_URL , "rmi://localhost:1099" );
		properties.setProperty ( Context.INITIAL_CONTEXT_FACTORY , 
								 "com.sun.jndi.rmi.registry.RegistryContextFactory" );
		properties.setProperty ( AbstractUserTransactionServiceFactory.RMI_EXPORT_CLASS_PROPERTY_NAME , "UnicastRemoteObject" );
        
		
		uts.init ( info );
		
		testInboundTransaction();
		
		testAutoStartup();
		
		uts.shutdown ( true );
	}

    public static void main(String[] args)
    throws Exception
    {
    	System.out.println ( "Starting... " );
    	test();
    	System.out.println ( "Done." ); 
    }
}
