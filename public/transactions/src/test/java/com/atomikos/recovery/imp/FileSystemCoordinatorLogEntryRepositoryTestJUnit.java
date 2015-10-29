package com.atomikos.recovery.imp;

import java.util.Properties;

import org.junit.Test;

import com.atomikos.icatch.provider.ConfigProperties;

public class FileSystemCoordinatorLogEntryRepositoryTestJUnit {
	FileSystemCoordinatorLogEntryRepository sut = new FileSystemCoordinatorLogEntryRepository();
	
	@Test
	public void testName() throws Exception {
		Properties properties = new Properties(); 
		properties.put("com.atomikos.icatch.log_base_dir", ClassLoader.getSystemClassLoader().getResource(".").getFile());
		properties.put("com.atomikos.icatch.log_base_name", "test");
		properties.put("com.atomikos.icatch.checkpoint_interval", "10000");
		ConfigProperties configProperties =new ConfigProperties(properties);
		sut.init(configProperties);
	}
	
}
