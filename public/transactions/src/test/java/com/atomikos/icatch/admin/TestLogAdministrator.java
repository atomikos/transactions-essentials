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
