//$Id: NotificationThread.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: NotificationThread.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:15  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:46  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:56  guy
//Import.
//
//Revision 1.2  2006/03/15 10:24:31  guy
//Corrected/improved JUnit tests.
//
//Revision 1.1.1.1  2006/03/09 14:59:19  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2005/08/05 15:03:51  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.4.1  2005/07/29 13:08:24  guy
//Completed and tested.
//
//Revision 1.1.2.1  2002/11/07 17:58:37  guy
//Updated test infrastructure.
//

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
