package com.atomikos.persistence.dataserializable;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.atomikos.icatch.TxState;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.LogStream;
import com.atomikos.persistence.ObjectLog;
import com.atomikos.persistence.Recoverable;
import com.atomikos.persistence.StateRecoverable;

public class SingleThreadedObjectLog implements ObjectLog {

	private final StreamObjectLog streamObjectLog;
	private final BlockingQueue<Recoverable> queue = new LinkedBlockingQueue<Recoverable>();
	MyWorker worker ;
	public SingleThreadedObjectLog(LogStream logstream, long maxFlushesBetweenCheckpoints) {
		streamObjectLog = new StreamObjectLog(logstream, maxFlushesBetweenCheckpoints);
		 worker = new MyWorker();
		worker.start();
		
	}

	public void flush(Recoverable recoverable) throws LogException {
			queue.add(recoverable);			
	}

	public void init() throws LogException {
		streamObjectLog.init();

	}

	public Vector<StateRecoverable<TxState>> recover() throws LogException {
		
		return streamObjectLog.recover();
	}

	public Recoverable recover(Object id) throws LogException {

		return streamObjectLog.recover(id);
	}

	public void delete(Object id) throws LogException {
		streamObjectLog.delete(id);

	}

	public void close() throws LogException {
		System.err.println(queue.size());
		streamObjectLog.close();

	}

	private  class MyWorker extends Thread {
		public void run() {
			try {
				while (true) {
					try {
						Recoverable recoverable = queue.take();
						streamObjectLog.flush(recoverable);
					} catch (LogException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (InterruptedException ie) {
				// just terminate
			}
		}

	}

}
