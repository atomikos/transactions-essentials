package com.atomikos.icatch.trmi;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.Stack;
import java.util.Enumeration;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.imp.AbstractCompositeTransaction;
import com.atomikos.icatch.system.Configuration;

/**
 *Copyright &copy; 2001, Atomikos. All rights reserved.
 *
 *A proxy composite tx for passing as part of propagated context.
 *Allows remote parts of the computation to perform operations on the ancestor
 *transactions.
 */

class CompositeTransactionProxy extends AbstractCompositeTransaction 
				implements java.io.Externalizable
{
    protected CompositeTransactionServer server_ = null;
    protected CompositeCoordinatorProxy coordinator_ = null;

    private static org.omg.CORBA.ORB orb_ = null;
    
    private static org.omg.CORBA.ORB getORB() {
        if ( orb_ == null ) {
            orb_ = org.omg.CORBA.ORB.init ( new String[0] , null );
        }
        return orb_;
    }
    
	private static void printPropertiesInDebugMode ( Properties props ) 
    {
    	Enumeration enumm = props.propertyNames();
    	while ( enumm.hasMoreElements() ) {
    		String name = ( String ) enumm.nextElement();
    		Object value = props.getProperty ( name );
    		Configuration.logDebug ( "Property: " + name+" = "+value );
    	}
    }
    
    /**
     *Creates a lineage of ONLY proxy instances for the given stack.
     *@param lineage The given lineage.
     *@return Stack a stack of proxies.
     */
     
    private static final Stack createLineage ( Stack lineage, 
          CompositeTransactionServer server, CompositeCoordinatorProxy coord ) 
    {
      Stack tmp = new Stack();
      Stack remoteLineage = new Stack();
      
      while ( lineage != null && !lineage.empty() ) {
          CompositeTransaction ancestor = 
              ( CompositeTransaction ) lineage.pop();
          if ( ancestor.isLocal() ) {
                //replace by proxy
                tmp.push ( new CompositeTransactionProxy ( ancestor, server, coord ) );
                //System.err.println ( "proxy added to propagation" );
          }
          else {
              //use this one in propagation
              tmp.push ( ancestor );
          }
      }
      //here, tmp contains all ancestor proxies, but in wrong order
      while ( !tmp.empty() ) {
          remoteLineage.push ( tmp.pop() );
      }
      
      return remoteLineage;
    }

     /**
      *Required for serialization and marshalling.
      */
	
    public CompositeTransactionProxy() {}  
     
    public CompositeTransactionProxy ( CompositeTransaction tx ,
			         CompositeTransactionServer server ,
			         CompositeCoordinatorProxy coordinator )
    {
        super ( tx.getTid() , 
                  createLineage ( tx.getLineage() , server, coordinator ), 
                   tx.isSerial() );
        
        coordinator_ = coordinator;
        server_ = server;
        
        //FIX FOR 22436: include tx properties too!
        Properties props = tx.getProperties();
        if ( props != null ) {
        	Enumeration enumm = props.propertyNames();
        	while ( enumm.hasMoreElements() ) {
        		String key = ( String ) enumm.nextElement();
        		String value = ( String ) props.getProperty ( key );
        		setProperty ( key , value );
        	}
        }
       
    }   

    /**
     *@see CompositeTransaction.
     */
    
    public CompositeCoordinator getCompositeCoordinator() 
    throws SysException, UnsupportedOperationException
    {
        return coordinator_;
    }
    
    /**
     *@see CompositeTransaction
     */
     
     public RecoveryCoordinator addParticipant ( Participant participant )
        throws SysException,
	     java.lang.IllegalStateException
	 {
	 	Stack errors = new Stack();
	 	try {
	 	  server_.addParticipant ( participant , getTid() );
	 	}
	 	catch ( RemoteException re ) {
	 	  Configuration.logWarning ( "Error in proxy.addParticpant: " + re.getMessage()  );
	 	  errors.push ( re );
	 	  throw new SysException ("Remote error" , errors );	
	 	}
	 	return coordinator_.getRecoveryCoordinator();
	 }
	 
	

    
    /**
     *@see CompositeTransaction
     */
    
    public void addSubTxAwareParticipant( SubTxAwareParticipant subtxaware )
        throws SysException,
	     java.lang.IllegalStateException,
	     UnsupportedOperationException
    {
        Stack errors = new Stack();
        try {
	  server_.addSubTxAwareParticipant ( subtxaware , getTid() );
        }
        catch ( RemoteException re ) {
        	Configuration.logWarning ( "Error in proxy.addSubTxAwarePartipant: " + 
        	re.getClass().getName() + ": " + re.getMessage()  );
	  errors.push ( re );
	  //re.printStackTrace();
	  throw new SysException ("Remote error" , errors );
        }
           }
    

	public void readExternal ( ObjectInput in ) throws IOException, ClassNotFoundException
    {
    	Configuration.logDebug ( "Proxy: deserializing..." );
        try {
            lineage_ = ( Stack ) in.readObject();
            Configuration.logDebug ( "Proxy: deserialized lineage" );
        }
        catch ( ClassCastException ce ) {
        	String msg = "LINEAGE could not be read in: " +
				ce.getClass().getName() + ce.getMessage();
        	Configuration.logWarning ( msg );
            throw new ClassCastException ( msg );
        }
        try {
            tid_ = ( String ) in.readObject();
            Configuration.logDebug ( "Proxy: deserialized tid: " + tid_ );
        }
        catch ( ClassCastException ce ) {
        	String msg = "TID could not be read in: " +
			ce.getClass().getName() + ce.getMessage();
			Configuration.logWarning ( msg  );
            throw new ClassCastException ( msg );
        }
        try {
            serial_ = in.readBoolean();
            Configuration.logDebug ( "Proxy: deserialized serial flag: " + serial_ );
        } catch ( ClassCastException ce ) {
        	String msg = "SERIAL could not be read in: " +
			ce.getClass().getName() + ce.getMessage();
			Configuration.logWarning ( msg );
            throw new ClassCastException ( msg );
        }
        try {
            tag_ = ( HeuristicMessage ) in.readObject();
            Configuration.logDebug ( "Proxy: deserialized tag: " + tag_ );
        } catch ( ClassCastException ce ) {
        	String msg = "TAG could not be read in: " +
			ce.getClass().getName() + ce.getMessage();
			Configuration.logWarning ( msg  );
            throw new ClassCastException ( msg );
        }
        try {
            coordinator_ = ( CompositeCoordinatorProxy ) in.readObject();
            if ( coordinator_ != null ) 
            	Configuration.logDebug ( "Proxy: deserialized coordinator: " + coordinator_ );
        } catch ( ClassCastException ce ) {
        	String msg = "COORDINATOR could not be read in: " +
			ce.getClass().getName() + ce.getMessage();
			Configuration.logWarning ( msg );
            throw new ClassCastException ( msg );
        }
        try {
            server_ = ( CompositeTransactionServer )
            javax.rmi.PortableRemoteObject.narrow (
            in.readObject() , CompositeTransactionServer.class );
            Configuration.logDebug ( "Proxy: deserialized server" );
        } catch ( ClassCastException ce ) {
        	String msg = "SERVER could not be narrowed or read: " +
			ce.getClass().getName() + ce.getMessage();
			Configuration.logWarning ( msg );
            throw new ClassCastException ( msg );
        }
        
        try {
        		properties_ = ( Properties ) in.readObject();
        		if ( properties_ != null ) printPropertiesInDebugMode ( properties_ );
        }
        catch ( ClassCastException ce ) {
        		String msg = "PROPERTIES could not be narrowed or read: " +
    			ce.getClass().getName() + ce.getMessage();
    			Configuration.logWarning ( msg );
                throw new ClassCastException ( msg );
        }
        
        //the following procedure is required to make the RMI-IIOP
        //stub able to communicate with the server
        //in case of a JRMP import
        org.omg.CORBA.ORB orb = null;
        javax.rmi.CORBA.Stub stub = null;
        
        try {
        	Configuration.logDebug ( "Proxy: reconnecting to ORB..." );
            orb = getORB();
            stub = ( javax.rmi.CORBA.Stub  ) server_;
            ( stub ).connect ( orb );
            Configuration.logDebug ( "Proxy: reconnected to ORB" );
        }
        catch ( java.rmi.RemoteException alreadyConnected ) {
            //happens if import is done in RMI-IIOP application
            //but this is abnormal for a pure RMI application
            String msg = "Exception on reconnecting to ORB: " + 
            	alreadyConnected.getClass().getName() + 
            	alreadyConnected.getMessage();
            	Configuration.logDebug ( msg  );
        }
        catch ( ClassCastException ce ) {
			String msg = "Exception on reconnecting to ORB: " + 
					ce.getClass().getName() + 
					ce.getMessage();
			Configuration.logDebug ( msg );        	
            //ignore: on weblogic???
            //throw new ClassCastException ( "Error connecting stub to ORB: " +
            //                               ce.getClass().getName() + ce.getMessage() );
        }
        Configuration.logDebug ( "Proxy: done deserializing." );
        
    }

    public void writeExternal ( ObjectOutput out ) throws IOException
    {
    	Configuration.logDebug ( "Proxy: serializing transaction to stream..." );
    	try {
    	Configuration.logDebug ( "Proxy: writing lineage..." );
        out.writeObject ( lineage_ );
        Configuration.logDebug ( "Proxy: writing tid: " + tid_ );
        out.writeObject ( tid_ );
        Configuration.logDebug ( "Proxy: writing serial flag: " + serial_ );
        out.writeBoolean ( serial_ );
        Configuration.logDebug ( "Proxy: writing tag: " + tag_ );
        out.writeObject ( tag_ );
        Configuration.logDebug ( "Proxy: writing coordinator: " + coordinator_ );
        out.writeObject ( coordinator_ );
        Configuration.logDebug ( "Proxy: writing server..." );
        out.writeObject ( server_ );
        Configuration.logDebug ( "Proxy: writing properties..." );
        printPropertiesInDebugMode ( properties_ );
        out.writeObject ( properties_ );
    	}
    	catch ( RuntimeException e ) {
    		Configuration.logWarning ( "Error in serializing proxy: " , e );
    		throw e;
    	}
    	finally {
    		Configuration.logDebug( "Proxy: done serializing!" );
    	}
    }
}
