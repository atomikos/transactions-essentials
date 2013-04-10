package com.atomikos.persistence.dataserializable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.RandomAccessFile;
import java.io.StreamCorruptedException;
import java.io.SyncFailedException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import com.atomikos.icatch.DataSerializable;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.LogStream;
import com.atomikos.persistence.Recoverable;

public class SingleThreadedLogStream implements LogStream {


	private static final Logger LOGGER = LoggerFactory.createLogger(SingleThreadedLogStream.class);

	private BlockingQueue<BatchedSyncRequest> queue = new LinkedBlockingQueue<BatchedSyncRequest>();

	private ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private RandomAccessFile output_;
	// keeps track of the latest output stream returned
	// from writeCheckpoint, so that it can be closed ( invalidated )
	// if necessary.


	private boolean corrupt_;
	// true if checkpoint; second call of recover
	// not allowed, otherwise suffix_ will be wrong
	// especially since checkpoint failed.

	private VersionedFile file_;

	public SingleThreadedLogStream(String baseDir, String baseName) throws IOException {
		file_ = new VersionedFile(baseDir, baseName, ".log");

		corrupt_ = false;
	}

	private void closeOutput() throws LogException {

		// try to close the previous output stream, if any.
		try {
			if (file_ != null) {
				file_.close();
				if (LOGGER.isInfoEnabled()) {
					LOGGER.logInfo("Logfile closed: " + file_.getCurrentVersionFileName());
				}
			}
			output_ = null;
			// ooutput_ = null;
		} catch (IOException e) {

			throw new LogException("Error closing previous output", e);
		}
	}

	public Vector<Recoverable> recover() throws LogException {

		if (corrupt_)
			throw new LogException("Instance might be corrupted");

		Vector<Recoverable> ret = new Vector<Recoverable>();

		try {
			RandomAccessFile f = file_.openLastValidVersionForReading();

			int count = 0;
			if (LOGGER.isInfoEnabled()) {
				LOGGER.logInfo("Starting read of logfile " + file_.getCurrentVersionFileName());
			}

			while (f.length() > f.getChannel().position()) {
				// if crashed, then unproper closing might cause endless blocking!
				// therefore, we check if avaible first.
				count++;

				SystemLogImage systemLogImage = new SystemLogImage();

				systemLogImage.readData(f);
				ret.addElement(systemLogImage);
				if (count % 10 == 0) {
					LOGGER.logInfo(".");
				}

			}
			LOGGER.logInfo("Done read of logfile");

		} catch (java.io.EOFException unexpectedEOF) {
			LOGGER.logDebug("Unexpected EOF - logfile not closed properly last time?", unexpectedEOF);
			// merely return what was read so far...
		} catch (StreamCorruptedException unexpectedEOF) {
			LOGGER.logDebug("Unexpected EOF - logfile not closed properly last time?", unexpectedEOF);
			// merely return what was read so far...
		} catch (ObjectStreamException unexpectedEOF) {
			LOGGER.logDebug("Unexpected EOF - logfile not closed properly last time?", unexpectedEOF);
			// merely return what was read so far...
		} catch (FileNotFoundException firstStart) {
			// the file could not be opened for reading;
			// merely return the default empty vector
		} catch (Exception e) {
			String msg = "Error in recover";
			LOGGER.logWarning(msg, e);
			throw new LogException(msg, e);
		}

		return ret;
	}

	public void writeCheckpoint(Enumeration elements) throws LogException {

		synchronized (file_) {
			// first, make sure that any pending output stream handles
			// in the client are invalidated
			closeOutput();

			try {
				// open the new output file
				// NOTE: after restart, any previous and failed checkpoint files
				// will be overwritten here. That is perfectly OK.
				output_ = file_.openNewVersionForWriting();

				while (elements != null && elements.hasMoreElements()) {
					DataSerializable next = (DataSerializable) elements.nextElement();
					DataByteArrayOutputStream dataByteArrayOutputStream = new DataByteArrayOutputStream();
					next.writeData(dataByteArrayOutputStream);

					output_.write(dataByteArrayOutputStream.getContent());
				}

				output_.getFD().sync();
				// NOTE: we do NOT close the object output, since the client
				// will probably want to write more!
				// Thus, we return the open stream to the client.
				// Any closing will be done later, during cleanup if necessary.

				try {
					file_.discardBackupVersion();
				} catch (IOException errorOnDelete) {
					corrupt_ = true;
					// should restart
					throw new LogException("Old file could not be deleted");
				}
			} catch (Exception e) {
				throw new LogException("Error during checkpointing", e);
			}

		}

	}

	public void flushObject(Object o, boolean shouldSync) throws LogException {

		// long start = System.currentTimeMillis();

		try {
			final DataByteArrayOutputStream dataByteArrayOutputStream = new DataByteArrayOutputStream();
			DataSerializable oo = (DataSerializable) o;
			oo.writeData(dataByteArrayOutputStream);
			dataByteArrayOutputStream.close();
			// take care of checkpoint...
			byte[] content = dataByteArrayOutputStream.getContent();
			writeBytes(content);
			if (shouldSync) scheduleSyncAndWait();
		} catch (Exception e) {
			throw new LogException(e.getMessage(), e);
		}
		// System.err.println("flushObject: " +(System.currentTimeMillis()-start));
	}

	private void writeBytes(byte[] content) throws IOException {
		synchronized (file_) {
			output_.write(content);
		}
	}


	public synchronized void close() throws LogException {
		executor.shutdown();
		closeOutput();
	}

	public void finalize() throws Throwable {
		try {
			close();
		} finally {
			super.finalize();
		}
	}

	public long getSize() throws LogException {
		return file_.getSize();
	}

	private void scheduleSyncAndWait() throws InterruptedException, ExecutionException {
		batchSyncRequest();
		Future<Void> syncResult = executor.submit(new Callable<Void>() {
			public Void call() throws Exception {
				satisfyAllScheduledSyncRequestsWithOneSyncCall();
				return null;
			}

		});
		syncResult.get();
	}

	private void batchSyncRequest() {
		synchronized (file_) {
			queue.add(new BatchedSyncRequest());
		}
	}

	private void satisfyAllScheduledSyncRequestsWithOneSyncCall()
			throws InterruptedException, IOException, SyncFailedException {
		synchronized (file_) {				
			if (!queue.isEmpty()) {
				output_.getFD().sync();
				queue.clear();
			}
		}
	}
	
	private static class BatchedSyncRequest 
	{
	}
}
