package jms;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import jdbc.Bank;

 /**
  * A message-driven bank, implemented as a JMS message listener
  * that can be registered in an Atomikos MessageDrivenContainer.
  *
  * Withdrawal orders are expected as JMS messages with the
  * account number and amount as message content.
  * An instance of Bank is used as the underlying delegate,
  * and each message is consumed in a JTA transaction
  * managed by Atomikos Transactions.
  */

public class MessageDrivenBank
implements MessageListener
{
    private Bank bank;

    public void setBank ( Bank bank )
    {
        this.bank = bank;
    }

    private Bank getBank()
    {
        return this.bank;
    }

    public void onMessage ( Message msg )
    {
        try {
            MapMessage m = ( MapMessage ) msg;
            int account = m.getIntProperty ( "account" );
            int amount = m.getIntProperty ( "amount" );
            bank.withdraw ( account , amount );
            System.out.println ( "Withdraw of " + amount + " from account " + account );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            
            //force rollback of the transaction by throwing runtime
            throw new RuntimeException ( e.getMessage() );
        }
    }
    
}