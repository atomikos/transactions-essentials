package com.atomikos.util;

public abstract class Assert {

	
	public static void notNull(String message, Object o) {
		if (o == null) {
			throw new IllegalArgumentException(message);
		}
	}
}
