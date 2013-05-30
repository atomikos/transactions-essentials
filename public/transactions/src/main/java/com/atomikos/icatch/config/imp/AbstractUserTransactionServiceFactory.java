/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.icatch.config.imp;

import javax.naming.Context;

import com.atomikos.icatch.config.UserTransactionServiceFactory;

 /**
  * Commonalities for all factories go here.
  *
  *
  */

public abstract class AbstractUserTransactionServiceFactory implements
		UserTransactionServiceFactory
{


	/**
	 * The name of the property that indicates whether JTA transactions are to
	 * be in serial mode or not.
	 *
	 * Expands to {@value}.
	 */
	public static final String SERIAL_JTA_TRANSACTIONS_PROPERTY_NAME = "com.atomikos.icatch.serial_jta_transactions";


	/**
	 * The name of the property that specifies the default timeout (in
	 * milliseconds) that is set for transactions when no timeout is specified.
	 *
	 * Expands to {@value}.
	 */
	public static final String DEFAULT_JTA_TIMEOUT_PROPERTY_NAME = "com.atomikos.icatch.default_jta_timeout";

	/**
	 * The name of the property that specifies the output folder for the
	 * transaction manager's files.
	 *
	 * Expands to {@value}.
	 */
	public static final String OUTPUT_DIR_PROPERTY_NAME = "com.atomikos.icatch.output_dir";

	/**
	 * The name of the property that specifies the log base dir folder.
	 *
	 * Expands to {@value}.
	 */
	public static final String LOG_BASE_DIR_PROPERTY_NAME = "com.atomikos.icatch.log_base_dir";

	/**
	 * The name of the property that indicates the base name of the log files.
	 *
	 * Expands to {@value}.
	 */
	public static final String LOG_BASE_NAME_PROPERTY_NAME = "com.atomikos.icatch.log_base_name";

	/**
	 * The name of the property that specifies the maximum number of active
	 * transactions.
	 *
	 * Expands to {@value}.
	 */
	public static final String MAX_ACTIVES_PROPERTY_NAME = "com.atomikos.icatch.max_actives";

	/**
	 * The name of the property that specifies the maximum timeout (in
	 * milliseconds) that can be allowed for transactions.
	 *
	 * Expands to {@value}.
	 */
	public static final String MAX_TIMEOUT_PROPERTY_NAME = "com.atomikos.icatch.max_timeout";

	/**
	 * The name of the property indicating the globally unique name of the
	 * transaction manager.
	 *
	 * Expands to {@value}.
	 */
	public static final String TM_UNIQUE_NAME_PROPERTY_NAME = "com.atomikos.icatch.tm_unique_name";

	/**
	 * The name of the property indicating the checkpoint interval.
	 *
	 * Expands to {@value}.
	 */
	public static final String CHECKPOINT_INTERVAL_PROPERTY_NAME = "com.atomikos.icatch.checkpoint_interval";

	/**
	 * The name of the property indicating whether remote clients can start
	 * transactions on this service or not.
	 *
	 * Expands to {@value}.
	 */
	public static final String CLIENT_DEMARCATION_PROPERTY_NAME = "com.atomikos.icatch.client_demarcation";

	/**
	 * The name of the property indicating what RMI export mechanism to use for
	 * exporting transaction service objects (if applicable).
	 *
	 * Expands to {@value}.
	 */
	public static final String RMI_EXPORT_CLASS_PROPERTY_NAME = "com.atomikos.icatch.rmi_export_class";

	/**
	 * The name of the property indicating what the JNDI initial context factory
	 * is.
	 *
	 * Expands to {@value}.
	 */
	public static final String JNDI_INITIAL_CONTEXT_FACTORY_PROPERTY_NAME = Context.INITIAL_CONTEXT_FACTORY;

	/**
	 * The name of the property indicating what the JNDI provider URL is.
	 *
	 * Expands to {@value}.
	 */
	public static final String JNDI_PROVIDER_URL_PROPERTY_NAME = Context.PROVIDER_URL;

	/**
	 * The name of the property indicating whether (JTA/XA) resources should be
	 * registered automatically or not.
	 *
	 * Expands to {@value}.
	 */
	public static final String AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME = "com.atomikos.icatch.automatic_resource_registration";

	/**
	 * The name of the property indicating whether or not to enable logging.
	 *
	 * Expands to {@value}.
	 */
	public static final String ENABLE_LOGGING_PROPERTY_NAME = "com.atomikos.icatch.enable_logging";

	/**
	 * The name of the property specifying whether two-phase commit should be done concurrently with threads.
	 *
	 * Expands to {@value}.
	 */
	public static final String THREADED_2PC_PROPERTY_NAME = "com.atomikos.icatch.threaded_2pc";

	/**
	 * The name of the property specifying whether or not to force shutdown on VM exit.
	 *
	 * Expands to {@value}.
	 */

	public static final String REGISTER_SHUTDOWN_HOOK_PROPERTY_NAME = "com.atomikos.icatch.force_shutdown_on_vm_exit";


	/**
	 * The name of the property specifying whether or not to log serializable objects.
	 */
	public static final String SERIALIZABLE_LOGGING_PROPERTY_NAME = "com.atomikos.icatch.serializable_logging";


}
