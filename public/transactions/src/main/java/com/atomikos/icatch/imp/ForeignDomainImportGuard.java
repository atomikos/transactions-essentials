/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.fs.RecoveryLogImp;

/**
 * Decides whether an imported transaction from a foreign recovery domain
 * deserves a warning that its recovery (if ever needed) will depend on the
 * exporting service being reachable, rather than on a shared recovery log.
 */
class ForeignDomainImportGuard {

    // cf case 228139
    static boolean shouldWarnAboutUnrecognizedDomain(RecoveryLog recoveryLog, String tmUniqueName, String recoveryDomainName) {
        boolean isForeignDomain = !tmUniqueName.equals(recoveryDomainName);
        boolean usesDefaultRecovery = recoveryLog instanceof RecoveryLogImp;
        return isForeignDomain && !usesDefaultRecovery && !recoveryLog.acceptsDomain(recoveryDomainName);
    }

    private ForeignDomainImportGuard() {
    }

}
