/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.atomikos.datasource.xa.RecoveryScan.ScanResult;
import com.atomikos.datasource.xa.RecoveryScan.XidSelector;

// cf case 229790
public class RecoveryScanTestJUnit {

	private static final String RESOURCE_NAME = "testResource";

	@Mock private XAResource xaResource;

	private XID mine;
	private XID foreign;
	private XidSelector onlyMine;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mine = new XID("tid1", "mineBranch", RESOURCE_NAME);
		foreign = new XID("tid2", "foreignBranch", RESOURCE_NAME);
		onlyMine = new XidSelector() {
			@Override
			public boolean selects(XID xid) {
				return xid.equals(mine);
			}
		};
		when(xaResource.recover(XAResource.TMSTARTRSCAN)).thenReturn(new Xid[] { mine, foreign });
		when(xaResource.recover(XAResource.TMNOFLAGS)).thenReturn(new Xid[0]);
	}

	@Test
	public void testRecoverXidsWithCountReportsTotalAndSelectedSeparately() throws Exception {
		ScanResult result = RecoveryScan.recoverXidsWithCount(xaResource, onlyMine);

		assertEquals(2, result.totalPrepared);
		assertEquals(1, result.selected.size());
		assertEquals(mine, result.selected.get(0));
	}

	@Test
	public void testRecoverXidsStillReturnsOnlySelected() throws Exception {
		List<XID> result = RecoveryScan.recoverXids(xaResource, onlyMine);

		assertEquals(1, result.size());
		assertEquals(mine, result.get(0));
	}

}
