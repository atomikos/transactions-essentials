/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.provider.imp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Properties;

import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.imp.CompositeTransactionManagerImp;
import com.atomikos.icatch.imp.TransactionServiceImp;
import com.atomikos.icatch.provider.Assembler;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.icatch.provider.TransactionServiceProvider;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.imp.StateRecoveryManagerImp;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.Repository;
import com.atomikos.recovery.OltpLog;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.imp.CachedRepository;
import com.atomikos.recovery.imp.FileSystemRepository;
import com.atomikos.recovery.imp.InMemoryRepository;
import com.atomikos.recovery.imp.OltpLogImp;
import com.atomikos.recovery.imp.RecoveryLogImp;
import com.atomikos.util.ClassLoadingHelper;
import com.atomikos.util.UniqueIdMgr;

public class AssemblerImp implements Assembler {
	
	private static final String DEFAULT_PROPERTIES_FILE_NAME = "transactions-defaults.properties";

	private static final String JTA_PROPERTIES_FILE_NAME = "jta.properties";

	private static final String TRANSACTIONS_PROPERTIES_FILE_NAME = "transactions.properties";

	private static final int MAX_TID_LENGTH = 64; //XID limitation
	
	private static com.atomikos.logging.Logger LOGGER = LoggerFactory.createLogger(AssemblerImp.class);
	
	
    private void loadPropertiesFromClasspath(Properties p, String fileName){
    		URL url = null;
    		
    		//first look in application classpath (cf ISSUE 10091)
    		url = ClassLoadingHelper.loadResourceFromClasspath(getClass(), fileName);		
    		if (url == null) {
    			url = getClass().getClassLoader().getSystemResource ( fileName );
    		}
    		if (url != null) {
    			loadPropertiesFromUrl(p, url);
    		} else {
    			LOGGER.logTrace("Could not find expected property file: " + fileName);
    		}
    }

	private void loadPropertiesFromUrl(Properties p, URL url) {
		InputStream in;
		try {
			in = url.openStream();
			p.load(in);
			in.close();
			LOGGER.logInfo("Loaded " + url.toString());
		} catch (IOException e) {
			LOGGER.logTrace("Failed to load property file: " + url.toString(), e);
		}
	}

	/**
	 * Called by ServiceLoader.
	 */
	public AssemblerImp() {
	}

	@Override
	public ConfigProperties initializeProperties() {
		Properties defaults = new Properties();
		loadPropertiesFromClasspath(defaults, DEFAULT_PROPERTIES_FILE_NAME);
		Properties transactionsProperties = new Properties(defaults);
		loadPropertiesFromClasspath(transactionsProperties, TRANSACTIONS_PROPERTIES_FILE_NAME);
		Properties jtaProperties = new Properties(transactionsProperties);
		loadPropertiesFromClasspath(jtaProperties, JTA_PROPERTIES_FILE_NAME);
		Properties customProperties = new Properties(jtaProperties);
		loadPropertiesFromCustomFilePath(customProperties);
		Properties finalProperties = new Properties(customProperties);
		ConfigProperties configProperties = new ConfigProperties(finalProperties);
		checkRegistration(configProperties);
		return configProperties;
	}
	private void checkRegistration(ConfigProperties configProperties) {
		if (!configProperties.getCompletedProperties().contains("com.atomikos.icatch.registered")) {
			String message ="Thanks for using Atomikos! Register at http://www.atomikos.com/Main/RegisterYourDownload to disable this message and receive FREE tips & advice.";
			LOGGER.logWarning(message);
			System.out.println(message);
		}
	}

	private void loadPropertiesFromCustomFilePath(Properties customProperties) {
		String customFilePath = System.getProperty(ConfigProperties.FILE_PATH_PROPERTY_NAME);
		if (customFilePath != null) {
			File file = new File(customFilePath);
			URL url;
			try {
				url = file.toURL();
				loadPropertiesFromUrl(customProperties, url);
			} catch (MalformedURLException e) {
				LOGGER.logFatal("File not found: " + customFilePath);
			}
		}
	}


	private void logProperties(Properties properties) {
		for (Entry<Object, Object> entry : properties.entrySet()) {
			LOGGER.logInfo("USING: " + entry.getKey() + " = " + entry.getValue());
		}
	}

	@Override
	public TransactionServiceProvider assembleTransactionService(
			ConfigProperties configProperties) {
		RecoveryLog recoveryLog =null;
		logProperties(configProperties.getCompletedProperties());
		String tmUniqueName = configProperties.getTmUniqueName();
		boolean enableLogging = configProperties.getEnableLogging();
		long maxTimeout = configProperties.getMaxTimeout();
		int maxActives = configProperties.getMaxActives();
		boolean threaded2pc = configProperties.getThreaded2pc();
		
		Repository repository;
		if (enableLogging) {
			try {
				repository = createCoordinatorLogEntryRepository(configProperties);
			} catch ( LogException le ) {
	            throw new SysException ( "Error in init: " + le.getMessage (), le );
	        }
		} else {
			repository = createInMemoryCoordinatorLogEntryRepository(configProperties);;
		}
		OltpLog oltpLog = createOltpLog(repository);
		//??? Assemble recoveryLog
		recoveryLog = createRecoveryLog(repository);
		StateRecoveryManagerImp	recoveryManager = new StateRecoveryManagerImp();
		recoveryManager.setOltpLog(oltpLog);
		UniqueIdMgr idMgr = new UniqueIdMgr ( tmUniqueName );
		int overflow = idMgr.getMaxIdLengthInBytes() - MAX_TID_LENGTH;
		if ( overflow > 0 ) {
			// see case 73086
			String msg = "Value too long : " + tmUniqueName;
			LOGGER.logFatal ( msg );
			throw new SysException(msg);
		}
		return new TransactionServiceImp(tmUniqueName, recoveryManager, idMgr, maxTimeout, maxActives, !threaded2pc, recoveryLog);
	}

	private Repository createInMemoryCoordinatorLogEntryRepository(
			ConfigProperties configProperties) {
		InMemoryRepository inMemoryCoordinatorLogEntryRepository = new InMemoryRepository();
		inMemoryCoordinatorLogEntryRepository.init();
		return inMemoryCoordinatorLogEntryRepository;
	}

	private RecoveryLog createRecoveryLog(Repository repository) {
		RecoveryLogImp recoveryLog = new RecoveryLogImp();
		recoveryLog.setRepository(repository);
		return recoveryLog;
	}

	private OltpLog createOltpLog(Repository repository) {
		OltpLogImp oltpLog = new OltpLogImp();
		oltpLog.setRepository(repository);
		return oltpLog;
	}

	private CachedRepository createCoordinatorLogEntryRepository(
			ConfigProperties configProperties) throws LogException {
		InMemoryRepository inMemoryCoordinatorLogEntryRepository = new InMemoryRepository();
		inMemoryCoordinatorLogEntryRepository.init();
		FileSystemRepository backupCoordinatorLogEntryRepository = new FileSystemRepository();
		backupCoordinatorLogEntryRepository.init();
		CachedRepository repository = new CachedRepository(inMemoryCoordinatorLogEntryRepository, backupCoordinatorLogEntryRepository);
		repository.init();
		return repository;
	}


	@Override
	public CompositeTransactionManager assembleCompositeTransactionManager() {
		return new CompositeTransactionManagerImp();
	}
}
