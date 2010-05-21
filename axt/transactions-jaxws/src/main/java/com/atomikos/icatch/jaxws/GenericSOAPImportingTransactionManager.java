package com.atomikos.icatch.jaxws;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;

import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTerminator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * A generic superclass to implement the common import logic. All the
 * transaction preference logic is implemented here, so each protocol can reuse
 * it.
 * 
 * 
 */
public abstract class GenericSOAPImportingTransactionManager implements
        SOAPImportingTransactionManager
{
	

	private Properties transactionProperties;
	//any properties that need to be set on NEW transactions
	//(imported transactions' properties are taken over too)

    protected void logIfDebug ( SOAPElement element )
    {
        Console c = Configuration.getConsole ();
        if ( c != null && c.getLevel () == Console.DEBUG ) {
            Configuration.logDebug ( element.toString () );

        }
    }
    
    /**
     * Sets the properties to use for NEW transactions.
     * @param p
     */
    protected synchronized void setProperties ( Properties p )
    {
    		this.transactionProperties = p;
    }
    
    protected synchronized Properties getProperties()
    {
    		if ( this.transactionProperties == null )
    			this.transactionProperties = new Properties();
    		return this.transactionProperties;
    }
    

    /**
     * Finds a given transaction
     * 
     * @param tid
     * @return The transaction
     * @throws RollbackException
     *             If not found
     */
    protected CompositeTransaction findTransaction ( String tid )
            throws RollbackException
    {
        CompositeTransactionManager ctm = Configuration
                .getCompositeTransactionManager ();
        CompositeTransaction ret = ctm.getCompositeTransaction ( tid );
        if ( ret == null )
            throw new RollbackException ( tid );

        return ret;
    }

    /**
     * Creates a new transaction (and suspends any existing transaction if
     * needed).
     * 
     * @param timeout
     *            The timeout in millis.
     * @return The transaction, suspended from the calling thread.
     */
    protected CompositeTransaction createNewTransaction ( long timeout )
    {
        CompositeTransactionManager ctm = Configuration
                .getCompositeTransactionManager ();
        CompositeTransaction pendingtx = ctm.getCompositeTransaction ();
        if ( pendingtx != null ) {
            // a previous invocation left a pending transaction
            // suspend it, and log a warning
            ctm.suspend ();
            Configuration
                    .logWarning ( "SOAPImportingTransactionManager: suspended PENDING transaction: "
                            + pendingtx.getTid () );
        }
        CompositeTransaction ct = ctm.createCompositeTransaction ( timeout );
        if ( transactionProperties != null ) {
        		Enumeration names = transactionProperties.propertyNames();
        		while ( names.hasMoreElements() ) {
        			String name = ( String ) names.nextElement();
        			String value = transactionProperties.getProperty ( name );
        			ct.setProperty ( name , value );
         	}
        }
        return ctm.suspend ();

    }

    /**
     * Finds the parent of a given transaction.
     * 
     * @param ct
     * @return The parent ID
     */
    protected String getParentTid ( CompositeTransaction ct )
    {
        String ret = null;

        if ( ct != null ) {

            CompositeTransaction parent = (CompositeTransaction) ct
                    .getLineage ().peek ();
            if ( parent == null )
                throw new SysException ( "No parent tx" );
            ret = parent.getTid ();
        }

        return ret;
    }

    /**
     * @see SOAPImportingTransactionManager
     */
    public CompositeTransaction importTransaction ( int preference ,
            long newTransactionTimeout , SOAPMessage msg , boolean orphancheck ,
            boolean heur_commit ) throws SOAPException, SOAPFaultException,
            PropagationException
    {

        CompositeTransaction ret = null;

        Configuration
                .logDebug ( "SOAPImportingTransactionManager: entering importTransaction with preference="
                        + preference
                        + ", newTransactionTimeout="
                        + newTransactionTimeout
                        + ", orphancheck="
                        + orphancheck + ", heur_commit=" + heur_commit );

        boolean debug = Configuration.getConsole ().getLevel () == Console.DEBUG;

        if ( debug ) {
            Configuration
                    .logDebug ( "SOAPImportingTransactionManager: extracting propagation from header: "
                            + msg.getSOAPHeader ().toString () );
            Configuration
                    .logDebug ( "SOAPImportingTransactionManager: delegating extraction to class: "
                            + this.getClass ().getName () );
        }
        SOAPHeaderElement propHeader = findPropagationHeader ( msg );
        if ( propHeader != null ) {
            if ( preference == SOAPImportingTransactionManager.PROPAGATION_NEVER )
                throw new PropagationException (
                        "A propagation was found and is not allowed by the import preference" );

            if ( preference == SOAPImportingTransactionManager.PROPAGATION_NOT_SUPPORTED ) {
                // ignore propagation context
            } else if ( preference == SOAPImportingTransactionManager.PROPAGATION_REQUIRES_NEW ) {
                ret = createNewTransaction ( newTransactionTimeout );
            } else {

                // import
                try {
                    ret = importTransactionFromHeader ( propHeader,
                            orphancheck, heur_commit );

                } catch ( PropagationException e ) {
                    throw e;
                } catch ( SOAPFaultException e ) {
                    throw e;
                } catch ( SOAPException e ) {
                    throw e;
                } catch ( Exception e ) {
                    Configuration
                            .logWarning (
                                    "SOAPImportingTransactionManager: unexpected error",
                                    e );
                    throw new SOAPException ( e );
                }
            }
        } else {
            // no propagation found
            switch ( preference ) {
            case SOAPImportingTransactionManager.PROPAGATION_MANDATORY:
                Configuration
                        .logInfo ( "SOAPImportingTransactionManager: a propagation is mandatory but not found" );
                throw new PropagationException (
                        "A propagation is mandatory but not found" );
            case SOAPImportingTransactionManager.PROPAGATION_REQUIRED:
            case SOAPImportingTransactionManager.PROPAGATION_REQUIRES_NEW:
                ret = createNewTransaction ( newTransactionTimeout );
                break;
            default:
                break;
            }
        }

        // remove processed headers of this actor
        removePropagationHeader ( msg );
        	
        if ( ret != null ) {
        		StringHeuristicMessage hmsg = new StringHeuristicMessage ( msg.getSOAPPart().toString() );
        		ret.setTag ( hmsg );
        }
        return ret;
    }

    /**
     * @see SOAPImportingTransactionManager
     */
    public void terminated ( String tid , SOAPMessage msg , boolean commit )
            throws SysException, RollbackException
    {

        CompositeTransaction current = findTransaction ( tid );
        long timeout = current.getTimeout ();
        boolean root = current.isRoot ();
        String parentTid = null;
        if ( !root ) {
            parentTid = getParentTid ( current );
        }

        CompositeTerminator terminator = current.getTransactionControl ()
                .getTerminator ();
        CompositeCoordinator coord = current.getCompositeCoordinator ();
        Extent extent = current.getTransactionControl ().getExtent ();

        try {

            // make sure that commit or rollback is called to
            // allow internal cleanup of data structures!
            // because this is an imported tx, this should be done here

            if ( commit )
                terminator.commit ();
            else
                terminator.rollback ();
        } catch ( RollbackException rb ) {
            throw rb;
        }

        catch ( Exception e ) {
            Stack errors = new Stack ();
            errors.push ( e );
            throw new SysException (
                    "Error in termination: " + e.getMessage (), errors );
        }
        if ( commit && !root ) {

            Hashtable table = extent.getRemoteParticipants ();
            Stack stack = extent.getParticipants ();

            try {
                // Participant p = ( Participant ) stack.peek();

                insertExtentHeader ( msg, tid, parentTid, coord.getCoordinatorId (),
                        coord.getTags (), table, timeout );
                Configuration
                        .logDebug ( "SOAPImportingTransactionManager: added extent for remote transaction "
                                + parentTid );
            } catch ( Exception e ) {
                Configuration.logWarning (
                        "SOAPImportingTransactionManager: error adding extent",
                        e );
                Stack errors = new Stack ();
                errors.push ( e );
                throw new SysException ( "Error inserting header: "
                        + e.getMessage (), errors );
            }

        }
    }

 

    /**
     * Finds a (protocol-specific) propagation header. This method merely
     * returns the header, without processing it.
     * 
     * @param msg
     * @return The header, or null if no propagation is present.
     * 
     */
    protected abstract SOAPHeaderElement findPropagationHeader ( SOAPMessage msg )
            throws SOAPException;

    /**
     * Does the actual import
     * 
     * @param header
     * @return The transaction as recreated from the header
     */

    protected abstract CompositeTransaction importTransactionFromHeader (
            SOAPHeaderElement header , boolean orphanCheck , boolean heurCommit )
            throws SOAPFaultException, SOAPException, PropagationException;

    /**
     * Removes the propagation header from the message.
     * 
     * @param msg
     */

    protected abstract void removePropagationHeader ( SOAPMessage msg )
            throws SOAPException;

    /**
     * Inserts the appropriate extent header into the message.
     * 
     * @param msg
     *            The message
     * @param tid
     *            The local TID of the imported tx
     * @param parentTid
     *            The parent TID at the sender
     * @param rootTid
     *            The root TID
     * @param tags
     *            The tags
     * @param table
     *            The count-> participant information
     * @param timeout
     *            The timeout in millis to wait (if applicable)
     * @throws SOAPException
     */
    protected abstract void insertExtentHeader ( SOAPMessage msg , String tid ,
            String parentTid , String rootTid , HeuristicMessage[] tags ,
            Hashtable table , long timeout ) throws SOAPException;

}
