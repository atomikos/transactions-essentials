//$Id: RollbackException.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: RollbackException.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:22  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.2  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//


package com.atomikos.icatch;

/**
 *
 *
 *An exception indicating that a tx has been rolled back.
 */
 
 public class RollbackException extends Exception
 {
    public RollbackException ( String msg ) 
    {
      super (msg);	
    }	
    
    public RollbackException ( ) 
    {
      super ();	
    }
 }
