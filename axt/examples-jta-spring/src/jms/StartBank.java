package jms;

import java.io.FileInputStream;
import java.io.InputStream;

import jdbc.Bank;

import org.springframework.beans.factory.xml.XmlBeanFactory;

import com.atomikos.jms.extra.MessageDrivenContainer;

public class StartBank
{
    public static void main ( String[] args )
    throws Exception
    {
        InputStream is = new FileInputStream("config.xml");
        XmlBeanFactory factory = new XmlBeanFactory(is);
        Bank bank = ( Bank ) factory.getBean ( "bank" );
        //initialize the bank if needed
        bank.checkTables();

        //retrieve the pool; this will also start the pool as specified in config.xml
        //by the init-method attribute!
        MessageDrivenContainer pool = ( MessageDrivenContainer ) factory.getBean ( "messageDrivenContainer" );

        //Alternatively, start pool here (if not done in XML)
        //pool.start();

        System.out.println ( "Bank is listening for messages..." );
        
    }
}
