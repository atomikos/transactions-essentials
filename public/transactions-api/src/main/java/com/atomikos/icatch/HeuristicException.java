package com.atomikos.icatch;

 /**
  * Common superclass for heuristics.
  */

public class HeuristicException extends Exception {

	private static final long serialVersionUID = 1L;

	public HeuristicException(String message) {
		super(message);
	}
	
}
