package com.atomikos.icatch.imp;

import java.util.Stack;

import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.TestRecoveryCoordinator;

import junit.framework.TestCase;

public class PropagationImpTestJUnit extends TestCase {
	
	private static Stack lineage = new Stack();
	
	static {
		lineage.push ( new CompositeTransactionAdaptor ( 
			"ROOT" , true , new TestRecoveryCoordinator()
		) );
	}
	
	private static long TIMEOUT = 600;
	
	private PropagationImp propagation;
	
	private static PropagationImp create ( boolean serial )
	{
		
		return new PropagationImp ( 
				lineage , serial , TIMEOUT
		);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		Stack lineage = new Stack();
		
		propagation = create ( false );
	}
	
	public void testTimeout()
	{
		assertEquals ( TIMEOUT , propagation.getTimeOut() );
	}
	
	public void testSerial()
	{
		assertFalse ( propagation.isSerial() );
		propagation = create ( true );
		assertTrue ( propagation.isSerial() );
	}
	
	public void testLineage()
	{
		assertEquals ( lineage , propagation.getLineage() );
	}
	
	public void testAdaptPropagation()
	{
		TestRecoveryCoordinator rc = new TestRecoveryCoordinator();
		Propagation p = PropagationImp.adaptPropagation ( propagation , rc );
		assertEquals ( rc , (( CompositeTransactionAdaptor ) p.getLineage().peek()).getRecoveryCoordinator() );
	}

}
