package com.atomikos.persistence.dataserializable;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.atomikos.persistence.LogException;
import com.atomikos.persistence.LogStream;
import com.atomikos.persistence.Recoverable;

public class SingleThreadedLogStream implements LogStream {

	private final FileLogStream logStream;

	private final ExecutorService service = Executors.newSingleThreadExecutor();

	public SingleThreadedLogStream(String baseDir, String baseName) throws IOException {
		logStream = new FileLogStream(baseDir, baseName);
	}

	public long getSize() throws LogException {

		return logStream.getSize();
	}

	public Vector<Recoverable> recover() throws LogException {

		return logStream.recover();
	}

	public void writeCheckpoint(Enumeration elements) throws LogException {
		logStream.writeCheckpoint(elements);

	}

	public void flushObject(final Object objectToFlush, final boolean shouldSync) throws LogException {

		service.submit(new Callable<Void>() {
			public Void call() throws Exception {
				logStream.flushObject(objectToFlush, shouldSync);
				return null;
			}
		});

	}

	public void close() throws LogException {
		// try {
		// service.awaitTermination(2000, TimeUnit.MILLISECONDS);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		logStream.close();

	}

}
