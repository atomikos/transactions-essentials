/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.provider;

import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;

public final class ConfigProperties {


	public static final String TM_UNIQUE_NAME_PROPERTY_NAME = "com.atomikos.icatch.tm_unique_name";
	public static final String LOG_BASE_DIR_PROPERTY_NAME = "com.atomikos.icatch.log_base_dir";
	public static final String LOG_BASE_NAME_PROPERTY_NAME = "com.atomikos.icatch.log_base_name";
	public static final String ENABLE_LOGGING_PROPERTY_NAME = "com.atomikos.icatch.enable_logging";
	public static final String MAX_TIMEOUT_PROPERTY_NAME = "com.atomikos.icatch.max_timeout";
	public static final String MAX_ACTIVES_PROPERTY_NAME = "com.atomikos.icatch.max_actives";
	public static final String FORCE_SHUTDOWN_ON_VM_EXIT_PROPERTY_NAME = "com.atomikos.icatch.force_shutdown_on_vm_exit";
	public static final String FILE_PATH_PROPERTY_NAME = "com.atomikos.icatch.file";
	public static final String CHECKPOINT_INTERVAL_PROPERTY_NAME = "com.atomikos.icatch.checkpoint_interval";

	public static final String FORGET_ORPHANED_LOG_ENTRIES_DELAY_PROPERTY_NAME = "com.atomikos.icatch.forget_orphaned_log_entries_delay";
	public static final String OLTP_MAX_RETRIES_PROPERTY_NAME = "com.atomikos.icatch.oltp_max_retries";
	public static final String OLTP_RETRY_INTERVAL_PROPERTY_NAME = "com.atomikos.icatch.oltp_retry_interval";
	public static final String RECOVERY_DELAY_PROPERTY_NAME = "com.atomikos.icatch.recovery_delay";

	public static final String ALLOW_SUBTRANSACTIONS_PROPERTY_NAME = "com.atomikos.icatch.allow_subtransactions";
    public static final String THROW_ON_HEURISTIC_PROPERTY_NAME = "com.atomikos.icatch.throw_on_heuristic";
    public static final String JVM_ID_PROPERTY_NAME = "com.atomikos.icatch.jvm_id";

    public static final String LOG_LOCK_ACQUISITION_MAX_ATTEMPTS = "com.atomikos.icatch.log_lock_acquisition_max_attempts";
    public static final String LOG_LOCK_ACQUISITION_RETRY_DELAY = "com.atomikos.icatch.log_lock_acquisition_retry_delay";

    public static final String REST_CLIENT_BUILDER = "com.atomikos.remoting.rest_client_builder";

	/**
	 * Replace ${...} sequence with the referenced value from the given properties or 
	 * (if not found) the system properties -
	 * contributed through Marian Kelc (marian.kelc@eplus.de)
	 * E-Plus Mobilfunk GmbH &amp; Co. KG, Germany
	 */
	private static String evaluateReference ( String value , Properties properties )
	{
		String result = value;
		//by default, the value as-is is returned

		int startIndex = value.indexOf ( '$' );
		if ( startIndex > -1 && value.charAt ( startIndex +1 ) == '{') {
			//at least one reference is found
			int endIndex = value.indexOf ( '}' );
			if ( startIndex + 2 == endIndex )
				throw new IllegalArgumentException ( "property ref cannot refer to an empty name: ${}" );
			if ( endIndex == -1 )
				throw new IllegalArgumentException ( "unclosed property ref: ${" + value.substring ( startIndex + 2 ) );

			//strip-off reference characters -> get the referenced property name 
			String subPropertyKey = value.substring ( startIndex + 2, endIndex );
			//the properties take precedence -> try them first
			String subPropertyValue = properties.getProperty ( subPropertyKey );
			if ( subPropertyValue == null ) {
				//not found in properties -> try system property
				subPropertyValue = System.getProperty ( subPropertyKey );
			}

			if ( subPropertyValue != null ) {
				//in-line refs supported - result is prefix + value + suffix !!!
				result = result.substring ( 0, startIndex ) + subPropertyValue + result.substring ( endIndex +1 );
				//two or more refs supported - evaluate any remaining references in the value
				result =  evaluateReference ( result , properties );
			}
			else {
				//referenced value not found -> ignore any other references and return value as-is
				//NOTE: trying to resolve further references would lead to infinite recursion
			}

		}

		return result;
	}

    private static String getDefaultName ()
    {

        String ret = "tm";
        try {
            ret = java.net.InetAddress.getLocalHost ().getHostAddress ()
                    + ".tm";
        } catch ( UnknownHostException e ) {
            // ignore: use short default
        }

        return ret;
    }


	private Properties properties;


	public ConfigProperties(Properties properties) {
		if (properties == null) throw new IllegalArgumentException("Properties should not be null");
		this.properties = properties;
	}

	private void setDefaultTmUniqueName() {
		if (properties.getProperty(TM_UNIQUE_NAME_PROPERTY_NAME) == null ) {
			properties.setProperty(TM_UNIQUE_NAME_PROPERTY_NAME, getDefaultName());
		}
	}
	
	private void setDefaultJvmId() {
	    if (properties.getProperty(JVM_ID_PROPERTY_NAME) == null ) {
	        properties.setProperty(JVM_ID_PROPERTY_NAME, getDefaultJvmId());
	    }
	}

	private String getDefaultJvmId() {
	    return ManagementFactory.getRuntimeMXBean().getName();
    }

    private void applySystemProperties() {
		Properties systemProperties = System.getProperties();
		Enumeration<?> propertyNames = systemProperties.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String name = (String) propertyNames.nextElement();
			if (name.startsWith("com.atomikos")) {
				properties.setProperty(name, systemProperties.getProperty(name));
			}
		}
	}

	private void substitutePlaceHolderValues() {
		//resolve referenced values with ant-like ${...} syntax
		Enumeration<?> allProps= properties.propertyNames();
		while ( allProps.hasMoreElements() ) {
			String key = ( String ) allProps.nextElement();
			String raw = properties.getProperty ( key );
			String value= evaluateReference ( raw , properties );
			if ( !raw.equals ( value ) ) {
				properties.setProperty ( key, value );
			}
		}
	}

	public String getProperty(String name) {
		completeProperties();
		String ret = properties.getProperty(name);
		if (ret == null) {
			throw new IllegalArgumentException("Missing required property: " + name);
		}
		ret = ret.trim();
		return ret;
	}

	public void setProperty(String name, String value) {
		properties.setProperty(name, value);		
	}

	public boolean getAsBoolean(String name) {
		boolean ret = false;
		String retAsString = getProperty(name);
		ret = Boolean.valueOf(retAsString);
		return ret;
	}

	public int getAsInt(String name) {
		String retAsString = getProperty(name);
		return Integer.valueOf(retAsString);
	}

	public long getAsLong(String name) {
		String retAsString = getProperty(name);
		return Long.valueOf(retAsString);
	}

	public String getTmUniqueName() {
		return getProperty(TM_UNIQUE_NAME_PROPERTY_NAME);
	}

	public String getLogBaseDir() {
		return getProperty(LOG_BASE_DIR_PROPERTY_NAME);
	}

	public String getLogBaseName() {
		return getProperty(LOG_BASE_NAME_PROPERTY_NAME);
	}

	public boolean getEnableLogging() {
		return getAsBoolean(ENABLE_LOGGING_PROPERTY_NAME);
	}

	public long getMaxTimeout() {
		return getAsLong(MAX_TIMEOUT_PROPERTY_NAME);
	}

	public int getMaxActives() {
		return getAsInt(MAX_ACTIVES_PROPERTY_NAME);
	}
	
	public long getCheckpointInterval(){
		return getAsLong(CHECKPOINT_INTERVAL_PROPERTY_NAME);
	}

	public void applyUserSpecificProperties(Properties userSpecificProperties) {
		Enumeration<?> names = userSpecificProperties.propertyNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			properties.setProperty(name, userSpecificProperties.getProperty(name));
		}
	}

	public Properties getCompletedProperties() {
		Properties ret = new Properties();
		completeProperties();
		Enumeration<?> propertyNames = properties.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String name = (String) propertyNames.nextElement();
			ret.setProperty(name, getProperty(name));
		}
		return ret;
	}

	private void completeProperties() {
		applySystemProperties();
		substitutePlaceHolderValues();
		setDefaultTmUniqueName();
		setDefaultJvmId();
	}

	public boolean getForceShutdownOnVmExit() {
		return getAsBoolean(FORCE_SHUTDOWN_ON_VM_EXIT_PROPERTY_NAME);
	}

	public long getForgetOrphanedLogEntriesDelay() {
		return getAsLong(FORGET_ORPHANED_LOG_ENTRIES_DELAY_PROPERTY_NAME);
	}

	public int getOltpMaxRetries() {
		return getAsInt(OLTP_MAX_RETRIES_PROPERTY_NAME);
	}

	public long getOltpRetryInterval() {
		return getAsInt(OLTP_RETRY_INTERVAL_PROPERTY_NAME);
	}

	public long getRecoveryDelay() {
		return getAsLong(RECOVERY_DELAY_PROPERTY_NAME);
	}

	public boolean getAllowSubTransactions() {
		return getAsBoolean(ALLOW_SUBTRANSACTIONS_PROPERTY_NAME);
	}

	public boolean getThrowOnHeuristic() {
		return getAsBoolean(THROW_ON_HEURISTIC_PROPERTY_NAME);
	}

	public int getLockAcquisitionMaxAttempts(){
		return getAsInt(LOG_LOCK_ACQUISITION_MAX_ATTEMPTS);
	}

	public long getLockAcquisitionRetryDelay(){
		return getAsLong(LOG_LOCK_ACQUISITION_RETRY_DELAY);
	}

    public String getJvmId() {
        return getProperty(JVM_ID_PROPERTY_NAME);

    }
    
    public String getRestClientBuilder() {
        return getProperty(REST_CLIENT_BUILDER);

    }


}
