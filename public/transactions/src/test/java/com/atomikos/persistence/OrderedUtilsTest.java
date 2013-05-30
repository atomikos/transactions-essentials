package com.atomikos.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class OrderedUtilsTest {

	@Test
	public void firstItemByPriority() {
		List<Ordered> candidates= new ArrayList<Ordered>(2);
		
		Ordered o1= new Ordered() {
			public int getOrder() {
				return 1;
			}
		};
		
		Ordered o2= new Ordered() {
			public int getOrder() {
				return 2;
			}
		};
		
		candidates.add(o1);
		candidates.add(o2);
		
		assertEquals(o1,OrderedUtils.firstByOrder(candidates));
	}

	@Test
	public void nullContentMustReturnNull() {
		List<Ordered> candidates=null;
		assertNull(OrderedUtils.firstByOrder(candidates));
	}

	@Test
	public void emptyContentMustReturnNull() {
		List<Ordered> candidates= new ArrayList<Ordered>(2);
		assertNull(OrderedUtils.firstByOrder(candidates));
	}
}
