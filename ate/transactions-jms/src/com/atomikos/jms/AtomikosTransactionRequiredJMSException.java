package com.atomikos.jms;

import com.atomikos.icatch.system.Configuration;


public class AtomikosTransactionRequiredJMSException extends
		AtomikosJMSException {

	public static void throwAtomikosTransactionRequiredJMSException ( String reason )
	throws AtomikosTransactionRequiredJMSException
	{
		Configuration.logWarning ( reason );
		throw new AtomikosTransactionRequiredJMSException ( reason );
	}
	
	AtomikosTransactionRequiredJMSException(String reason) {
		super(reason);
	}

}
