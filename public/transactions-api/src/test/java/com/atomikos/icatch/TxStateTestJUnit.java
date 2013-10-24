package com.atomikos.icatch;

import junit.framework.TestCase;

public class TxStateTestJUnit extends TestCase {
	
	public void test()
	{
		TxState[] states = TxState.values();
		
		assertEquals ( 15 , states.length );
	}

}
