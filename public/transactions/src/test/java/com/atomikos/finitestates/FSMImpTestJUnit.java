/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

import com.atomikos.recovery.TxState;

import junit.framework.TestCase;

public class FSMImpTestJUnit extends TestCase 
{
	
    public static TxState INITIAL=TxState.ACTIVE;
    public static TxState MIDDLE=TxState.COMMITTING;
    public static TxState END=TxState.TERMINATED;

    
	private FSM fsm;
	private TestListener lstnr1, lstnr2, lstnr3, lstnr4;
	
	public FSMImpTestJUnit ( String name )
	{
		super ( name );
	}
	
	protected void setUp()
	{
           fsm = new FSMImp (new Object(), INITIAL);
           lstnr1 =new TestListener();
           lstnr2=new TestListener();
           lstnr3=new TestListener();
           lstnr4=new TestListener();
           fsm.addFSMEnterListener(lstnr1, MIDDLE);
           fsm.addFSMTransitionListener(lstnr2, INITIAL,MIDDLE);
           fsm.addFSMEnterListener(lstnr3, MIDDLE);
           fsm.addFSMTransitionListener(lstnr4, INITIAL,MIDDLE);
	}
	
	public void testIllegalTransition()
	{
		try {
			  fsm.setState(END);
			  //should cause exception since not allowed
			  fail ("ERROR: transition checking not ok");
		 }
		 catch (IllegalStateException ok ) {
		 }
	}
	
	public void testEnterListenerNotification()
	{
        fsm.setState(MIDDLE);
        if (!lstnr1.isNotified())
        		fail ("ERROR: notification does not work");
	}
	
	public void testTransitionListenerNotification()
	{
		fsm.setState(MIDDLE);
        if (!lstnr2.isNotified())
        		fail ("ERROR: notification does not work");
	}
	
	public void testPreEnterListenerNotification()
	{
		fsm.setState(MIDDLE);
        if (!lstnr3.isNotified())
        		fail ("ERROR: notification does not work");
	}
	
	public void testPreTransitionListenerNotification()
	{
		fsm.setState(MIDDLE);
        if (!lstnr4.isNotified())
        		fail ("ERROR: notification does not work");
	}

}
