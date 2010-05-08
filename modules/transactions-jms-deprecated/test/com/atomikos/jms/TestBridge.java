//$Id: TestBridge.java,v 1.1.1.1 2006/08/29 10:01:14 guy Exp $
//$Log: TestBridge.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:14  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
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
//Revision 1.1.1.1  2006/03/09 14:59:21  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2005/04/29 12:07:15  guy
//Added test for rollback after bridge failure.
//
package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Message;

import com.atomikos.jms.AbstractBridge;

/**
 * 
 * 
 * 
 * 
 * A bridge for testing: bridge generates failures each time.
 * 
 */
public class TestBridge extends AbstractBridge
{

    /* (non-Javadoc)
     * @see com.atomikos.jms.AbstractBridge#bridgeMessage(javax.jms.Message)
     */
    protected Message bridgeMessage(Message message) throws JMSException
    {
        throw new JMSException ( "Simulated error");
    }

}
