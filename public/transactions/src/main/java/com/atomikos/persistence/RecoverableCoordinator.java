/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.persistence;

import com.atomikos.finitestates.FSMPreEnterEventSource;
import com.atomikos.icatch.CoordinatorLogEntry;
import com.atomikos.icatch.TxState;

/**
 * A type of stateful objects whose state is guaranteed to be recoverable. The
 * logging is done based on PreEnter events. The guarantee offered is the
 * following: IF a recoverable state is reached by the instance, then its image
 * is GUARANTEED to be recoverable. The inverse does NOT hold: the fact that an
 * object is recovered in some state does NOT mean that the state was reached.
 * Indeed, other PreEnter listeners may still have prevented the transition in
 * the last moment. However, this should not be a real problem; applications
 * should take this into account.
 */

public interface RecoverableCoordinator extends FSMPreEnterEventSource
{
    
    CoordinatorLogEntry getCoordinatorLogEntry(TxState state);
    
}
