/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.fs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.atomikos.icatch.config.Configuration;

public class RecoveryLogImpTestJUnit {

    @Test
    public void testAcceptsDomainReturnsTrueForOwnDomain() {
        RecoveryLogImp sut = new RecoveryLogImp();
        String ownDomain = Configuration.getConfigProperties().getTmUniqueName();
        assertTrue(sut.acceptsDomain(ownDomain));
    }

    @Test
    public void testAcceptsDomainReturnsFalseForForeignDomain() {
        RecoveryLogImp sut = new RecoveryLogImp();
        assertFalse(sut.acceptsDomain("someForeignDomain"));
    }

}
