/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.thread;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

// cf case 230654: moved here from com.atomikos.icatch.imp (public/transactions)
// so it can be reused outside that module without reaching into kernel
// internals - the class itself carries no transaction semantics, only a
// generic bounded condition-wait (AGENTS.md core-surface note: this is a
// relocation + visibility widening, no logic change).
public class ConditionalWaiter {

    private static final Logger LOGGER = LoggerFactory.createLogger(ConditionalWaiter.class);

    private long maxWaitTime;

    public ConditionalWaiter(long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    /**
     * Waits while the condition evaluates to true, or until maxWaitTime has passed.
     * @param condition
     * @return True if timed out.
     */
    public boolean waitWhile(Condition condition) {
        long accumulatedWaitTime = 0;
        int waitTime = 1000;
        boolean evaluation = condition.evaluate();
        while (evaluation && (accumulatedWaitTime < maxWaitTime)) {
            LOGGER.logInfo("Waiting for condition to become true...");
            synchronized(this) {
                try {
                    this.wait(waitTime);
                } catch (InterruptedException ex) {
                    InterruptedExceptionHelper.handleInterruptedException ( ex );
                    // ignore
                    if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": interrupted during wait" , ex );
                }
            }
            accumulatedWaitTime = accumulatedWaitTime + waitTime;
            evaluation = condition.evaluate();
        }
        return evaluation;
    }

    @FunctionalInterface
    public static interface Condition {
        boolean evaluate();
    }
}
