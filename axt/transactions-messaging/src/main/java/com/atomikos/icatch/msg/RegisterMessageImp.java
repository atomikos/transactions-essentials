//$Id: RegisterMessageImp.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: RegisterMessageImp.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:15  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:46  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:30  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:48  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:12  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2005/10/03 11:46:03  guy
//Added support for WS-T registration (2PC and volatile).
//
package com.atomikos.icatch.msg;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * 
 * 
 * 
 */
public class RegisterMessageImp extends AbstractMessage implements
        RegisterMessage
{

    private boolean for2PC;

    /**
     * @param protocol
     * @param format
     * @param targetAddress
     * @param targetURI
     * @param senderAddress
     * @param senderURI
     * @param for2PC
     */
    public RegisterMessageImp ( int protocol , int format ,
            Object targetAddress , String targetURI , Object senderAddress ,
            String senderURI , boolean for2PC )
    {
        super ( protocol , format , targetAddress , targetURI , senderAddress ,
                senderURI );
        this.for2PC = for2PC;
    }

    /**
     * @see com.atomikos.icatch.msg.TransactionMessage#getMessageType()
     */
    public int getMessageType ()
    {
        return TransactionMessage.REGISTER_MESSAGE;
    }

    /**
     * @see com.atomikos.icatch.msg.RegisterMessage#registerForTwo2PC()
     */
    public boolean registerForTwo2PC ()
    {
        return for2PC;
    }

}
