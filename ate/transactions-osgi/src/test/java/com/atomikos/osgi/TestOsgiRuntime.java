package com.atomikos.osgi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

class TestOsgiRuntime implements BundleListener, FrameworkListener
{
	
	private String[] EXPECTED_BUNDLE_NAMES = {
		"com.atomikos.transactions-jta" , "com.atomikos.transactions" , 
		"com.atomikos.transactions-jdbc" , "com.atomikos.transactions-jms" , 
		"com.atomikos.transactions-api" , "com.atomikos.transactions-hibernate2",
		"com.atomikos.transactions-hibernate3" , "com.atomikos.util",
		"com.atomikos.transactions-jdbc-deprecated" , "com.atomikos.transactions-jms-deprecated"
	};
	
	private Felix felix;
	
	private Set symbolicNamesOfBundlesLoaded;
	
	TestOsgiRuntime() 
	{
		symbolicNamesOfBundlesLoaded = new HashSet();
	}

	public void start() {
	    Map configMap = new StringMap(false);
	    configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES,
	        "org.osgi.framework; version=1.3.0,"
	            + "org.osgi.service.packageadmin; version=1.2.0,"
	            + "org.osgi.service.startlevel; version=1.0.0,"
	            + "org.osgi.service.url; version=1.0.0");
	 
	    configMap.put(Constants.FRAMEWORK_STORAGE_CLEAN,
	        Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
	 
	    configMap.put(BundleCache.CACHE_ROOTDIR_PROP, "cache");
	 
	    try {
	      
	      List list = new ArrayList();
	 
	      configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
	          "org.xml.sax, org.xml.sax.helpers, javax.xml.parsers, javax.naming");
	      configMap.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, list);
	      configMap.put("felix.log.level", "4");

	      felix = new Felix(configMap);
	      felix.init();
	 
	      Properties props = new Properties();
	      props.put(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY, "../../../tmp/jars/");
	      props.put(AutoProcessor.AUTO_DEPLOY_ACTION_PROPERY,AutoProcessor.AUTO_DEPLOY_START_VALUE + ","
	              + AutoProcessor.AUTO_DEPLOY_INSTALL_VALUE);
	 
	      BundleContext felixBudleContext = felix.getBundleContext();
	 
	      AutoProcessor.process(props, felixBudleContext);
	      felixBudleContext.addFrameworkListener(this);
	      felixBudleContext.addBundleListener(this);
	      
	      felix.start();
	 
	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }
	}

	public void stop() throws BundleException {
		felix.stop();
		symbolicNamesOfBundlesLoaded.clear();
	}

	public void assertAtomikosBundlesWereLoadedSuccessfully() 
	{
		for ( int i = 0 ; i < EXPECTED_BUNDLE_NAMES.length ; i++ ) {
			String name = EXPECTED_BUNDLE_NAMES[i];
			if ( ! symbolicNamesOfBundlesLoaded.contains ( name ) ) {
				throw new RuntimeException ( "Missing bundle: " + name );
			}
		}
	}

	public void bundleChanged ( BundleEvent be ) 
	{
		String name = be.getBundle().getSymbolicName();
		symbolicNamesOfBundlesLoaded.add ( name );
	}

	public void frameworkEvent ( FrameworkEvent fe ) 
	{
	    if ( fe.getType() == FrameworkEvent.ERROR ) {
	    	System.err.println ( "OSGi ERROR:" );
	    	fe.getThrowable().printStackTrace();
	      }
	}
	
}
