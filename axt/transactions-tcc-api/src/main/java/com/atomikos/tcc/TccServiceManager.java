package com.atomikos.tcc;


public interface TccServiceManager 
{
	
	/**
	 * Registers a service for recovery. 
	 * Any service that you intend to use should always
	 * first be registered to help with startup recovery.
	 * Preferably this should be done before or during 
	 * startup of the transaction service, or the 
	 * transaction recovery mechanism may be left incomplete.
	 * 
	 * @param service
	 */
	
	public void registerForRecovery ( TccService service );
	
	/**
	 * Deregisters a service for recovery (the inverse of 
	 * registerForRecovery);
	 * this can be used to re-register
	 * later if desirable.
	 * 
	 * @param service
	 */
	
	public void deregisterForRecovery ( TccService service );

	/**
	 * Registers the calling thread for new 
	 * try-confirm-cancel work. The work will be
	 * subject to the current activity's termination; if
	 * no activity exists then a new activity will be 
	 * created. If an activity exists, then the work
	 * will execute in a <b>subactivity</b>, meaning
	 * that its failure will <b>not</b> automatically
	 * lead to cancelation of the existing activity.
	 * 
	 * <p>
	 * <b>IMPORTANT: 
	 * <ul><li>
	 * In well-behaved applications, each
	 * call to register is always followed by a call to either 
	 * completed or failed. This ensures proper garbage
	 * collection in the virtual machine. 
	 * </li>
	 * <li>Also, applications
	 * should not take much longer than the chosen timeout
	 * to streamline internal resource consumption.
	 * Failure to do so will lead to an increase in 
	 * completion errors due to timeout.
	 * </li>
	 * </ul>
	 * </b>
	 * @param service The service implementation for which
	 * registration is done (and that will receive the callbacks
	 * for completion). If null then this method will merely
	 * start a new activity (or subactivity depending on the context).
	 * @param timeout The timeout in milliseconds
	 * before the work should be canceled automatically
	 * by the system (this ensures that 
	 * pending work will eventually be canceled if it 
	 * is never confirmed).
	 * 
	 * @return A ticket to identify the work, 
	 * needed by the various callback methods in
	 * the TCC interfaces. This id can also be 
	 * used as the correlation ID for reliable
	 * messaging calls made during the execution
	 * of the service. This eases the association
	 * of incoming replies with the work in question.
	 */
	
	public String register ( TccService service, long timeout );
	
	/**
	 * Marks previously registered work as
	 * complete. Calling this method signals 
	 * that the work can be confirmed as far
	 * as the service implementation is concerned.
	 * <p>
	 * If there was no pre-existing activity
	 * when the work was registered, then this
	 * method will also confirm the work everwhere (or cancel everywhere, if 
	 * for instance orphans are detected or intermediate timeouts
	 * have occurred).
	 * <p>
	 * If there was a pre-existing activity then 
	 * the work's confirmation will be subject to 
	 * the termination of that pre-existing activity.
	 * <p>
	 * <b>
	 * IMPORTANT: all the persistent results should be
	 * saved BEFORE this method is called. Otherwise,
	 * cancel callbacks may interleave with pending
	 * work, which involves a correctness risk.
	 * </b>
	 * 
	 * This method merely triggers completion and
	 * does not wait for the result; instead,
	 * the final result is obtained through 
	 * one of cancel or confirm in the TccService
	 * instance.
	 * 
	 * @param id Correlation id of the work 
	 * as obtained during register (used to 
	 * check if the work is not timed out). 
	 * 
	 * 
	 * 
	 */
	
	public void completed ( String id );
	
	/**
	 * Signals that the work has failed.
	 * The system will later invoke cancel
	 * with the same id. 
	 * <br>
	 * <b>IMPORTANT NOTE: the failure of the work 
	 * does NOT automatically lead to cancelation of any
	 * PRE-EXISTING activity. It is up to the application
	 * logic to determine what to do next. Failed work 
	 * can safely be retried within the same parent activity.</b>
	 * 
	 * This method merely triggers the cancelation process and 
	 * does not wait for the result (you can use the 
	 * cancel method of the TccService to be informed of 
	 * the result).
	 * 
	 * @param id Correlation id of the work 
	 * as obtained during register (used to check if the
	 * work has not timed out).
	 */
	
	public void failed ( String id );
	
	
	/**
	 * Suspends the association of the calling thread
	 * with the work in question. Use this method to 
	 * reuse the application's thread for other activities
	 * while waiting for (asynchronous) results, or
	 * if you plan to continue the work in another thread.
	 * 
	 * @param id Correlation id of the work 
	 * as obtained during register.
	 * 
	 * 
	 */
	
	public void suspend ( String id );

	/**
	 * Resumes a previously suspended thread association.
	 * Typically used when an asynchronous reply comes in
	 * and is processed by another thread than the one that
	 * started the work. 
	 * 
	 * @param id Correlation id of the work 
	 * as obtained during register.
	 * 
	 */
	
	public void resume ( String id );
	

}
