package com.atomikos.icatch;

import junit.framework.TestCase;

public class HeurMixedExceptionTestJUnit extends TestCase {

	HeurMixedException exception1;
	
	HeurMixedException exception2;
	
	HeurMixedException exception3;
	
	HeurMixedException exception4;
	
	HeuristicMessage[] msgs, commitMsgs , abortMsgs;
	
	protected void setUp() throws Exception {
		super.setUp();
		msgs = new StringHeuristicMessage[1];
		commitMsgs = new StringHeuristicMessage[2];
		abortMsgs = new StringHeuristicMessage[3];
		exception1 = new HeurMixedException ( msgs );
		exception2 = new HeurMixedException ( abortMsgs , commitMsgs );
		exception3 = new HeurMixedException ( abortMsgs , null );
		exception4 = new HeurMixedException ( null , commitMsgs );
	}
	
	public void testWithHeuristics()
	{
		int len = exception1.getHeuristicMessages().length;
		assertEquals ( len , msgs.length );
		assertNull ( exception1.getHeuristicCommitMessages() );
		assertNull ( exception1.getHeuristicRollbackMessages() );
	}
	
	public void testWithCommitHeuristicsAndNullAborts()
	{
		int len = exception4.getHeuristicMessages().length;
		assertEquals ( len ,commitMsgs.length );
		assertNull ( exception4.getHeuristicRollbackMessages() );
		assertNotNull ( exception4.getHeuristicCommitMessages() );
	}
	
	public void testWithRollbackHeuristicsAndNullCommits()
	{
		int len = exception3.getHeuristicMessages().length;
		assertEquals ( len , abortMsgs.length );
		assertNull ( exception3.getHeuristicCommitMessages() );
		assertNotNull ( exception3.getHeuristicRollbackMessages() );
	}
	
	public void testWithRollbackAndCommitMsgs()
	{
		int len = exception2.getHeuristicMessages().length;
		assertEquals ( len , abortMsgs.length + commitMsgs.length );
		assertNotNull ( exception2.getHeuristicCommitMessages() );
		assertNotNull ( exception2.getHeuristicRollbackMessages() );
	}

}
