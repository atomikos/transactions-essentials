//$Id: License.java,v 1.1.1.1 2006/10/02 15:20:56 guy Exp $
//$Log: License.java,v $
//Revision 1.1.1.1  2006/10/02 15:20:56  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:36  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:41  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:03  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:44  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.9  2005/08/23 07:16:57  guy
//Corrected javadoc.
//
//Revision 1.8  2005/08/10 07:45:18  guy
//Corrected javadoc.
//
//Revision 1.7  2004/10/25 08:46:09  guy
//Removed old todos
//
//Revision 1.6  2004/10/22 13:31:20  guy
//Reformatted output info to fit in HTML frame.
//
//Revision 1.5  2004/09/01 13:39:59  guy
//Improved exceptions if no license file (null URL).
//
//Revision 1.4  2004/03/25 15:10:56  guy
//Updated printInfo to include fixed features
//
//Revision 1.3  2004/03/25 12:54:39  guy
//Added license-based features that can be fixed, for instance
//the max number of concurrent transactions.
//
//Revision 1.2  2004/03/22 15:38:33  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.5  2004/03/22 07:57:56  guy
//Updated license classes: sendForm includes product and version
//
//Revision 1.1.2.4  2004/03/17 15:27:28  guy
//Added Developer license
//
//Revision 1.1.2.3  2004/03/08 13:05:59  guy
//*** empty log message ***
//
//Revision 1.1.2.2  2004/02/18 12:07:50  guy
//Added support for printing license info.
//
//Revision 1.1.2.1  2004/01/29 15:54:06  guy
//*** empty log message ***
//
//Revision 1.3  2002/03/03 09:19:38  guy
//Changed license file lookup from filename to URL, this allows checking in JAR files as well!
//
//Revision 1.2  2002/01/25 07:39:29  guy
//Changed domain license to check only on ending condition, not equals.
//
//Revision 1.1  2002/01/22 13:44:52  guy
//Added a License class for better checking of license conditions.
//

package com.atomikos.license;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import com.atomikos.diagnostics.Console;

 /**
   *Copyright &copy; 2002-2008, Atomikos. All rights reserved.
   *
   *A utility class for checking if the local machine is allowed to
   *run the software according to the specified license
   *file. The license file is checked for integrity by checking the signature.
   *A license is assigned based on the IP address or domain of the host.
   *
   */
 
public  abstract class License
{
	
	/**
	 * The base name of the license files.
	 */
	public static final String LICENSE_FILE_BASE_NAME = "atomikos-license";
	
	/**
	 * The name of the property license file.
	 */
	public static final String LICENSE_PROPERTY_FILE_NAME = LICENSE_FILE_BASE_NAME + ".prp";
	
	/**
	 * The name of the signature license file.
	 */
	public static final String LICENSE_SIGNATURE_FILE_NAME = LICENSE_FILE_BASE_NAME + ".sig";
	
	
	
     /**
      *The name of the property that holds the product name.
      *The value should NOT include the version number.
      */
      
     public static final String PRODUCT_NAME_PROPERTY_NAME = "product";
   
     /**
      *The name of the license property that indicates the type of license.
      *Must be present!
      */
      
    public static final String LICENSE_TYPE_PROPERTY_NAME = "type";
    
     /**
      *The name of the license property that indicates the different
      *hosts a license is for.
      */
    
    public static final String HOSTS_PROPERTY_NAME = "hosts";
    
    
    
    /**
     *The name of the license file property indicating what domain
     *the license is for.
     */
    
    public static final String DOMAIN_PROPERTY_NAME = "domain";
    
    /**
     * The name of the property whose value indicates the 
     * expiration date of the license.
     */
    
    public static final String EXPIRES_PROPERTY_NAME = "expiry";
    
    /**
     * The value of the license type property indicating an evaluation
     * license. If this is the value of the LICENSE_TYPE_PROPERTY_NAME
     * then the property EXPIRES_PROPERTY_NAME must also be 
     * set to a long value that denotes the expiry date.
     */
    
	public static final String EVAL_TYPE_PROPERTY_VALUE = "evaluation";
    
     /**
      *The value of the license type property indicating a host-based license.
      *If this is the value of LICENSE_TYPE_PROPERTY_NAME, then 
      *the property HOSTS_PROPERTY_NAME must also be set to an enumeration
      *of allowed host IP addresses.
      */
    
    public static final String HOST_TYPE_PROPERTY_VALUE = "host";
    
     /**
      *The value of the license type property indicating a domain-based 
      *license. If this is the value of LICENSE_TYPE_PROPERTY_ NAME, then 
      *the property DOMAIN_PROPERTY_NAME must also be set to the 
      *appropriate IP domain name.
      */
      
    public static final String DOMAIN_TYPE_PROPERTY_VALUE = "domain";
    
    /**
      *The value of the license type property indicating an unlimited hosts 
      *license. If this is the value of LICENSE_TYPE_PROPERTY_ NAME, then 
      *no additional property is needed to indicate which hosts.
      */
    
    public static final String UNLIMITED_TYPE_PROPERTY_VALUE = "unlimited";
    
     /**
      * Value of license type property that indicates development use only.
      * In this case, no additional host properties are needed.
      */
    public static final String DEVELOPER_TYPE_PROPERTY_VALUE = "developer";
    
    private static final String REGISTRATION_URL = "http://registration.atomikos.com";
    
    private static final String LICENSE_FILE_URL = "/" +  License.LICENSE_PROPERTY_FILE_NAME;
    
    private static final String SIGNATURE_FILE_URL =  "/" +  License.LICENSE_SIGNATURE_FILE_NAME;
  
    /**
     * The name of the property indicating the license owner.
     */
    public static final String OWNER_PROPERTY_NAME = "grantee";

	private static final int REGISTRATION_TIMEOUT = 5000;
    
    private static  Console console_ = null;
    //for diagnostics
    
    private static String digestName_ = "SHA";
    //the name of the digest alg.
    
    private  Properties properties_ = new Properties();
    //the license properties
    
    protected static void printMsg ( String msg ) 
    {
        try {
            if ( console_ != null ) 
              console_.println ( msg ); 
        }
        catch ( IOException io ) {
              System.err.println ( msg );
        }
    }
    
    private static License createLicense ( Properties properties )
    {
    	License ret = null;
    	String type = properties.getProperty ( LICENSE_TYPE_PROPERTY_NAME );
    	
    	if ( EVAL_TYPE_PROPERTY_VALUE.equals ( type ) ) {
    		ret = new EvaluationLicense ( properties );
    	}
    	else if ( HOST_TYPE_PROPERTY_VALUE.equals ( type ) ) {
    		ret = new NodeLockedLicense ( properties );
    	}
    	else if ( DOMAIN_TYPE_PROPERTY_VALUE.equals ( type ) ) {
    		ret = new DomainLicense ( properties );
    	}
    	else if ( UNLIMITED_TYPE_PROPERTY_VALUE.equals ( type ) ) {
    		ret = new UnlimitedLicense ( properties );
    	}
    	else if ( DEVELOPER_TYPE_PROPERTY_VALUE.equals ( type ) ) {
    		ret = new DeveloperLicense ( properties );
    	}
    	
    	return ret;
    }
    
    private static void assertIntegrity ( 
        URL licenseFileURL , URL sigFileURL , String key )
    throws CorruptLicenseException
    {
         InputStream lin = null;
         ObjectInputStream oin = null;
         if ( licenseFileURL == null || sigFileURL == null )
         	throw new CorruptLicenseException ( "No License Found!" );
         try {
			  //check for system date: if set back then license is being 
			  //tampered with
			  Date now = new Date();
			  File licenseFile = new File ( licenseFileURL.getPath() );
			  Date lastModified = new Date ( licenseFile.lastModified() );		
			 	  
			  if ( now.before ( lastModified )) 
			  	throw new CorruptLicenseException ( "ERROR: system clock turned back?!?");
			  //set last modification date to one more
			  	
			  licenseFile.setLastModified ( now.getTime() );
			  	
              lin = licenseFileURL.openStream();
              InputStream sin = sigFileURL.openStream();
              
              //get stored signature in sig file
              oin = new ObjectInputStream ( sin );
              byte[] sig = ( byte[] ) oin.readObject();
              
              //calculate signature of license file
              MessageDigest md = MessageDigest.getInstance ( digestName_ );
              int next = 0;
              while ( next != -1 ) {
                  next = lin.read();
                  if ( next  != -1 ) {
                      md.update ( ( byte ) next );
                  }
              }
              byte[] currentSig = md.digest ( key.getBytes() );
              if ( ! MessageDigest.isEqual ( currentSig , sig ) ) {
                  printMsg ( "Incorrect signature in license!" );
                  printMsg ( "While checking license file:" + licenseFileURL );
                  throw new CorruptLicenseException ( "Incorrect signature in license" );
              }
           	  
           	  
           	  
         }
         catch ( Exception io ) {
              printMsg ( "Error verifying license file integrity:" );
              printMsg ( io.getMessage() );
			  printMsg ( "While checking license file:" + licenseFileURL );
              throw new CorruptLicenseException ( io.getMessage() );
         }
         finally {
          
              if ( lin != null ) {
                  try {
                      lin.close();
                  }
                  catch ( IOException io ) {
                      throw new RuntimeException ( io.getMessage() );
                  }
              }
              
              if ( oin != null ) {
                  try {
                      oin.close();
                  }
                  catch ( IOException io ) {
                      throw new RuntimeException ( io.getMessage() );
                  }
              }
              
         }
         
      
         
    }
    
    private static URL getLicenseFileURL() 
    {
    	URL ret = null;
    	ret = License.class.getResource ( LICENSE_FILE_URL );
    	return ret;
    }
    
    private static URL getSignatureFileURL() 
    {
    	URL ret = null;
    	ret = License.class.getResource ( SIGNATURE_FILE_URL );
    	return ret;
    }

     
    
     /**
      *Set the digest algorithm name.
      *@param name The name of the alg to use.
      */
      
    public static void setDigestName ( String name ) 
    {
         digestName_ = name;
    }
    
     /**
      *Set the console for debugging and diagnostics.
      *@param console The console
      */
      
    public static void setConsole ( Console console )
    {
          console_ = console;
    } 
    
     /**
      *Create a license instance.
      *
      *@param key The key data to be used for checking the license signature.
      *@exception CorruptLicenseException If the license file appears to be
      *corrupt.
      *@exception IOException If the file(s) could not be read.
      *@return License The license instance.
      *
      */
      
    public static License createLicense ( String key )
    throws CorruptLicenseException, IOException
    {
    	URL licenseFileURL = getLicenseFileURL();
    	URL sigFileURL = getSignatureFileURL();
        assertIntegrity ( licenseFileURL , sigFileURL , key );
        
       	return createInformationalLicense();
    }
    
    /**
     * Create a purely information license instance based on the URL.
     * An informational license is one without integrity guarantees.
     * Instances are useful mostly for showing information to the user.
     * @return
     * @throws IOException
     */
    public static License createInformationalLicense () throws IOException
    {
    	License ret = null;
		InputStream in =  null;
		Properties properties = new Properties();
		URL licenseFileURL = getLicenseFileURL();
        
		try {
			in = licenseFileURL.openStream();
			properties.load ( in );
			ret = createLicense ( properties );
		}
		finally {
			if ( in != null )
				in.close(); 
		}
    	return ret;
    }
    
    protected License ( Properties properties )
    {
        properties_ =  properties;
    }
    
    protected String getProperty ( String name )
    {
    	return properties_.getProperty ( name );
    }
    
    
     /**
      *Checks if the local host is allowed to run a product of the
      *specified version.
      *@param productName The name of the product.
      *
      *@return boolean True iff the license file indicates that the local 
      *host is allowed to run the software.
      *@exception CorruptLicenseException If tampering is detected.
      *@exception InvalidMachineLicenseException If the IP is wrong.
      *@exception ExpiredLicenseException If the license has expired.
      *@exception UnknownHostException If the IP could not be determined.
      *@exception WrongProductLicenseException If the wrong product for this license.
      *
      */
      
    public void checkLocalHost ( String productName )
    throws UnknownHostException, CorruptLicenseException, 
	InvalidMachineLicenseException, ExpiredLicenseException, 
	WrongProductLicenseException
    {
          //boolean ret = true;
          

          if ( ! productName.equals ( 
                  properties_.getProperty ( 
                  PRODUCT_NAME_PROPERTY_NAME ) ) ) {
              //if the license is for another product, then surely it has no
              //clearing for this product's use
              printMsg ( "License is not for product " + productName );
              //ret = false; 
              throw new WrongProductLicenseException ( 
			  "License is not for product " + productName );
          }
          
          String expiresAsString =  getProperty ( EXPIRES_PROPERTY_NAME );
          if ( expiresAsString != null ) {
        	  long expires = Long.parseLong ( expiresAsString );
        	  Date expiryDate = new Date ( expires );
              Date now = new Date();
              if ( now.after ( expiryDate ) ) 
            	  throw new ExpiredLicenseException ( 
            			  "THIS LICENSE HAS EXPIRED!!!\n" +
            			  "IT IS ILLEGAL TO CONTINUE USING THIS SOFTWARE WITHOUT A VALID LICENSE\n" +
            			  "PLEASE CONTACT SALES@ATOMIKOS.COM TO GET SUCH A LICENSE"
            	  );
          }
        
			
		  String owner = getProperty ( OWNER_PROPERTY_NAME );
		  if ( owner == null ) owner = "unknown";
		  sendForm ( productName , owner );
         
    }
    
    /**
     * Print information about the license to the 
     * supplied output.
     * @param out The output.
     */
    
    public  void printInfo ( PrintWriter out )
    {
		
		String productName = properties_.getProperty ( 
						  PRODUCT_NAME_PROPERTY_NAME );
		out.println ( "LICENSE INFORMATION");
		out.println ( "PRODUCT: " + productName );
		
		Enumeration licenseProps = properties_.propertyNames();
		
		while ( licenseProps.hasMoreElements() ) {
			String name = ( String ) licenseProps.nextElement();
			String value = properties_.getProperty ( name );
			if ( name.startsWith( ( "feature.") )) {
				//this property is a product feature
				name = name.substring ( "feature.".length() );
				out.println ( "ENFORCED FEATURE: " );
				out.println ( name );
				out.println ( "WITH VALUE: " + value );
				
			}
		}
		
		long expires = Long.parseLong ( getProperty ( EXPIRES_PROPERTY_NAME ));
		Date expiryDate = new Date ( expires );
		Date now = new Date();
		if ( now.after ( expiryDate ) )  {
			
			out.println ( "THIS LICENSE HAS EXPIRED!!!");
			out.println ( "IT IS ILLEGAL TO CONTINUE USING THIS SOFTWARE WITHOUT A VALID LICENSE");
			out.println ( "PLEASE CONTACT SALES@ATOMIKOS.COM TO GET SUCH A LICENSE");
			
		}
		else {
			out.println ( "LICENSE TYPE: TIME-LIMITED LICENSE" );
			out.println ( "EXPIRES: " + expiryDate );
		}
		
		
    }
    
    /**
     * Filter out any license-bound product features.
     * @param productFeatures The product features 
     * in the form of Properties. Any license-bound
     * properties will be set during this method.
     */
    public void filterProductFeatures ( Properties productFeatures )
    {
    	Enumeration licenseProps = properties_.propertyNames();
    	while ( licenseProps.hasMoreElements() ) {
    		String name = ( String ) licenseProps.nextElement();
    		String value = properties_.getProperty ( name );
    		if ( name.startsWith( ( "feature.") )) {
    			//this property is a product feature
    			//so retrieve it and set in into the feature props
    			name = name.substring ( "feature.".length() );
    			productFeatures.setProperty ( name , value );
    			printMsg ( "WARNING: enforcing license-specific feature " + 
    				name + " with value " + value );
    		}
    	}
    }
    
	protected void sendForm ( String productName , String ownerName )
	{
		URL url = null;
		HttpURLConnection conn = null;
		PrintStream out = null;
		DataInputStream in = null;
		String line = null;
     
		try {
			String hostName = java.net.InetAddress.getLocalHost().getHostName();
			String ip = java.net.InetAddress.getLocalHost().getHostAddress();
			url = new URL ( REGISTRATION_URL );
			conn = ( HttpURLConnection ) url.openConnection();
			conn.setConnectTimeout ( REGISTRATION_TIMEOUT );
			conn.setDoOutput ( true );
			conn.setRequestMethod ( "POST" );
			conn.setRequestProperty ( "Content-Type", 
				  "application/x-www-form-urlencoded" );
			conn.connect();
			out = new PrintStream ( conn.getOutputStream() );
			out.print (
				"PRODUCT=" + productName + "&" +	
				"OWNER=" + ownerName +
				"HOST=" + hostName + "&" +
				"IP=" + ip + "&"
				 );
			out.close();	
			in = new DataInputStream ( conn.getInputStream() );
			while ( ( line = in.readLine() ) != null ) {
			
			}
			in.close();
		
		}
		catch ( Throwable e ) {
			//ignore: sending is best-effort
		}
	
	}
}
