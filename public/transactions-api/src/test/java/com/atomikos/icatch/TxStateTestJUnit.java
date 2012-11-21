package com.atomikos.icatch;

import java.util.Enumeration;

import junit.framework.TestCase;

public class TxStateTestJUnit extends TestCase {
	
	public void test()
	{
		Enumeration states = TxState.getStates();
		int count = 0;
		while ( states.hasMoreElements() ) {
			states.nextElement();
			count++;
		}
		assertEquals ( 15 , count );
	}

}
