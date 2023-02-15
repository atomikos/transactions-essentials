/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.internal;

import javax.jms.Session;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.util.DynamicProxySupport;

public abstract class AbstractJmsSessionProxy extends DynamicProxySupport<Session> {

    public AbstractJmsSessionProxy(Session delegate) {
        super(delegate);
    }

    protected abstract boolean isAvailable();

    protected abstract boolean isErroneous();

    protected abstract boolean isInTransaction(CompositeTransaction ct);

    protected boolean isInactiveTransaction(CompositeTransaction ct) {
        // default to false: be pessimistic and disallow reuse if not sure
        return false;
    }

    public abstract void recycle();
}
