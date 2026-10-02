/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.fs.RecoveryLogImp;

public class ForeignDomainImportGuardTestJUnit {

    private static final String LOCAL_DOMAIN = "localDomain";
    private static final String FOREIGN_DOMAIN = "foreignDomain";

    @Test
    public void testSameDomainNeverWarnsAndNeverConsultsRecoveryLog() {
        RecoveryLog recoveryLog = Mockito.mock(RecoveryLog.class);
        boolean shouldWarn = ForeignDomainImportGuard.shouldWarnAboutUnrecognizedDomain(recoveryLog, LOCAL_DOMAIN, LOCAL_DOMAIN);
        assertFalse(shouldWarn);
        Mockito.verifyNoInteractions(recoveryLog);
    }

    @Test
    public void testDifferentDomainWithDefaultRecoveryNeverWarns() {
        RecoveryLog recoveryLog = new RecoveryLogImp();
        boolean shouldWarn = ForeignDomainImportGuard.shouldWarnAboutUnrecognizedDomain(recoveryLog, LOCAL_DOMAIN, FOREIGN_DOMAIN);
        assertFalse(shouldWarn);
    }

    @Test
    public void testDifferentDomainWithNonDefaultRecoveryAndRecognizedDomainNeverWarns() {
        RecoveryLog recoveryLog = Mockito.mock(RecoveryLog.class);
        Mockito.when(recoveryLog.acceptsDomain(FOREIGN_DOMAIN)).thenReturn(true);
        boolean shouldWarn = ForeignDomainImportGuard.shouldWarnAboutUnrecognizedDomain(recoveryLog, LOCAL_DOMAIN, FOREIGN_DOMAIN);
        assertFalse(shouldWarn);
    }

    @Test
    public void testDifferentDomainWithNonDefaultRecoveryAndUnrecognizedDomainWarns() {
        RecoveryLog recoveryLog = Mockito.mock(RecoveryLog.class);
        Mockito.when(recoveryLog.acceptsDomain(FOREIGN_DOMAIN)).thenReturn(false);
        boolean shouldWarn = ForeignDomainImportGuard.shouldWarnAboutUnrecognizedDomain(recoveryLog, LOCAL_DOMAIN, FOREIGN_DOMAIN);
        assertTrue(shouldWarn);
    }

}
