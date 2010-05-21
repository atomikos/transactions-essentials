package com.atomikos.vendor;

import java.io.File;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import com.atomikos.license.CorruptLicenseException;
import com.atomikos.license.License;
import com.atomikos.license.LicenseException;
import com.atomikos.license.WrongProductLicenseException;
import com.atomikos.util.TestCaseWithTemporaryOutputFolder;

public class LicenseTestJUnit extends TestCaseWithTemporaryOutputFolder 
{
	
	private String getOutputFolder() {
		return "./";
	}
	

   	
   	private long getExpiryDate() 
   	{
   	 GregorianCalendar calendar = new GregorianCalendar();
   	 calendar.setTime ( new Date() );
   	 calendar.add ( Calendar.MONTH , 18 );
   	 Date expiryDate = calendar.getTime();
   	 return expiryDate.getTime();
   	}

	public LicenseTestJUnit ( String name )
	{	
		super ( name );
	}
	
	protected void setUp()
	{
		super.setUp();
		
	}
	
	protected void tearDown() 
	{
		File file = null;
		try {
			file = new File ( getOutputFolder() + License.LICENSE_PROPERTY_FILE_NAME );
			file.delete();
		} catch ( Exception ok ) {}
		
		try {
			file = new File ( getOutputFolder() + License.LICENSE_SIGNATURE_FILE_NAME );
			file.delete();
		} catch ( Exception ok ) {}
	}

	
    private void testEvaluation ( boolean expired )
    throws Exception
    {
		String key = "secret_key";
		String productName = "MySoftware";
		Date now = new Date();
		long expiryDate = 0;
		if ( expired ) expiryDate = now.getTime() - 10000;
		else expiryDate = now.getTime() + 10000;
		
		CreateLicense.createEvaluationLicense ( null ,
			productName , getOutputFolder() , key  , expiryDate,
			new Properties() );
         
	    //test if the license can be read in
		License license = License.createLicense ( 
				key );
    }
    
    public void testEvaluationWithExpiration()
    throws Exception
    {
    		testEvaluation ( true );
    }
    
    public void testEvaluation()
    throws Exception
    {
    		testEvaluation ( false );
    }
    
   
    
    public void testUnlimited() throws Exception
    {
         String key = "secret_key";
         String baseName = getOutputFolder();
         String productName = "MySoftware";
         CreateLicense.createUnlimitedLicense ( "Atomikos" , productName , baseName , key , getExpiryDate(), new Properties() );
         

         License license = License.createLicense (
                    key );
         
         //check if the local host is allowed for a version lower than max
         license.checkLocalHost ( productName  );
            
        
         //test if local host is allowed to run another product than the license says
         try { 
         	license.checkLocalHost ( productName + "blabla" );
            failTest ( "Local host allowed to run different product?" );
         }
         catch ( WrongProductLicenseException shouldHappen ) {}
            
         //test signature
         try {
          
            license = License.createLicense ( 
                key + "blabla");
            failTest ( "Signature not solid?" );
         }
         catch ( CorruptLicenseException e ) {
              //should happen
         }
    }
    
    private void testDomain ( boolean otherDomain ) throws Exception
    {
         String key = "secret_key";
         String baseName = getOutputFolder();
         String domain = "";
         String productName = "MySoftware";
         if ( !otherDomain ) {
             domain = InetAddress.getLocalHost().getHostName(); 
             int dot = domain.indexOf ( "." );
             domain = domain.substring ( dot + 1 );
             domain = domain.trim();
         }
         else 
            domain = "nonexistent.dom";
            
         CreateLicense.createDomainLicense ( null ,
         productName , baseName , key , getExpiryDate() , domain , new Properties() );
         
         //test if the license can be read in
         License license = License.createLicense ( 
                    key );
         
         //check if the local host is allowed for a version lower than max
         if ( !otherDomain ) {
         
         	try {
         
           		license.checkLocalHost ( productName );
         	}
         	catch ( LicenseException le ) {
              failTest ( "Local host not allowed for domain: " + domain );
           	}
         }
         else 
            try { 
            	license.checkLocalHost ( productName );
            	
            	failTest ( "Local host allowed for domain: " + domain );
            }
            catch ( LicenseException shouldHappen ) {}
          
          
         //test if local host is allowed to run another product than the license says
         try {
         	license.checkLocalHost ( productName + "blabla" );
            failTest ( "Local host allowed to run different product?" );
         }
         catch ( LicenseException shouldHappen ) {}
            
         //test signature
         try {
            license = License.createLicense ( 
                key + "blabla" );
            failTest ( "Signature not solid?" );
         }
         catch ( CorruptLicenseException e ) {
              //should happen
         }
    }
    
    public void testDomainLicense() throws Exception
    {
    		testDomain ( false );
    }
    
    public void testDomainLicenseWithWrongDomain() throws Exception
    {
    		testDomain ( true );
    }
    
    private void testHost ( boolean otherHost ) throws Exception
    {
         String key = "secret_key";
         String productName = "MySoftware";
         String baseName = getOutputFolder();
         String host = "";
         if ( !otherHost ) {
             host = InetAddress.getLocalHost().getHostAddress(); 
             
         }
         else 
            host = "www.nonexistent.dom";
        
        
         String[] hosts = new String[1];
         hosts[0] = host;            
         CreateLicense.createNodeLockedLicense (  null,
         	productName , baseName , key , getExpiryDate() , hosts,
         	new Properties() );
         
         //test if the license can be read in
         License license = License.createLicense ( 
                    key );
         
         //check if the local host is allowed for a version lower than max
         if ( !otherHost ) {
           		license.checkLocalHost ( productName );
              //failTest ( "Local host not allowed for host: " + host );
         }
         else 
            try {
            	license.checkLocalHost ( productName );
              	failTest ( "Local host allowed for host: " + host );
            }
            catch ( LicenseException shouldHappen ) {}
   
         
         //test if local host is allowed to run another product than the license says
         try {
         	license.checkLocalHost ( productName + "blabla" );
            failTest ( "Local host allowed to run different product?" );
         }
         catch ( LicenseException shouldHappen ){}
            
         //test signature
         try {
            license = License.createLicense ( 
                key + "blabla" );
            failTest ( "Signature not solid?" );
         }
         catch ( CorruptLicenseException e ) {
              //should happen
         }
    }
    
    public void testHostLicense()
    throws Exception
    {
    		testHost ( false );
    }
    
    public void testHostLicenseWithWrongHost()
    throws Exception
    {
    		testHost ( true );
    }
    
    public void testEnforcedFeatures() throws Exception
    {
    	Properties features = new Properties();
    	String key = "secret_key";
    	String featureName = "myFeature";
    	String featureValue = "myValue";
    	features.setProperty ( featureName , featureValue );
    	CreateLicense.createDeveloperLicense( null , "featuredProduct" , getOutputFolder() ,key , getExpiryDate() ,features);
		//test if the license can be read in
		License license = License.createLicense ( 
						  key );
		Properties test = new Properties();
		license.filterProductFeatures(test);
		if ( test.getProperty ( featureName ) == null ) 
			failTest ( "Feature not supported");
		if ( !test.getProperty ( featureName ).equals (featureValue) ) 
			failTest ( "Feature not conserved by license");
    	
    }
    
}
