package com.atomikos.finitestates;

import junit.framework.TestCase;

public class FSMImpTestJUnit extends TestCase 
{
	
	private FSM fsm;
	private TestListener lstnr1, lstnr2, lstnr3, lstnr4;
	
	public FSMImpTestJUnit ( String name )
	{
		super ( name );
	}
	
	protected void setUp()
	{
           fsm = new FSMImp ( new TestTransitionTable() ,
   			 TestTransitionTable.INITIAL);
           
           lstnr1 =new TestListener();
           lstnr2=new TestListener();
           lstnr3=new TestListener();
           lstnr4=new TestListener();

           fsm.addFSMEnterListener(lstnr1, TestTransitionTable.MIDDLE);
           fsm.addFSMTransitionListener(lstnr2, TestTransitionTable.INITIAL,
   			       TestTransitionTable.MIDDLE);
           fsm.addFSMPreEnterListener(lstnr3, TestTransitionTable.MIDDLE);
           fsm.addFSMPreTransitionListener(lstnr4, TestTransitionTable.INITIAL,
   				TestTransitionTable.MIDDLE);
           
	}
	
	protected void tearDown()
	{
		
	}
	
	public void testIllegalTransition()
	{
		try {
			  fsm.setState(TestTransitionTable.END);
			  //should cause exception since not allowed
			  fail ("ERROR: transition checking not ok");
		 }
		 catch (IllegalStateException ok ) {
		 }
	}
	
	public void testEnterListenerNotification()
	{
        fsm.setState(TestTransitionTable.MIDDLE);
        if (!lstnr1.isNotified())
        		fail ("ERROR: notification does not work");
	}
	
	public void testTransitionListenerNotification()
	{
		fsm.setState(TestTransitionTable.MIDDLE);
        if (!lstnr2.isNotified())
        		fail ("ERROR: notification does not work");
	}
	
	public void testPreEnterListenerNotification()
	{
		fsm.setState(TestTransitionTable.MIDDLE);
        if (!lstnr3.isNotified())
        		fail ("ERROR: notification does not work");
	}
	
	public void testPreTransitionListenerNotification()
	{
		fsm.setState(TestTransitionTable.MIDDLE);
        if (!lstnr4.isNotified())
        		fail ("ERROR: notification does not work");
	}

}
