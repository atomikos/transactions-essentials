package com.atomikos.jdbc;

import java.beans.PropertyDescriptor;

import junit.framework.TestCase;

public class DataSourceBeanBeanInfoTestJUnit extends TestCase {

	private DataSourceBeanBeanInfo info;
	
	public DataSourceBeanBeanInfoTestJUnit(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		info = new DataSourceBeanBeanInfo();
	}
	
	public void testBug21542() {
		PropertyDescriptor[] props =
			info.getPropertyDescriptors();
		assertNotNull ( props );
		assertTrue ( props.length > 0 );
		for ( int i = 0 ; i < props.length ; i ++ ) {
			assertNotNull ( props[i] );
		}
	}

}
