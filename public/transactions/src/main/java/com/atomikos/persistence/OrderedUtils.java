package com.atomikos.persistence;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrderedUtils {

	public static <T extends Ordered> T firstByOrder(List<T> candidates) {
		
		if (candidates == null || candidates.isEmpty())
			return null;
		
		return Collections.min(candidates, new OrderedObjectComparator());
	}

	private static class OrderedObjectComparator implements Comparator<Ordered> {
		public int compare(Ordered o1, Ordered o2) {
			return o1.getOrder() - o2.getOrder();
		}
	}

}
