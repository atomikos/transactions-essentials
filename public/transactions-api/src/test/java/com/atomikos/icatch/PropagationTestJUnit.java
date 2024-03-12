/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;
import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PropagationTestJUnit {
    
    private static final String PARENT_ID = "parent";
    private static final String ROOT_ID = "root";
    private static final long TIMEOUT = 100l;
    private static final boolean SERIAL = true;
    private static final String DOMAIN = "domain";
    private static final String PROPERTY_KEY ="key";
    private static final String PROPERTY_VALUE ="value";
    private static final String COORDINATOR_ID = "coordinator";
    
    private final String EXPECTED_TO_STRING = 
            "version=2019,domain="+DOMAIN+",timeout="+TIMEOUT+",serial="+SERIAL+",recoveryCoordinatorURI="+COORDINATOR_ID+
            ",parent="+ROOT_ID+
            ",parent="+PARENT_ID+","+"property."+
            PROPERTY_KEY+"="+PROPERTY_VALUE;
    
    private Propagation propagation;
    private CompositeTransaction parent;
    private CompositeTransaction root;

    @Before
    public void setUp() throws Exception {
        parent = Mockito.mock(CompositeTransaction.class);
        Mockito.when(parent.getTid()).thenReturn(PARENT_ID);
        CompositeCoordinator coordinator = Mockito.mock(CompositeCoordinator.class);
        Mockito.when(coordinator.getCoordinatorId()).thenReturn(COORDINATOR_ID);
        Mockito.when(parent.getCompositeCoordinator()).thenReturn(coordinator);
        Properties p = new Properties();
        p.setProperty(PROPERTY_KEY, PROPERTY_VALUE);
        Mockito.when(parent.getProperties()).thenReturn(p);
        root = Mockito.mock(CompositeTransaction.class);
        Mockito.when(root.getTid()).thenReturn(ROOT_ID);
        Mockito.when(root.getProperties()).thenReturn(new Properties());
        propagation = new Propagation(DOMAIN, root, parent, SERIAL, TIMEOUT);
    }

    @Test
    public void testToString() {    
       assertEquals(EXPECTED_TO_STRING, propagation.toString());
    }

}
