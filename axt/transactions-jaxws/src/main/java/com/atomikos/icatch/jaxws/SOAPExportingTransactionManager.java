package com.atomikos.icatch.jaxws;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.atomikos.icatch.RollbackException;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * An interface for explicit export of a transaction.
 * 
 * 
 * 
 */
public interface SOAPExportingTransactionManager
{
    /**
     * Extract extent information from a returned SOAP message. This method
     * returns nothing because the extraction process needs to do all the
     * construction and addition of the remote participant proxies based on the
     * returned URIs. <b>This method does not need or establish any thread
     * associations for the transaction in question.</b>
     * 
     * @param msg
     *            The message.
     * @throws SOAPException
     *             If the message could not be parsed.
     * @exception RollbackException
     *                If the transaction referred to in the message has rolled
     *                back in the meantime.
     * 
     */
    public abstract void extractExtent ( SOAPMessage msg )
            throws SOAPException, RollbackException;

    /**
     * Insert a portable propagation into an outgoing SOAP message. <b>This
     * method does not need or establish any thread associations for the
     * transaction in question.</b>
     * 
     * @param tid
     *            The identifier of the transaction for which to insert the
     *            propagation.
     * @param msg
     *            The outgoing message.
     * 
     * @exception RollbackException
     *                If the transaction has rolled back due to timeout.
     * @SOAPException For SOAP errors.
     */
    public abstract void insertPropagation ( String tid , SOAPMessage msg )
            throws RollbackException, SOAPException;
}
