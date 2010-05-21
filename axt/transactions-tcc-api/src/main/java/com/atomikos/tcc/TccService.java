package com.atomikos.tcc;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurRollbackException;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * TryConfirmCancel (TCC) is a convenient business activity paradigm for
 * services that support the notions of confirmation and cancelation. This interface
 * represents the application-specific termination callbacks needed by the 
 * transaction service to terminate the work of an activity in an application-specific way.
 * <p>
 * Every TCC service invocation has a unique identifier returned by 
 * <code>TryConfirmCancelServiceManager.register</code>. This identifier is
 * used by the system as an argument to every callback method. 
 * <p>
 * The TCC paradigm is a nice way of doing distributed (business) transactions
 * without the overhead and limitations of classical ACID transactions. 
 * Instead, the TCC paradigm uses an approach based on open nested transactions
 * where rollback (cancel) and commit (confirm) are implemented by application-level logic.
 * No locks are kept, meaning that resource consumption is kept to a minimum.
 * <p>
 *<b> Implementations should have public visibility and have a public, no-argument constructor to work correctly.</b>
 * 
 * 
 */

public interface TccService 
{
	/**
	 * Confirms the LOCAL business logic for this service. Called by the transaction
	 * service when the corresponding activity completes successfully. 
	 * If a JTA transaction is required for the implementation of this method then
	 * it is the implementer's responsibility to create one.
	 * 
	 * <b>Make sure that this method is idempotent (meaning it should have the
	 * same effect if executed once or many times in a row).</b>
	 *  <p>
	 * Only local effects matter, because any remote effects are dealt with
	 * by the transaction service. This allows a synchronous paradigm, and also
	 * simplifies the implementation.
	 * 
	 * @param id The identifier of the work, as obtained during registration.
	 * If needed then the implementation can use this id to retrieve all the related context
	 *  information from its database.
	 * 
	 * @throws HeurRollbackException 
	 * Optional exception that may be thrown if the activity has been canceled already.
	 * For example, in the case of seat reservations: if the status was 
	 * already set to 'CANCELED'. This reflects a fatal and non-recoverable
	 * error in the outcome.
	 * @throws TccException On any application-level error. In this case, the
	 * system will retry this method a number of times.
	 */
	
	public void confirm ( String id ) throws HeurRollbackException, TccException;

	/**
	 * Cancels the LOCAL business logic for this service. Called by the transaction
	 * core when the corresponding activity cancels. <b>Make sure that this
	 * method is idempotent (meaning it should have the same effect if executed
	 * once or many times in a row).</b> This method should also be able to 
	 * handle cases where the work failed, executed incompletely or not at all.
	 * If a JTA transaction is required for the implementation of this method then
	 * it is the implementer's responsibility to create one.
	 * <p>
	 * Only local effects matter, because any remote effects are dealt with
	 * by the transaction service. This allows a synchronous paradigm, and also
	 * simplifies the implementation.
	 * 
	 *  @param id The identifier of the work, as obtained during registration.
	 *  If needed then the implementation can use this id to retrieve all the related context
	 *  information from its database.
	 * 
	 * @throws HeurCommitException Optional exception that may be thrown if the activity has been canceled already.
	 * In case the activity has been confirmed
	 * (for instance by manual intervention in the system). For example: in the
	 * case of the seat reservation, if the status was already set to 'CONFIRMED'.
	 * This reflects a non-recoverable fatal error in the outcome.
	 * @throws TccException On any application-level error. In this case, the 
	 * system will retry this method a number of times.
	 */
	
	public void cancel ( String id ) throws HeurCommitException, TccException;

	/**
	 * Callback for recovery purposes: if any non-serializable state needs to be
	 * restored for the cancel or confirmation process. 
	 * The system guarantees that this method will be called for recovered
	 * instances (i.e., those services that were pending at the time of shutdown or crash)
	 * and <b>before</b> cancel or confirm is called. 
	 * <p>
	 * Only local effects matter, because any remote effects are dealt with
	 * by the transaction service. This allows a synchronous paradigm, and also
	 * simplifies the implementation.
	 * <p>
	 * <b>Make sure that this method is idempotent (meaning it should
	 * have the same effect if executed once or many times in a row).</b>
	 * 
	 * @param id The identifier of the work, as obtained during registration.
	 * If needed then the implementation can use this id to retrieve all the related context
	 *  information from its database.
	 * 
	 * @return boolean False if the state could not be recovered, or if the
	 * supplied id was not found by the implementation. The latter may happen 
	 * if multiple services co-exist in the same server, for instance in J2EE
	 * (web) applications where each application may have its own TccService 
	 * implementation.
	 */
	
	public boolean recover ( String id );

}
