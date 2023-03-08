/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import java.io.Serializable;

import javax.naming.Reference;

import junit.framework.TestCase;

public class SerializableObjectFactoryTestJUnit extends TestCase {
	private static final String NAME = "name";

	private static final int VALUE = 12;

	private static final int TRANSIENT_VALUE = 3;

	private TestBean bean;

	public SerializableObjectFactoryTestJUnit(String name) {
		super(name);
	}

	protected void setUp() {

		bean = new TestBean();
		bean.setName(NAME);
		bean.setValue(VALUE);
		bean.setTransientValue(TRANSIENT_VALUE);
	}

	public void testBasic() throws Exception {
		Reference ref = SerializableObjectFactory.createReference(bean);
		SerializableObjectFactory fact = new SerializableObjectFactory();

		bean = (TestBean) fact.getObjectInstance(ref, null, null, null);
		if (!bean.getName().equals(NAME))
			fail("getName failure");
		if (bean.getValue() != VALUE)
			fail("getValue failure");
		if (bean.getTransientValue() == TRANSIENT_VALUE)
			fail("getTransientValue failure");
	}
	@SuppressWarnings("serial")
	static class TestBean implements Serializable {
		private String name;

		private int value;

		private transient int transientValue;

		TestBean() {
			name = "";
			value = 0;
			transientValue = 0;
		}

		/**
		 * @return
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return
		 */
		public int getTransientValue() {
			return transientValue;
		}

		/**
		 * @return
		 */
		public int getValue() {
			return value;
		}

		/**
		 * @param string
		 */
		public void setName(String string) {
			name = string;
		}

		/**
		 * @param i
		 */
		public void setTransientValue(int i) {
			transientValue = i;
		}

		/**
		 * @param i
		 */
		public void setValue(int i) {
			value = i;
		}

	}
}
