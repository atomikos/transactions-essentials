package com.atomikos.icatch.msg;
 
/**
 *Copyright &copy; 2002, Guy Pardon. All rights reserved.
 *
 *An implementation of a notification thread.
 */

class NotificationThread
implements Runnable
{
    
    private TestTransport requestTransport_ , replyTransport_;
    
    private TransactionMessage msg_;
    
    
    public NotificationThread ( TestTransport requestTransport , 
            TestTransport replyTransport , TransactionMessage msg )
    {
        requestTransport_ = requestTransport;
        replyTransport_ = replyTransport;
        msg_ = msg;
        
    }
   
    
    public void run()
    {
    	
    	if ( msg_ instanceof PreparedMessage || msg_ instanceof StateMessage || msg_ instanceof ErrorMessage ) {
    		replyTransport_.replyReceived ( msg_ );
    		
    	}
    	else requestTransport_.requestReceived ( msg_ );
    	//System.out.println ( "NotificationThread: received message" );
        
    } 
    
}
