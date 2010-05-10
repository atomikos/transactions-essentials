package com.atomikos.diagnostics;

import java.io.IOException;

public class TestConsole implements Console {

	private boolean closed;
	private String lastString;
	private int level;
	
	public String getLastString() {
		return lastString;
	}
	
	public void println(String string) throws IOException {
		lastString = string;

	}

	public void print(String string) throws IOException {
		lastString = string;

	}

	public void println(String string, int level) throws IOException {
		lastString = string;

	}

	public void print(String string, int level) throws IOException {
		lastString = string;

	}

	public void close() throws IOException {
		closed = true;

	}
	
	public boolean isClosed() {
		return closed;
	}

	public void setLevel(int level) {
		this.level = level;

	}

	public int getLevel() {
		return this.level;
	}

}
