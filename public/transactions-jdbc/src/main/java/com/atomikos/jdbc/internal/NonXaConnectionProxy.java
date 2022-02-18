/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.internal;

import java.sql.SQLException;

interface NonXaConnectionProxy 
{

	void transactionTerminated ( boolean committed ) throws SQLException;
	
	boolean isAvailableForReuseByPool();
	
}
