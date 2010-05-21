package com.atomikos.jdbc.nonxa;

import java.sql.SQLException;

interface JtaAwareNonXaConnection 
{

	void transactionTerminated ( boolean committed ) throws SQLException;
	
}
