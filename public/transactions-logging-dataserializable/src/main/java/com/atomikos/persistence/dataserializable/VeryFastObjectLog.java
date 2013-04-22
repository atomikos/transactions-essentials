package com.atomikos.persistence.dataserializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.atomikos.icatch.TxState;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.Recoverable;
import com.atomikos.persistence.StateRecoverable;

 /**
  * A high-performance logging implementation - works by batching multiple
  * concurrent threads' sync requests together into one sync towards the 
  * underlying ObjectLog delegate.
  */

public class VeryFastObjectLog extends AbstractObjectLog {

	private AbstractObjectLog delegate;

	private ExecutorService executor=Executors.newSingleThreadExecutor();
	
	private List<SystemLogImage> flushQueue = new ArrayList<SystemLogImage>();
	
	public VeryFastObjectLog(AbstractObjectLog delegate) {
		this.delegate = delegate;
	}

    public  void flush ( Recoverable rec ) throws LogException
    {
        if ( rec == null ) return;
        SystemLogImage simg = new SystemLogImage ( rec, false );
        flush ( simg , true );	
        
    }

	public void init() throws LogException {
		delegate.init();

	}

	public Vector<StateRecoverable<TxState>> recover() throws LogException {

		return delegate.recover();
	}

	public Recoverable recover(Object id) throws LogException {
		return delegate.recover(id);
	}

	public void delete(Object id) throws LogException {
		delegate.delete(id);

	}

	public void close() throws LogException {
		delegate.close();

	}

	@Override
	protected void flush(SystemLogImage img, boolean shouldSync) throws LogException {
		if (shouldSync) {
			try {
				queueFlushRequest(img);
			} catch (Exception e) {
				throw new LogException(e);
			} 
		} else {
			delegate.flush(img, shouldSync);
		}
	}

	private void queueFlushRequest(SystemLogImage img) throws InterruptedException, ExecutionException {
		addToFlushQueue(img);
		Future<Void> future= executor.submit(new Callable<Void>() {
			public Void call() throws Exception {
				processFlushQueue();
				
				return null;
			}
		});
		future.get();
	}

	private synchronized void addToFlushQueue(SystemLogImage img) {
		flushQueue.add(img);
	}

	private synchronized void processFlushQueue() throws LogException {
		int size=flushQueue.size();
		int inc=0;		
		for (SystemLogImage systemLogImage : flushQueue) {
			if(inc<size-1){
				delegate.flush(systemLogImage, false);	
			} else {
				delegate.flush(systemLogImage, true);
			}	
			inc++;
		}
		flushQueue.clear();
	}

}
