/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atomikos.icatch.Extent;
import com.atomikos.icatch.Propagation;

public class ParserTestJUnit {
    
    private static final String PROPAGATION_OF_ROOT_WITHOUT_PROPERTIES =
            "version=2019,domain=DOMAIN,timeout=1234,serial=true,recoveryCoordinatorURI=PARENT,parent=ROOT";
    
	
	private static final String PROPAGATION_WITHOUT_PROPERTIES =
			"version=2019,domain=DOMAIN,timeout=1234,serial=true,recoveryCoordinatorURI=PARENT,parent=ROOT,parent=PARENT";
	
	private static final String PROPAGATION_WITH_FUTURE_EXTRA_LINEAGE =
            "version=2019,domain=DOMAIN,timeout=1234,serial=true,recoveryCoordinatorURI=PARENT,parent=ROOT,parent=FUTURE_PARENT,parent=PARENT";
	
	private static final String PROPAGATION_WITH_INCOMPATIBLE_VERSION =
            "version=2020,domain=DOMAIN,timeout=1234,serial=true,recoveryCoordinatorURI=PARENT,parent=ROOT,parent=PARENT";
	
	private static final String PROPAGATION_WITH_FUTURE_EXTRA_PROPERTIES =
            "version=2019,domain=DOMAIN,timeout=1234,serial=true,recoveryCoordinatorURI=PARENT,extra=bla,extra2=blabla,parent=ROOT,parent=PARENT,property.key=value,property.key=bla";
	
	private static final String PROPAGATION_WITH_PROPERTIES =
			"version=2019,domain=DOMAIN,timeout=1234,serial=true,recoveryCoordinatorURI=PARENT,parent=ROOT,parent=PARENT,property.key=value";
	
	private static final String EXTENT = 
			"version=2019,parent=PARENT,uri=URI_1,responseCount=1,direct=true,uri=URI_2,responseCount=2,direct=false";
	
	private static final String EXTENT_WITH_FUTURE_EXTRA_PROPERTIES = 
            "version=2019,parent=PARENT,extra=bla,extra2=blabla,uri=URI_1,responseCount=1,direct=true,extra=bla,uri=URI_2,responseCount=2,direct=false";
	
	private static final String EXTENT_INCOMPLETE ="version=2019,parent=PARENT,uri=URI_1,responseCount=1";

	private static final String EXTENT_WITH_INCOMPATIBLE_VERSION = 
            "version=2020,parent=PARENT,uri=URI_1,responseCount=1,direct=true,uri=URI_2,responseCount=2,direct=false";
	
	private static final String PROPAGATION_FROM_EXAMPLES = "version=2019,domain=client,timeout=9750,serial=true,recoveryCoordinatorURI=client155888493530000001,parent=client155888493530000001,property.com.atomikos.icatch.jta.transaction=true";
	
	private Parser parser;

	@Before
	public void setUp() throws Exception {
		parser = new Parser();
	}

	@Test
	public void testParsePropagationWithoutProperties() {
		Propagation p = parser.parsePropagation(PROPAGATION_WITHOUT_PROPERTIES);
		assertEquals(PROPAGATION_WITHOUT_PROPERTIES, p.toString());
	}
	
	@Test
	public void testParsePropagationWithProperties() {
		Propagation p = parser.parsePropagation(PROPAGATION_WITH_PROPERTIES);
		assertEquals(PROPAGATION_WITH_PROPERTIES, p.toString());
	}
	
	@Test
	public void testParseExtent() {
		Extent e = parser.parseExtent(EXTENT);
		assertEquals(EXTENT, e.toString());
	}
	
	@Test
    public void testParsePropagationWithFutureExtraProperties() {
        Propagation p = parser.parsePropagation(PROPAGATION_WITH_FUTURE_EXTRA_PROPERTIES);
        assertEquals(PROPAGATION_WITH_PROPERTIES, p.toString());
    }
	
	@Test
    public void testParseExtentWithFutureExtraProperties() {
        Extent e = parser.parseExtent(EXTENT_WITH_FUTURE_EXTRA_PROPERTIES);
        assertEquals(EXTENT, e.toString());
    }
	
	@Test(expected=IllegalArgumentException.class)
	public void testParseIncompleteExtent() {
	    Extent e = parser.parseExtent(EXTENT_INCOMPLETE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParsePropagationWithIncompatibleVersionThrows() {
	    parser.parsePropagation(PROPAGATION_WITH_INCOMPATIBLE_VERSION);
	}
	
	@Test(expected=IllegalArgumentException.class)
    public void testParseExtentWithIncompatibleVersionThrows() {
        parser.parsePropagation(EXTENT_WITH_INCOMPATIBLE_VERSION);
    }
	
	@Test
	public void testParsePropagationForRoot() {
	    Propagation p = parser.parsePropagation(PROPAGATION_OF_ROOT_WITHOUT_PROPERTIES);
	    assertEquals(PROPAGATION_OF_ROOT_WITHOUT_PROPERTIES, p.toString());
	}
	
	@Test
	public void testParsePropagationWithFutureExtraAncestors() {
	    Propagation p = parser.parsePropagation(PROPAGATION_WITH_FUTURE_EXTRA_LINEAGE);
	    assertEquals(PROPAGATION_WITHOUT_PROPERTIES, p.toString());
	}
	
	@Test
	public void testParsePropagationFromExamples() {
	    Propagation p = parser.parsePropagation(PROPAGATION_FROM_EXAMPLES);
	}
}
