//$Id: CorruptLicenseException.java,v 1.1.1.1 2006/10/02 15:20:56 guy Exp $
//$Log: CorruptLicenseException.java,v $
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
//Revision 1.3  2004/10/12 13:04:07  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2004/03/22 15:38:33  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.1  2004/01/29 15:54:05  guy
//*** empty log message ***
//
//Revision 1.1  2002/01/22 13:44:52  guy
//Added a License class for better checking of license conditions.
//

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
