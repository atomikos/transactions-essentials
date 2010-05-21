package com.atomikos.icatch.admin.jmx;

import com.atomikos.icatch.admin.TestAdminTransaction;

public class JmxDefaultTransactionTestJUnit extends
		AbstractJUnitJmxTransactionTest {

	
	protected JmxTransaction onSetup(TestAdminTransaction testAdminTransaction, TestMBeanServer server) {
		return new JmxDefaultTransaction ( testAdminTransaction );
	}

}
