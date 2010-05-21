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
