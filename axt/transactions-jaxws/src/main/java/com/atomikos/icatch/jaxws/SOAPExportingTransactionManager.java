//$Id: SOAPExportingTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:12 guy Exp $
//$Log: SOAPExportingTransactionManager.java,v $
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
//Revision 1.2  2006/03/15 10:31:42  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:10  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/10/24 09:49:58  guy
//Added tid of imported tx to insertExtent parameters.
//
//Revision 1.2  2005/08/19 13:48:47  guy
//Debugged.
//
//Revision 1.1  2005/08/11 09:24:19  guy
//Moved importing and exporting interfaces here.
//
//Revision 1.5  2005/08/10 09:04:45  guy
//Added interfaces.
//
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