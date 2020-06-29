/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.thread.InterruptedExceptionHelper;

class ConditionalWaiter {
    
    private static final Logger LOGGER = LoggerFactory.createLogger(ConditionalWaiter.class);
    
    private long maxWaitTime;
    
    ConditionalWaiter(long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }
    
    /**
     * Waits while the condition evaluates to true, or until maxWaitTime has passed.
     * @param condition
     * @return True if timed out.
     */
    boolean waitWhile(Condition condition) {
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
    static interface Condition {
        boolean evaluate();
    }
}

