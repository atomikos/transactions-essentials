package com.atomikos.icatch.provider.imp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.RecoveryService;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.admin.LogControl;
import com.atomikos.icatch.imp.CompositeTransactionManagerImp;
import com.atomikos.icatch.imp.TransactionServiceImp;
import com.atomikos.icatch.provider.Assembler;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.persistence.imp.StateRecoveryManagerImp;
import com.atomikos.persistence.imp.VolatileStateRecoveryManager;
import com.atomikos.util.ClassLoadingHelper;
import com.atomikos.util.UniqueIdMgr;

public class AssemblerImp implements Assembler {
	
	private static final String DEFAULT_PROPERTIES_FILE_NAME = "transactions-defaults.properties";

	private static final String JTA_PROPERTIES_FILE_NAME = "jta.properties";

	private static final String TRANSACTIONS_PROPERTIES_FILE_NAME = "transactions.properties";

	private static final String FILE_PATH_PROPERTY_NAME = "com.atomikos.icatch.file";

	private static final int MAX_TID_LENGTH = 64; //XID limitation
	
	private static com.atomikos.logging.Logger LOGGER = LoggerFactory.createLogger(AssemblerImp.class);
	
	private TransactionServiceImp transactionService = null;
	
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
    			LOGGER.logDebug("Could not find expected property file: " + fileName);
    		}
    }

	private void loadPropertiesFromUrl(Properties p, URL url) {
		InputStream in;
		try {
			in = url.openStream();
			p.load(in);
			in.close();
		} catch (IOException e) {
			LOGGER.logDebug("Failed to load property file: " + url.toString(), e);
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
		return new ConfigProperties(finalProperties);
	}

	private void loadPropertiesFromCustomFilePath(Properties customProperties) {
		String customFilePath = System.getProperty(FILE_PATH_PROPERTY_NAME);
		if (customFilePath != null) {
			File file = new File(customFilePath);
			URL url;
			try {
				url = file.toURL();
				loadPropertiesFromUrl(customProperties, url);
			} catch (MalformedURLException e) {
				LOGGER.logWarning("File not found: " + customFilePath);
			}
		}
	}

	@Override
	public TransactionService assembleTransactionService(
			ConfigProperties configProperties) {
		if (transactionService == null) {			
			String tmUniqueName = configProperties.getTmUniqueName();
			String logBaseDir = configProperties.getLogBaseDir();
			boolean enableLogging = configProperties.getEnableLogging();
			long maxTimeout = configProperties.getMaxTimeout();
			int maxActives = configProperties.getMaxActives();
			boolean threaded2pc = configProperties.getThreaded2pc();
			StateRecoveryManager recMgr = null;
			if (enableLogging) {
				recMgr = new StateRecoveryManagerImp();
			} else {
				recMgr = new VolatileStateRecoveryManager();
			}
			UniqueIdMgr idMgr = new UniqueIdMgr ( tmUniqueName, logBaseDir );
			if ( idMgr.getMaxIdLengthInBytes() > MAX_TID_LENGTH ) {
				// see case 73086
				String msg = "Value too long :" + tmUniqueName;
				LOGGER.logWarning ( msg );
				throw new SysException(msg);
			}
			transactionService = new TransactionServiceImp(tmUniqueName, recMgr, idMgr, maxTimeout, maxActives, !threaded2pc);
		}
		return transactionService;
	}

	@Override
	public RecoveryService assembleRecoveryService(
			ConfigProperties configProperties) {
		assembleTransactionService(configProperties);
		return transactionService;
	}

	@Override
	public CompositeTransactionManager assembleCompositeTransactionManager(
			ConfigProperties configProperties) {
		return new CompositeTransactionManagerImp();
	}
}
