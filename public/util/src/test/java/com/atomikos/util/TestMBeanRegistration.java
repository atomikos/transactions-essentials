package com.atomikos.util;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class TestMBeanRegistration extends DefaultMBeanRegistration implements TestMBeanRegistrationMBean {
	 
		public static ObjectName instanceSpecificObjectName;
		
		static {
			try {
				instanceSpecificObjectName = new ObjectName("name:name=instance");
			} catch (MalformedObjectNameException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected ObjectName createObjectName() {
			return instanceSpecificObjectName;
		}

		@Override
		protected void doInit() {
			
		}
	}
