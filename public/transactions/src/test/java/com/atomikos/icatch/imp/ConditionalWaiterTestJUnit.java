/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ConditionalWaiterTestJUnit {
    
    private ConditionalWaiter waiter;
    
    @Before
    public void setUp() {
        waiter = new ConditionalWaiter(1000);
    }

    @Test
    public void testTimeout() {
        boolean timeout = waiter.waitWhile(() -> {return true;});
        assertTrue(timeout);
    }
    
    @Test
    public void testNoTimeout() {
        boolean timeout = waiter.waitWhile(() -> {return false;});
        assertFalse(timeout);
    }
}
