/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
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
		
		timer.stopTimer();
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
