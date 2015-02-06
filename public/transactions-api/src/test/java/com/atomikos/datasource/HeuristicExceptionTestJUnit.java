package com.atomikos.datasource;

import junit.framework.TestCase;

import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.StringHeuristicMessage;

public class HeuristicExceptionTestJUnit extends TestCase {

	private HeuristicException exception;
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testBasicConstructor() 
	{
		String msg = "bla";
		exception = new HeuristicException ( msg );
		assertEquals ( msg , exception.getMessage() );
	}
	
	public void testConstructorWithHeurMessages() {
		HeuristicMessage[] msgs = new HeuristicMessage[1];
		msgs[0] = new StringHeuristicMessage ( "test" );
		exception = new HeuristicException ( msgs );
		assertEquals ( msgs , exception.getHeuristicMessages() );
	}
	

}
