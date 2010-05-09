//$Id: GenericImportingTransactionHandler.java,v 1.1.1.1 2006/10/02 15:21:12 guy Exp $
//$Log: GenericImportingTransactionHandler.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:12  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:46  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.3  2006/03/21 16:13:26  guy
//Added active recovery init parameter.
//
//Revision 1.2  2006/03/15 10:31:42  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:10  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.9  2005/11/19 16:56:21  guy
//Improved logging.
//
//Revision 1.8  2005/10/28 15:24:10  guy
//*** empty log message ***
//
//Revision 1.7  2005/09/02 08:17:01  guy
//Updated javadoc for import preference (mustUnderstand!)
//
//Revision 1.6  2005/08/30 12:51:34  guy
//Improved: added handleFault and logging.
//
//Revision 1.5  2005/08/30 07:33:27  guy
//Added handleFault method: tries to rollback transaction if any.
//
//Revision 1.4  2005/08/30 07:20:56  guy
//Refactored to improve reuse across protocols.
//
//Revision 1.3  2005/08/23 13:29:40  guy
//Added logging to configuration.
//
//Revision 1.2  2005/08/19 13:48:47  guy
//Debugged.
//
//Revision 1.1  2005/08/19 07:44:59  guy
//Added generic support for import preferences.
//
package com.atomikos.icatch.jaxws;


import java.util.Stack;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPFactory;

import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005-2009, Atomikos. All rights reserved.
 * 
 * A generic superclass for common handler features. Subclasses inherit the
 * following config parameters:
 * 
 * <table border="1">
 * <tr>
 * <td><b>importPreference</b></td>
 * <td>This parameter indicates the desired behaviour of the handler. The
 * possible values are similar to the standard J2EE(TM) transaction attributes
 * (i.e.: Mandatory, Required, RequiresNew, Supports, NotSupported, Never). For
 * instance, if this parameter is set to <b>Mandatory</b> then every incoming
 * SOAP message must have transaction headers, or the handler will reject the
 * message by generating a SOAPFaultException. The default value is Required.
 * <p>
 * <b>NOTE: the values of this parameter are case sensitive!</b>
 * <p>
 * It is important to realize that the preferences that ignore the transaction
 * headers of the incoming messages (like RequiresNew) may be impractical if the
 * client message has a transaction context header with <b>mustUnderstand</b>
 * set to true. Nevertheless, in the spirit of web service autonomy we do
 * provide these preferences: the service provider (the party that installs the
 * importing handler) is the one that decides what kind of transaction scope to
 * allow. </td>
 * </tr>
 * <tr>
 * <td><b>newTransactionTimeout</b></td>
 * <td>The timeout (in milliseconds) of <b>new</b> transactions that are
 * started by this handler. Defaults to 60000. For imported transactions, the
 * timeout is indicated in the headers of the message and limited by the
 * <b>com.atomikos.icatch.max_timeout</b> init parameter set in the transaction
 * service core.
 * 
 * </td>
 * </tr>
 * 
 * <tr>
 * <td><b>commitOnHeuristicTimeout</b></td>
 * <td>This parameter should be set to <b>true</b> or <b>false</b>. If true,
 * then heuristic timeout will lead to positive termination (confirm or commit).
 * If false, then heuristic timeout will lead to negative termination (rollback
 * or cancel). Default is false. </td>
 * </tr>
 * <tr>
 * <td><b>checkOrphans</b></td>
 * <td>This parameter should be set to <b>true</b> or <b>false</b>. If true,
 * then extra checks will be performed by the transaction service in order to
 * avoid anomalies that can be caused by lost response messages. This is a
 * unique feature only offered by Atomikos, and can be set to make non-reliable
 * messaging platforms safer from the transactional point of view. If enabled,
 * your application can safely retry failed remote calls within the same
 * transaction. If you don't retry any calls then you don't need to enable this
 * feature. Default is false. </td>
 * </tr>
 * </table>
 * 
 */

public abstract class GenericImportingTransactionHandler 
implements SOAPHandler<SOAPMessageContext>
{



    /**
     * Constant indicating the ATOMIKOS actor URI.
     */
    public static final String ATOMIKOS_ACTOR = "http://www.atomikos.com/transactions";


    /**
     * Value of the import preference parameter to indicate that a new
     * transaction should be created, even if a context is included in the
     * incoming message.
     */
    public static final String IMPORT_PREFERENCE_REQUIRES_NEW = "RequiresNew";

    /**
     * Value of the import preference parameter to indicate that a transaction
     * is required. If none is present in the message, it will be created.
     */
    public static final String IMPORT_PREFERENCE_REQUIRED = "Required";

    /**
     * Value of the import preference parameter to indicate that a transaction
     * must be present in the incoming message.
     */
    public static final String IMPORT_PREFERENCE_MANDATORY = "Mandatory";

    /**
     * Value of the import preference parameter to indicate that a transaction
     * is not allowed.
     */
    public static final String IMPORT_PREFERENCE_NEVER = "Never";

    /**
     * Value of the import preference parameter to indicate that a transaction
     * is supported but not required.
     */
    public static final String IMPORT_PREFERENCE_SUPPORTS = "Supports";

    /**
     * Value of the import preference parameter to indicate that a transaction
     * is not supported (and ignored).
     */
    public static final String IMPORT_PREFERENCE_NOT_SUPPORTED = "NotSupported";

    /**
     * Converts a string parameter value to a integer preference as needed by
     * the API.
     * 
     * @param preference
     * @return
     */
    public static int convertPreference ( String preference )
    {
        preference = preference.trim ();
        int ret = SOAPImportingTransactionManager.PROPAGATION_REQUIRED;
        if ( IMPORT_PREFERENCE_REQUIRES_NEW.equals ( preference ) )
            ret = SOAPImportingTransactionManager.PROPAGATION_REQUIRES_NEW;
        else if ( IMPORT_PREFERENCE_REQUIRED.equals ( preference ) )
            ret = SOAPImportingTransactionManager.PROPAGATION_REQUIRED;
        else if ( IMPORT_PREFERENCE_MANDATORY.equals ( preference ) )
            ret = SOAPImportingTransactionManager.PROPAGATION_MANDATORY;
        else if ( IMPORT_PREFERENCE_NEVER.equals ( preference ) )
            ret = SOAPImportingTransactionManager.PROPAGATION_NEVER;
        else if ( IMPORT_PREFERENCE_SUPPORTS.equals ( preference ) )
            ret = SOAPImportingTransactionManager.PROPAGATION_SUPPORTS;
        else if ( IMPORT_PREFERENCE_NOT_SUPPORTED.equals ( preference ) )
            ret = SOAPImportingTransactionManager.PROPAGATION_NOT_SUPPORTED;
        else {
            System.err.println ( "Unrecognized value for importPreference: " + preference
                    + " -- using default value instead." );

        }
        return ret;
    }

    protected boolean heuristic_commit = false;

   
    protected boolean orphan_check = false;

    protected int preference = SOAPImportingTransactionManager.PROPAGATION_REQUIRED;

    protected long new_transaction_timeout = 60000;
    
    //protected boolean active_recovery = false;

    protected SOAPFactory factory = null;
    
    private ThreadLocal importedTransactions;
    //keeps track of whether a transaction were imported in a request
    //needed to detect timed out transactions in handling the response
    

	
    public GenericImportingTransactionHandler () 
    {
        super ();
        importedTransactions = new ThreadLocal();
        try {
			factory = SOAPFactory.newInstance();
		} catch ( SOAPException e ) {
			String msg = "ImportingTransactionHandler: failed to create SOAPFactory";
			Stack errors = new Stack();
			errors.push ( e ); 
			Configuration.logWarning ( msg , e );
			throw new SysException ( msg ,errors );
		}
    }
    
	protected SOAPFaultException createSOAPFaultException ( String reason , QName code )
	{
        SOAPFault fault = null;
        try {
			fault = factory.createFault ( reason , code );
		} catch ( SOAPException e ) {
			throw new ProtocolException ( e );
		}
        return new SOAPFaultException ( fault );
	}
    
    protected CompositeTransaction getCompositeTransaction ()
    {
        CompositeTransaction ret = null;
        CompositeTransactionManager ctm = Configuration
                .getCompositeTransactionManager ();

        ret = ctm.getCompositeTransaction ();
        return ret;
    }

    protected boolean isRollbackOnly ( CompositeTransaction ct )
    {
        boolean ret = false;
        // CompositeTransactionManager ctm =
        // Configuration.getCompositeTransactionManager();
        // CompositeTransaction ct = ctm.getCompositeTransaction();
        ret = (ct.getState ().equals ( TxState.MARKED_ABORT ));

        return ret;
    }
    
    /**
     * Sets if heuristic timeout means
     * commit or rollback. Default is false.
     * @param value
     */
    @Resource
    public void setCommitOnHeuristicTimeout ( boolean value ) 
    {
    	Configuration.logInfo ( this + ": commitOnHeuristicTimeout = " + value );
    	this.heuristic_commit = value;
    }

    /**
     * Sets whether orphans should be
     * checked or not. Orphan transactions are pending (lost) invocations; i.e.
     * invocations whose response was lost on its way to the client side. If not
     * set, defaults to true.
     * 
     * @param value
     */
    @Resource
    public void setCheckOrphans ( boolean value ) 
    {
    	Configuration.logInfo ( this + ": checkOrphans = " + value );
    	this.orphan_check = value;
    }

    
    /**
     * Sets the timeout (in millis) of
     * newly started transactions. Defaults to 60000.
     * @param milliseconds
     */
    
    @Resource
    public void setNewTransactionTimeout ( long milliseconds ) 
    {
    	Configuration.logInfo ( this + ": newTransactionTimeout = " + milliseconds );
		
    	this.new_transaction_timeout = milliseconds;
    }
    
    /**
     * Sets the desired import preference.
     * 
     * @param importPreference
     */   
    @Resource
    public void setImportPreference ( String importPreference )
    {
    		Configuration.logInfo ( this + ": importPreference = " + importPreference );
    		this.preference = convertPreference ( importPreference );
    }
 

    /**
     * @return The preference for heuristic decisions.
     */
    
    public boolean getCommitOnHeuristicTimeout ()
    {
        return heuristic_commit;
    }


    /**
     * @return The timeout for new transactions. In millis.
     */
    
    public long getNewTransactionTimeout ()
    {
        return new_transaction_timeout;
    }

    /**
     * @return Whether or not orphan checking is enabled.
     */
    
    public boolean getCheckOrphans ()
    {
        return orphan_check;
    }

    /**
     * @return The preference for importing transactions.
     * @see SOAPImportingTransactionManager
     */
    
    public int getImportPreference ()
    {
        return preference;
    }
    
    /**
     * Checks if active transactions are recoverable. 
     * @return
     */
    
    protected abstract boolean getActiveRecovery();
    /**
     * @return The protocol-specific importing transaction manager.
     * @return
     */
    
    protected abstract SOAPImportingTransactionManager getSOAPImportingTransactionManager ();

    /**
     * Extracts/inserts the protocol-specific transaction context (if any) and
     * (re)creates a transaction in the local VM. The exact behaviour depends on
     * the import preference parameter: either the created transaction will be
     * dependent on the remote transaction (represented in the message headers)
     * or a new, independent local transaction will be created.
     * 
     * 
     * @throws ProtocolException
     * @throws SOAPFaultException
     * 
     */

    public boolean handleMessage ( SOAPMessageContext ctx ) throws ProtocolException 
    {
    		boolean ret = false;
    		Boolean outboundProperty = ( Boolean ) ctx.get ( MessageContext.MESSAGE_OUTBOUND_PROPERTY );
    		if ( !outboundProperty.booleanValue() ) {
    			ret = handleRequest ( ctx );
    		}
    		else {
    			ret = handleResponse ( ctx );
    		}
    		return ret;
    }
    
    private boolean handleRequest ( MessageContext ctx ) throws ProtocolException,
            SOAPFaultException
    {
        boolean ret = true;
        SOAPMessageContext sctx = (SOAPMessageContext) ctx;
        SOAPMessage msg = sctx.getMessage ();

        Configuration
                .logDebug ( "ImportingTransactionHandler: entering handleRequest..." );

        try {
            CompositeTransactionManager ctm = Configuration
                    .getCompositeTransactionManager ();
            if ( ctm == null ) {
                // TS NOT RUNNING
                System.err
                        .println ( "ERROR in ImportingTransactionHandler: transaction service not running!" );
                throw new ProtocolException (
                        "Transaction service is required but not running." );
            }
            CompositeTransaction ct = getSOAPImportingTransactionManager ()
                    .importTransaction ( preference, new_transaction_timeout,
                            msg, orphan_check, heuristic_commit );

            // NOTE: ct is null for certain preferences
            if ( ct != null ) {
                ctm.resume ( ct );
                importedTransactions.set ( new Boolean ( true ) );
                if ( getActiveRecovery() ) ct.getCompositeCoordinator().setRecoverableWhileActive();
            } else {
            	//in case of thread reuse: make sure to reset whatever was set before
            	importedTransactions.set ( null );
            }

        } catch ( SOAPException e ) {
            Configuration.logWarning (
                    "ImportingTransactionHandler: error in import", e );
            System.err
                    .println ( "ImportingTransactionHandler: error in import: "
                            + e.getMessage () );

            QName code = new QName (
                    "http://schemas.xmlsoap.org/soap/envelope", "Client" );
            
            throw createSOAPFaultException ( "Transaction header(s) not found"  , code );
        } catch ( PropagationException e ) {

            Configuration.logWarning (
                    "ImportingTransactionHandler: error in import", e );
            System.err
                    .println ( "ImportingTransactionHandler: error in import: "
                            + e.getMessage () );
            QName code = new QName (
                    "http://schemas.xmlsoap.org/soap/envelope", "Client" );

            throw createSOAPFaultException ( "Transaction header(s) incompatible with service preference"  , code );
        } catch ( RuntimeException e ) {
        	Configuration.logWarning ( "ImportingTransactionHandler: unexpected error in import" , e );
        	throw e;
        } finally {
            Configuration
                    .logDebug ( "ImportingTransactionHandler: handleRequest done." );
        }

        return ret;
    }

    /**
     * Inserts the necessary transaction information into the headers of the
     * outgoing response message. If an independent local transaction was
     * created (depending on the import preference) then nothing is added in the
     * message.
     * 
     * @throws ProtocolException
     * @throws SOAPFaultException
     */
    
    private boolean handleResponse ( MessageContext ctx )
            throws ProtocolException, SOAPFaultException
    {

        Configuration
                .logDebug ( "ImportingTransactionHandler: entering handleResponse..." );

        boolean ret = true;
        SOAPMessageContext sctx = (SOAPMessageContext) ctx;
        SOAPMessage msg = sctx.getMessage ();
        
        CompositeTransaction ct = getCompositeTransaction ();
        if ( ct == null ) {
            if ( importedTransactions.get() != null )
                throw new ProtocolException ( "Transaction timed out" );
            
            // do nothing if not imported

        }

        else {
            // tx was found
            String tid = ct.getTid ();
           

                try {
                    if ( !isRollbackOnly ( ct ) ) {
                        Configuration
                                .logDebug ( "ImportingTransactionHandler: doing normal termination of "
                                        + tid );
                        getSOAPImportingTransactionManager ().terminated ( tid,
                                msg, true );
                    } else {
                        Configuration
                                .logDebug ( "ImportingTransactionHandler: rollback requested of "
                                        + tid );
                        getSOAPImportingTransactionManager ().terminated ( tid,
                                msg, false );
                    }
                    // NOTE: rollback should be indicated
                    // by setRollbackOnly in the service itself!
                } catch ( RollbackException e ) {
                    // ignore of service wanted rollback!
                    if ( !isRollbackOnly ( ct ) ) {
                        throw new ProtocolException ( e );
                    }
                } catch ( Exception e ) {
                	   e.printStackTrace();
                    Configuration
                            .logWarning (
                                    "ImportingTransactionHandler: error in termination",
                                    e );
                    throw new ProtocolException ( e );
                }
           
        }

        Configuration
                .logDebug ( "ImportingTransactionHandler: handleResponse done." );

        return ret;
    }

    public boolean handleFault ( SOAPMessageContext ctx ) throws ProtocolException
    {
        Configuration
                .logDebug ( "ImportingTransactionHandler: entering handleFault..." );

        SOAPMessageContext sctx = (SOAPMessageContext) ctx;
        SOAPMessage msg = sctx.getMessage ();
        CompositeTransaction ct = getCompositeTransaction ();
        if ( ct != null ) {
            // try to rollback, to avoid pending transactions
            try {
                Configuration
                        .logDebug ( "ImportingTransactionHandler: doing rollback of "
                                + ct.getTid () );
                getSOAPImportingTransactionManager ().terminated (
                        ct.getTid (), msg, false );
            } catch ( RollbackException ok ) {
                // ignore: this is what we wanted!
            }
        }

        Configuration
                .logDebug ( "ImportingTransactionHandler: handleFault done." );
        return true;
    }
	

	public void close ( MessageContext ctx ) 
	{
		//called just when the MEP ends - NOT the end of the handler life!!!
		
	}
	
	@Override
	public String toString() 
	{
		return "GenericImportingTransactionHandler: ";
	}
}
