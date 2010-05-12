//$Id: CreateLicense.java,v 1.1.1.1 2006/10/02 15:21:29 guy Exp $
//$Log: CreateLicense.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:29  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:51  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:41  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:04  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:47  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2004/03/25 14:20:28  guy
//Added support for license-limited features
//
//Revision 1.1.2.3  2004/03/17 17:48:03  guy
//Added support for developer license
//
//Revision 1.1.2.2  2004/01/29 15:55:32  guy
//Improved license framework.
//
//Revision 1.1.2.1  2003/05/07 09:42:02  guy
//Re-added files to this module; they seemed to be gone.
//
//Revision 1.1.1.1  2002/01/29 13:02:14  guy
//Vendor-specific tools
//

package com.atomikos.vendor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;

import com.atomikos.license.License;
 
 /**
   *Copyright &copy; 2002, Guy Pardon. All rights reserved.
   *
   *A simple command tool for creating new license files.
   */

public class CreateLicense
{
    
    private static String digestName_ = "SHA";
    //name of algorithm to compute digest
    
    
    
     /**
      *Write the files for the license.
      *@param baseDir Where to put the files.
      *@param key The key to generate the signature with.
      *@param prop The license properties.
      *@exception IOException On IO errors.
      *@exception NoSuchAlgorithmException If the signature alg is not found.
      */
      
    private static void writeFiles ( String baseDir , String key, 
                                               Properties prop )
                                               throws 
                                               IOException, 
                                               NoSuchAlgorithmException
    {
         DigestOutputStream dout = null;
         ObjectOutputStream oout = null;
         try {
        	  if ( !baseDir.endsWith( java.io.File.separator ) ) baseDir = baseDir + java.io.File.separator;
              FileOutputStream out = new FileOutputStream ( baseDir + License.LICENSE_FILE_BASE_NAME + ".prp" );
              MessageDigest md = MessageDigest.getInstance ( digestName_ );
              dout = new DigestOutputStream ( out , md );
              prop.store ( dout , "Atomikos license file - do NOT edit!" );
              
              //update digest with key
              md.update ( key.getBytes() );
              
              //next, store digest in separate file
              out = new FileOutputStream ( baseDir + License.LICENSE_FILE_BASE_NAME + ".sig" );
              oout = new ObjectOutputStream ( out );
              oout.writeObject ( md.digest() );
         }
         finally {
              if ( dout != null )
                  dout.close();
              if ( oout != null )
                  oout.close();
         }
    }
    
    private static Properties createBaseProperties ( String owner , String productName , 
        	Properties features )
        {
            Properties ret = new Properties();
            ret.setProperty ( License.PRODUCT_NAME_PROPERTY_NAME , productName );
            ret.setProperty ( License.OWNER_PROPERTY_NAME , owner );
    		Enumeration featureNames = features.propertyNames();
    		while ( featureNames.hasMoreElements() ) {
    			String name =  ( String ) featureNames.nextElement();
    			ret.setProperty ( "feature." + name , features.getProperty ( name ) );
    		}
            return ret;
        }
    
    private static Properties createBaseProperties ( String productName , 
    	Properties features )
    {
        return createBaseProperties ( "Atomikos" , productName, features);
    }
    
	/**
	 *Create a new evaluation license that does NOT limit the hosts.
	 *@param owner The owner.
	 *@param productName The name of the product line.
	 *@param baseName The base name of the license files.
	 *@param key The key to sign the license with.
	 *@param expiryDate The date of expiration. After this date, the license
	 *becomes invalid.
	 *@param features The features to fix, empty object if none
	 *@exception IOException On IO error.
	 *@exception NoSuchAlgorithmException If the signature could not be 
	 *generated.
	 */
    
	public static final void createEvaluationLicense ( String owner , String productName , String baseName , 
										  String key , long expiryDate , 
										  Properties features )
		throws IOException, NoSuchAlgorithmException
		{
			Properties p = createBaseProperties ( productName , features );
			if ( owner != null ) p.setProperty ( License.OWNER_PROPERTY_NAME , owner );
			p.setProperty ( License.LICENSE_TYPE_PROPERTY_NAME , 
								  License.EVAL_TYPE_PROPERTY_VALUE );
			p.setProperty ( License.EXPIRES_PROPERTY_NAME , ""+expiryDate );

			writeFiles ( baseName , key , p );
		}
    
     /**
      *Create a new license that does NOT limit the hosts.
      *@param issuer The vendor who creates the license.
      *@param productName The name of the product line.
      *@param baseName The base name of the license files.
      *@param key The key to sign the license with.
      *@param expiryDate The date of expiration.
      *@param features The features to fix in the license, emtpy if none.
      *@exception IOException On IO error.
      *@exception NoSuchAlgorithmException If the signature could not be 
      *generated.
      */
      
    public static final void createUnlimitedLicense ( String issuer , String productName , String baseName , 
                                      String key , long expiryDate ,  Properties features )
    throws IOException, NoSuchAlgorithmException
    {
        Properties p = createBaseProperties ( issuer , productName , features );
        p.setProperty ( License.LICENSE_TYPE_PROPERTY_NAME , 
                              License.UNLIMITED_TYPE_PROPERTY_VALUE );
        p.setProperty ( License.EXPIRES_PROPERTY_NAME , ""+expiryDate );
        writeFiles ( baseName , key , p );
    }
    
    /**
      *Create a new license that limits the hosts to the given domain.
      *@param owner The owner.
      *@param productName The name of the product line.
      *@param baseName The base name of the license files.
      *@param key The key to sign the license with.
      *@param expiryDate When does the license expire?
      *@param domain The domain name for the license.
      *@param features The features to fix in the license. Empty if none.
      *@exception IOException On IO error.
      *@exception NoSuchAlgorithmException If the signature could not be 
      *generated.
      */
      
    public static final void createDomainLicense ( String owner , String productName , String baseName , 
                                       String key , long expiryDate,
                                       String domain , Properties features  ) 
    throws IOException, NoSuchAlgorithmException
    {
        Properties p = createBaseProperties ( productName , features );
        
        if ( owner != null )  p.setProperty ( License.OWNER_PROPERTY_NAME , owner );
        p.setProperty ( License.LICENSE_TYPE_PROPERTY_NAME , 
                              License.DOMAIN_TYPE_PROPERTY_VALUE );
        p.setProperty ( License.DOMAIN_PROPERTY_NAME , domain );
        p.setProperty ( License.EXPIRES_PROPERTY_NAME , ""+expiryDate );
        writeFiles ( baseName , key , p );
      
    }
    
    /**
      *Create a new license that limits the hosts to the given IP addresses.
      *@param owner The owner of the license.
      *@param productName The name of the product line.
      *@param baseName The base name of the license files.
      *@param key The key to sign the license with.
      *@param expiryDate When does the license expire?
      *@param hosts The host IP addresses for which the license is valid.
      *@param features The features to fix in the license, empty if none.
      *@exception IOException On IO error.
      *@exception NoSuchAlgorithmException If the signature could not be 
      *generated.
      */
    
    public static final void createNodeLockedLicense ( String owner , String productName , String baseName ,
                                      String key , long expiryDate ,
                                      String[] hosts , Properties features )
    throws IOException, NoSuchAlgorithmException
    {
        Properties p = createBaseProperties ( productName , features );
        if ( owner != null )  p.setProperty ( License.OWNER_PROPERTY_NAME , owner );
        p.setProperty ( License.LICENSE_TYPE_PROPERTY_NAME , 
                              License.HOST_TYPE_PROPERTY_VALUE );
        p.setProperty ( License.EXPIRES_PROPERTY_NAME , ""+expiryDate );
        
        StringBuffer buf = new StringBuffer();
        for ( int i = 0 ; i < hosts.length ; i++ ) {
            buf.append ( hosts[i] + " " );
        }
        p.setProperty ( License.HOSTS_PROPERTY_NAME ,  buf.toString() );
        writeFiles ( baseName , key , p );
      
    }
    
    /**
     * Create a new developer license, not limited in time.
     * 
     * @param owner The owner of the license.
     * @param productName The product name.
     * @param baseName The name for the files.
     * @param key The secret key for signing.
     * @param expiryDate When does the license expire?
     * @param features The features to fix.
     */
    public static final void createDeveloperLicense ( String owner , String productName , String baseName , 
	String key , long expiryDate , Properties features) 
	throws NoSuchAlgorithmException, IOException
	{
		Properties p = createBaseProperties ( productName , features );
		if ( owner != null )  p.setProperty ( License.OWNER_PROPERTY_NAME , owner );
		p.setProperty ( License.LICENSE_TYPE_PROPERTY_NAME , 
							  License.DEVELOPER_TYPE_PROPERTY_VALUE );
		p.setProperty ( License.EXPIRES_PROPERTY_NAME , ""+expiryDate );
		
		writeFiles ( baseName , key , p );		
	}
    
     /**
       *Set the name of the algorithm to use for computing digests.
       *
       *@param name The name of the alg.
       */
       
    public static void setDigestName ( String name )
    {
        digestName_ = name; 
    }
    
    public static final void main ( String[] args ) 
    {
        try {
            if ( args.length < 4 ) {
                System.err.println ( "Usage: java com.atomikos.vendor.CreateLicense [key] [productName] [baseDir] [type] [additional]" );
                System.err.println ( "where:" );
                System.err.println ( "[key] is the key to sign the license file with" );
                System.err.println ( "[productName] is the product's name WITHOUT version number" );
                System.err.println ( "[baseDir] is the target folder path of the generated files" );
                System.err.println ( "[type] is one of evaluation , developer, unlimited , domain or host" );
                System.err.println ( "[additional] is empty for unlimited type or evaluation type, " +
                                            "a valid domain name for domain type and a list of IP addresses for host type" );
                System.exit ( 1 );
            }
            
            String key = args[0];
            String productName = args[1];
            String baseName = args[2];
            String type = args[3];
            Properties p = new Properties();
            GregorianCalendar calendar = new GregorianCalendar();
        	calendar.setTime ( new Date() );
        	calendar.add ( Calendar.MONTH , 18 );
        	Date expiryDate = calendar.getTime();
        	
            if ( type.equals ( "unlimited" ) ) {
            	calendar.setTime ( new Date() );
            	calendar.add ( Calendar.MONTH , Integer.MAX_VALUE );
            	expiryDate = calendar.getTime();
                createUnlimitedLicense ( "Atomikos" , productName , baseName , key , expiryDate.getTime() , p );
            }
			else if ( type.equals ( "developer" ) ) {
							createDeveloperLicense ( null , productName , baseName , key , expiryDate.getTime() , p );
			}
            else if ( type.equals ( "evaluation" ) ) {
            	calendar.setTime ( new Date() );
            	calendar.add ( Calendar.MONTH , 1 );
            	expiryDate = calendar.getTime();
            	System.out.println ( "Evaluation valid until: " + expiryDate );
            	createEvaluationLicense ( null , productName , baseName , 
            		key , expiryDate.getTime() , p);
            }
            else if ( type.equals ( "domain" ) ) {

                createDomainLicense ( null , productName , baseName , key , expiryDate.getTime() , args[4],p );
            }
            else if ( type.equals ( "host" ) ) {
                String[] list = new String [ args.length - 4 ];
                for ( int i = 0 ; i < list.length ; i++ ) {
                    list[i] = args [ i + 4 ]; 
                }
                createNodeLockedLicense ( null , productName , baseName , key , expiryDate.getTime() , list, p );
            }
            else 
                throw new Exception ( "Unknown license type: " + type );
        }
        catch ( Exception e ) {
            e.printStackTrace(); 
        } 
    }
}
