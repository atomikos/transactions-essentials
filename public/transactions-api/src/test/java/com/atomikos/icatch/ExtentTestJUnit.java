/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ExtentTestJUnit {
    
    private Extent extent;
    private Participant p1;
    private Map<String, Integer> map;

    @Before
    public void setUp() throws Exception {
        p1 = Mockito.mock(Participant.class);
        Mockito.when(p1.getURI()).thenReturn("p1");
        extent = new Extent();
        map = new HashMap<String,Integer>();
        map.put("p2", 2);
        
    }

    @Test
    public void testToStringWithParticipants() {
       extent.add(p1, 1);
       extent.addRemoteParticipants(map);
       final String expected="version=2019,uri=p1,responseCount=1,direct=true,uri=p2,responseCount=2,direct=false";
       assertEquals(expected,extent.toString());
    }
    
    @Test
    public void testToStringWithOneDirectParticipant() {
        extent.add(p1, 1);
        final String expected="version=2019,uri=p1,responseCount=1,direct=true";
        assertEquals(expected,extent.toString());
    }
    
    @Test
    public void testToStringWithOneRemoteParticipant() {
        extent.addRemoteParticipants(map);
        final String expected="version=2019,uri=p2,responseCount=2,direct=false";
        assertEquals(expected,extent.toString());
    }
    
    @Test
    public void testToStringWithParentTransactionId() {
        extent = new Extent("parentId");
        final String expected = "version=2019,parent=parentId";
        assertEquals(expected, extent.toString());
    }

}
