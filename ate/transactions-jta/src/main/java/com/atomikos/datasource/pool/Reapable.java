package com.atomikos.datasource.pool;

public interface Reapable {
	
	void reap();
	
	void close();

}
