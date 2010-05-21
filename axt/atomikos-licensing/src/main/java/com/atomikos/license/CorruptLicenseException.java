package com.atomikos.license;

 /** 
  *Copyright &copy; 2002, Atomikos. All rights reserved.
  *
  *An exception indicating a corrupt license.
  */

public class CorruptLicenseException extends LicenseException
{
    public CorruptLicenseException()
    { 
        super();
    } 
    
    public CorruptLicenseException ( String msg )
    {
        super ( msg ); 
    }
    
}
