/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.internal;

import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.Session;
import javax.jms.XAConnection;
import javax.jms.XASession;
import javax.transaction.xa.XAResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.datasource.xa.session.SessionHandleStateChangeListener;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.jms.SessionCreationMode;

/**
 * Verifies the feature-flag gate (cf case 226870) around the legacy
 * Oracle-AQ {@code forceConnectionIntoXaMode} workaround (cf issue 10095).
 *
 * <p>The workaround opens a transacted {@link Session} on the underlying
 * connection and rolls it back, purely to coax Oracle AQ into XA mode before
 * the first {@code createXASession()}. For every other (spec-compliant)
 * provider the extra session is wasted churn, so the workaround is now
 * <strong>opt-in</strong>: it is skipped by default and only applied when the
 * feature flag {@code com.atomikos.icatch.feature.226870} is enabled.
 *
 * <p>The observable, provider-independent difference is therefore whether the
 * delegate's plain {@code createSession(...)} is invoked before the XA session
 * is created. We assert exactly that, mocking the vendor JMS driver
 * ({@link XAConnection}) at the architectural boundary (per ADR-031).
 */
public class AtomikosJmsConnectionProxyForceXaModeTestJUnit {

    private static final String FEATURE_FLAG = "com.atomikos.icatch.feature.226870";

    private XAConnection delegate;
    private XATransactionalResource resource;
    private SessionHandleStateChangeListener owner;
    private ConnectionPoolProperties props;

    @Before
    public void setUp() throws Exception {
        delegate = mock(XAConnection.class);

        XASession xaSession = mock(XASession.class);
        when(xaSession.getXAResource()).thenReturn(mock(XAResource.class));
        when(delegate.createXASession()).thenReturn(xaSession);

        // the Oracle-AQ workaround creates a transacted session and rolls it back
        when(delegate.createSession(true, Session.AUTO_ACKNOWLEDGE)).thenReturn(mock(Session.class));

        resource = mock(XATransactionalResource.class);
        owner = mock(SessionHandleStateChangeListener.class);
        props = mock(ConnectionPoolProperties.class);
        // PRE_6_0 + localTransactionMode==false => createXaSession() is true
        // without needing an active JTA transaction.
        when(props.getLocalTransactionMode()).thenReturn(false);

        setFeatureFlag(false);
    }

    @After
    public void tearDown() {
        // do not leak the flag into other tests sharing this JVM's Configuration
        setFeatureFlag(false);
    }

    private static void setFeatureFlag(boolean enabled) {
        Configuration.getConfigProperties().setProperty(FEATURE_FLAG, Boolean.toString(enabled));
    }

    private AtomikosJmsConnectionProxy newProxy() {
        return new AtomikosJmsConnectionProxy(delegate, SessionCreationMode.PRE_6_0, resource, owner, props);
    }

    @Test
    public void testForceXaModeIsNotAppliedByDefault() throws Exception {
        AtomikosJmsConnectionProxy proxy = newProxy();

        proxy.createSession(true, Session.AUTO_ACKNOWLEDGE);

        // workaround skipped by default: no plain session is created, only the XA session
        verify(delegate, never()).createSession(anyBoolean(), anyInt());
        verify(delegate).createXASession();
    }

    @Test
    public void testForceXaModeIsAppliedWhenFeatureFlagEnabled() throws Exception {
        setFeatureFlag(true);
        AtomikosJmsConnectionProxy proxy = newProxy();

        proxy.createSession(true, Session.AUTO_ACKNOWLEDGE);

        // workaround applied: a transacted session is created (and rolled back) before the XA session
        verify(delegate).createSession(true, Session.AUTO_ACKNOWLEDGE);
        verify(delegate).createXASession();
    }
}
