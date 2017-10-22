/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

import java.util.UUID;

import org.junit.Assert;

import org.junit.Test;

public class StringUtilsTestJUnit {

	private static String byteArrayToHexStringWithNativeJavaImplementation(byte[] byteArray) {
		StringBuffer sb = new StringBuffer(2*byteArray.length);
    	for (int i = 0; i < byteArray.length; i++) {
    		String hexByte = Integer.toHexString(byteArray[i]);
			sb.append(hexByte);
		}
    	return sb.toString().toUpperCase();
	}

	@Test
	public void testByteAreEquals() {      
        for (int i = 0; i < 10; i++) {
        	String str = UUID.randomUUID().toString();
        	byte[]  bytes= str.getBytes();
        	Assert.assertEquals(byteArrayToHexStringWithNativeJavaImplementation(bytes), StringUtils.byteArrayToHexString(bytes));
        }        
    }
}
