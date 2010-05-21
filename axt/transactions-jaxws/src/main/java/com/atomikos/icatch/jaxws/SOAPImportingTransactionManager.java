package com.atomikos.icatch.jaxws;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * An interface for explicit import of transactions.
 * 
 * 
 */
public interface SOAPImportingTransactionManager
{

    /**
     * Constant indicating that a propagation is required; if none exists it
     * will be created during import.
     */
    public static final int PROPAGATION_REQUIRED = 0;

    /**
     * Constant indicating that a propagation is mandatory; if none exists then
     * import will throw an exception.
     */
    public static final int PROPAGATION_MANDATORY = 1;

    /**
     * Constant indicating that a propagation should not be present during
     * import. If one is found then an exception will be thrown.
     */
    public static final int PROPAGATION_NEVER = 2;

    /**
     * Constant indicating that any propagation (if present) will be ignored and
     * a new transaction should be created instead.
     */
    public static final int PROPAGATION_REQUIRES_NEW = 3;

    /**
     * Constant indicating that a propagation will be used if present. If not
     * present, no transaction will be created during import.
     */
    public static final int PROPAGATION_SUPPORTS = 4;

    /**
     * Constant indicating that any propagation should be ignored during import.
     * No transaction will be created during import.
     */
    public static final int PROPAGATION_NOT_SUPPORTED = 5;

    /**
     * Extracts the portable propagation information contained in an incoming
     * SOAP message and creates a local transaction for it. This is an
     * alternative to the normal import of a portable propagation.
     * <p>
     * <b>NOTE: this method does not need nor guarantee any thread associations
     * for the transaction.</b>
     * 
     * @param preference
     *            The propagation preference, being one of the predefined
     *            PROPAGATION_* values.
     * @param newTransactionTimeout
     *            Timeout (in millis) of new transactions (that may or may not
     *            be created according to the preference parameter).
     * @param msg
     *            The incoming SOAP message.
     * @param orphancheck
     *            True if orphan checks need to be enabled.
     * @param heur_commit
     *            True if heuristic means commit. False otherwise.
     * @return CompositeTransaction The new transaction, or null if none was
     *         created.
     * @throws SOAPFaultException
     *             If the message header could not be parsed.
     * @throws SOAPException
     *             On SOAP parsing errors.
     * @throws PropagationException
     *             If the message contents are incompatible with the supplied
     *             propagation preference.
     */
    public abstract CompositeTransaction importTransaction ( int preference ,
            long newTransactionTimeout , SOAPMessage msg , boolean orphancheck ,
            boolean heur_commit ) throws SOAPFaultException, SOAPException,
            PropagationException;

    /**
     * 
     * Terminates the local work and inserts the extent information into a SOAP
     * message. An extent is only inserted if the local work was done in the
     * transaction propagation context of the caller (this also depends on the
     * preference with which the import was done).
     * 
     * <p>
     * <b>NOTE: this method does not need nor guarantee any thread associations
     * for the transaction.</b>
     * 
     * @param tid
     *            The id of the transation from which to take the extent.
     * 
     * @param msg
     *            The SOAP message, which is about to be returned to the remote
     *            client.
     * @param commit
     *            True iff the invocation had no errors. Implies that the local
     *            subtx is committed. <b>If false, then the local work will be
     *            rolled back and NO extent will be added to the response
     *            message.</b>
     * @exception SysException
     *                Unexpected error.
     * @exception RollbackException
     *                If the transaction has timed out.
     * 
     */
    public abstract void terminated ( String tid , SOAPMessage msg ,
            boolean commit ) throws SysException, RollbackException;
}
