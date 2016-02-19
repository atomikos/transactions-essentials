/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.finitestates;

import com.atomikos.icatch.TxState;

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
           fsm = new FSMImp (INITIAL);
           lstnr1 =new TestListener();
           lstnr2=new TestListener();
           lstnr3=new TestListener();
           lstnr4=new TestListener();
           fsm.addFSMEnterListener(lstnr1, MIDDLE);
           fsm.addFSMTransitionListener(lstnr2, INITIAL,MIDDLE);
           fsm.addFSMPreEnterListener(lstnr3, MIDDLE);
           fsm.addFSMPreTransitionListener(lstnr4, INITIAL,MIDDLE);
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
