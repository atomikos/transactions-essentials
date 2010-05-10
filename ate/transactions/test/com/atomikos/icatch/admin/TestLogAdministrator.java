//$Id: TestLogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: TestLogAdministrator.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:06  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:55  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:17  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2004/10/12 13:03:34  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/02/20 17:30:59  guy
//Added Test instance.
//

package com.atomikos.icatch.admin;

 /**
  *
  *
  *A LogAdministrator implementation for test purposes.
  */

public class TestLogAdministrator implements LogAdministrator
{
    private LogControl control_;
    //the logcontrol
    
    public TestLogAdministrator()
    {
        control_ = null; 
    } 
    
     /**
      *Get the log control instance. 
      *@return LogControl The instance obtained through 
      *registerLogControl. This will be null after deregisterLogControl!
      */
      
    public LogControl getLogControl()
    {
        return control_; 
    }
    
     /**
      *@see LogAdministrator
      */
      
    public void registerLogControl ( LogControl control )
    {
          control_ = control;
    }
        
    /**
     *@see LogAdministrator
     */
      
    public void deregisterLogControl ( LogControl control )
    {
        control_ = null;
    }

}
