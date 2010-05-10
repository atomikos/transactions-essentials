//$Id: TestTextMessage.java,v 1.1.1.1 2006/08/29 10:01:14 guy Exp $
//$Log: TestTextMessage.java,v $
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
//Revision 1.1  2005/01/07 17:07:31  guy
//Added tests for JMS receiver support (lightweigh MDB), and JMS queue bridging.
//
package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * 
 * 
 * 
 * 
 *
 * 
 */
public class TestTextMessage extends TestMessage implements TextMessage
{
	private String text;
    
    public String getText() throws JMSException
    {
        return text;
    }

    
    public void setText(String text) throws JMSException
    {
        this.text = text;

    }

  
    public void clearBody() throws JMSException
    {
        text = null;

    }

}
