/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.datasource.xa;

import java.util.UUID;

import junit.framework.Assert;

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
