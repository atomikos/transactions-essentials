package com.atomikos.icatch.config.imp;

import junit.framework.TestCase;

public class TSMetaDataImpTestJUnit extends TestCase {

	private static final String JTA_VERSION = "1.0.1";
	
	private static final String VERSION = "3.0.0";
	
	private static final String PRODUCT_NAME = "TEST";
	
	private TSMetaDataImp md;
	
	protected void setUp() throws Exception {
		super.setUp();
		md = new TSMetaDataImp ( 
				JTA_VERSION , VERSION , PRODUCT_NAME , false , false );
	}
	
	public void testJtaVersion()
	{
		assertEquals ( JTA_VERSION , md.getJtaVersion() );
	}
	
	public void testReleaseVersion()
	{
		assertEquals ( VERSION , md.getReleaseVersion() );
	}
	
	public void testProductName()
	{
		assertEquals ( PRODUCT_NAME , md.getProductName() );
	}
	
	public void testSupportsImport()
	{
		assertFalse ( md.supportsImport() );
		md = new TSMetaDataImp ( 
				JTA_VERSION , VERSION , PRODUCT_NAME , true , false );
		assertTrue ( md.supportsImport() );
	}
	
	public void testSupportsExport()
	{
		assertFalse ( md.supportsExport());
		md = new TSMetaDataImp ( 
				JTA_VERSION , VERSION , PRODUCT_NAME , false , true );
		assertTrue ( md.supportsExport() );
	}

}
