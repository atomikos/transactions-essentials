/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.xa;

/**
 * The outcome recorded for a coordinator by a {@link RecoveryDecisionStore}.
 */
public enum Decision {
	COMMIT, ABORT
}
