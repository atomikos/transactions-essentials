package com.atomikos.diagnostics;

import java.io.IOException;

import junit.framework.TestCase;

public class CascadedConsoleTestJUnit extends TestCase {

	private CascadedConsole console;
	private TestConsole tc1,tc2;
	
	protected void setUp() throws Exception {
		super.setUp();
		tc1 = new TestConsole();
		tc2 = new TestConsole();
		console = new CascadedConsole ( tc1 ,tc2);
	}
	
	public void testLevel() {
		console.setLevel ( 3 );
		assertEquals ( 3 , console.getLevel() );
		assertEquals ( 3 , tc1.getLevel() );
		assertEquals ( 3 , tc2.getLevel() );
	}
	
	public void testClose() throws IOException {
		assertFalse ( tc1.isClosed() );
		assertFalse ( tc2.isClosed() );
		console.close();
		assertTrue ( tc1.isClosed() );
		assertTrue ( tc2.isClosed() );
	}

	public void testPrint() throws IOException {
		String msg = "test1";
		console.print( msg );
		assertEquals ( msg , tc1.getLastString() );
		assertEquals ( msg , tc2.getLastString() );
		msg = "test2";
		console.println ( msg );
		assertEquals ( msg , tc1.getLastString() );
		assertEquals ( msg , tc2.getLastString() );
		msg = "test3";
		console.print ( msg , 1 );
		assertEquals ( msg , tc1.getLastString() );
		assertEquals ( msg , tc2.getLastString() );
		msg = "test4";
		console.println ( msg , 1 );
		assertEquals ( msg , tc1.getLastString() );
		assertEquals ( msg , tc2.getLastString() );
	}
	
}
