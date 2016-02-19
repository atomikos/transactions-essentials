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

package com.atomikos.timing;

import junit.framework.TestCase;

public class PooledAlarmTimerTestJUnit extends TestCase {

	private int count1;
	private int count2;
	
	

	protected void setUp() throws Exception {
		count1 = 0;
		count2 = 0;
	}
//
//	public void testSingleAlarmListener() throws Exception {
//		PooledAlarmTimer timer = new PooledAlarmTimer(100);
//		timer.addAlarmTimerListener(new AlarmTimerListener() {
//			public void alarm(AlarmTimer timer) {
//				count1 ++;
//			}
//		});
//		
//		Thread thread = new Thread(timer);
//		thread.start();
//		
//		Thread.sleep(1010);
//		assertEquals(10, count1);
//		
//		Thread.sleep(1010);
//		assertEquals(20, count1);
//		
//		timer.stop();
//		Thread.sleep(500);
//		assertFalse(thread.isAlive());
//		
//		Thread.sleep(1010);
//		assertEquals(20, count1);
//	}

	public void testMultiAlarmListener() throws Exception {
		PooledAlarmTimer timer = new PooledAlarmTimer(100);
		
		timer.addAlarmTimerListener(new AlarmTimerListener() {
			public void alarm(AlarmTimer timer) {
				count1 ++;
			}
		});
		timer.addAlarmTimerListener(new AlarmTimerListener() {
			public void alarm(AlarmTimer timer) {
				count2 ++;
				if (count2 >= 10)
					timer.removeAlarmTimerListener(this);
			}
		});
		
		Thread thread = new Thread(timer);
		thread.start();
		
		Thread.sleep(1050);
		assertEquals(10, count1);
		assertEquals(10, count2);
		
		Thread.sleep(1050);
		assertEquals(20, count1);
		assertEquals(10, count2);
		
		timer.stop();
		Thread.sleep(500);
		assertFalse(thread.isAlive());
		
		timer.addAlarmTimerListener(new AlarmTimerListener() {
			public void alarm(AlarmTimer timer) {
				count2 ++;
			}
		});
		
		Thread.sleep(1050);
		assertEquals(20, count1);
		assertEquals(10, count2);
	}

}
