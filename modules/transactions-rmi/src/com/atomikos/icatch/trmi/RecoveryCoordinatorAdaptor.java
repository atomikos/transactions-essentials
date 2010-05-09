//$Id: RecoveryCoordinatorAdaptor.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: RecoveryCoordinatorAdaptor.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:32  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2005/08/05 15:04:23  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.6  2004/10/12 13:03:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.5  2003/09/01 15:28:04  guy
//Modified exception wrapping in init: more verbose messages.
//Added JRMP native stubs for WebLogic and JBoss compatibility.
//
//Revision 1.4  2003/08/28 05:57:11  guy
//Changed stub reconnect to ORB: not needed if RMI-IIOP import is done.
//
//Revision 1.3  2003/08/27 06:24:07  guy
//Adapted to RMI-IIOP.
//
//$Id: RecoveryCoordinatorAdaptor.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.2  2003/03/11 06:39:16  guy
//$Id: RecoveryCoordinatorAdaptor.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: RecoveryCoordinatorAdaptor.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//Revision 1.1.1.1.4.1  2003/01/29 17:20:07  guy
//Adapted to use JNDI binding instead of Naming of RMI.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//

package com.atomikos.icatch.trmi;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;


 /**
  *Copyright &copy; 2001, Atomikos. All rights reserved.
  *
  *An adaptor for converting a LOCAL coordinator's replay request
  *into one that uses an appropriate trmi stub for the local instance.
  */
  
  class RecoveryCoordinatorAdaptor implements RecoveryCoordinator, Externalizable
{
    private static  org.omg.CORBA.ORB orb_ = null;

    
      private RecoveryCoordinator coord_ ;
      private String name_;
      private ParticipantServer server_;
      private String initialContextFactory_;
      private String providerUrl_;

      private static org.omg.CORBA.ORB getORB() {
          if ( orb_ == null ) {
              orb_ = org.omg.CORBA.ORB.init ( new String[0] , null );
          }
          return orb_;
      }

      public RecoveryCoordinatorAdaptor() {
          //required for Externalizable
      }
      
      RecoveryCoordinatorAdaptor ( RecoveryCoordinator coord,
                                                   String servername, 
                                                   String initialContextFactory ,
                                                   String providerUrl,
                                                   ParticipantServer server ) 
      {
          coord_ = coord;	
          name_ = servername;
          server_ = server;
          initialContextFactory_ = initialContextFactory;
          providerUrl_ = providerUrl;
      }

           
      /**
       *Transforms a replay request on behalf of a local coordinator into
       *a request with the appropriate proxy for the local instance.
       *Needed because the local coordinator does not know 
       *how to make its proxy.
       *
       *@param localCoordinator The local coordinator making the request.
       */
       
      public Boolean replayCompletion ( Participant localCoordinator )
        throws IllegalStateException
      {
          
          CompositeCoordinator c = 
      	    ( CompositeCoordinator ) localCoordinator;
      	//construct a participant proxy for the argument
      	//and delegate to the coordinator with proxy 
          	ParticipantProxy p = 
          	      new ParticipantProxy ( 
          	      name_ ,  initialContextFactory_ , 
          	      providerUrl_ , server_ ,c );
          	      
          	                                
          	return coord_.replayCompletion ( p );
      }
      
       /**
        *@see RecoveryCoordinator
        */
        
      public String getURI()
	{
		return coord_.getURI(); 
	}

      public void readExternal ( ObjectInput in ) throws IOException , ClassNotFoundException {
          
          coord_ = ( RecoveryCoordinator ) in.readObject();
          name_ = ( String ) in.readObject();
          server_ = ( ParticipantServer )
              javax.rmi.PortableRemoteObject.narrow (
              in.readObject() , ParticipantServer.class );
          initialContextFactory_ = ( String ) in.readObject();
          providerUrl_ = ( String ) in.readObject();

          //the following procedure is required to make the RMI-IIOP
          //stub able to communicate with the server in case of
          //JRMP import
          try {
              org.omg.CORBA.ORB orb = getORB();
              ( ( javax.rmi.CORBA.Stub  ) server_ ).connect ( orb );
          }
          catch ( java.rmi.RemoteException alreadyConnected ) {
              //happens if import is being done in RMI-IIOP application
          }
          catch ( ClassCastException ce ) {
              //normal on weblogic?
          }
          
          
      }

      public void writeExternal ( ObjectOutput out ) throws IOException
      {
          out.writeObject ( coord_ );
          out.writeObject ( name_ );
          out.writeObject ( server_ );
          out.writeObject ( initialContextFactory_ );
          out.writeObject ( providerUrl_ );
      }
          

  }
