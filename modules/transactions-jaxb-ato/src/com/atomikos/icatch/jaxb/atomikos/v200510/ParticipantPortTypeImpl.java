
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package com.atomikos.icatch.jaxb.atomikos.v200510;

import java.util.List;

import com.atomikos.icatch.msg.CascadeInfo;
import com.atomikos.icatch.msg.CommitMessageImp;
import com.atomikos.icatch.msg.ForgetMessageImp;
import com.atomikos.icatch.msg.HttpTransport;
import com.atomikos.icatch.msg.PrepareMessage;
import com.atomikos.icatch.msg.PrepareMessageImp;
import com.atomikos.icatch.msg.RollbackMessageImp;
import com.atomikos.icatch.msg.soap.atomikos.AtomikosHttpTransport;
import com.atomikos.icatch.system.Configuration;



/**
 * The Participant implementation.
 * 
 */

@javax.jws.WebService(name = "ParticipantPortType", serviceName = "TransactionService",
                      portName = "ParticipantPort",
                      targetNamespace = "http://www.atomikos.com/schemas/2005/10/transactions", 
                    //  wsdlLocation = "file:./resources/wsdl/atomikos/v200510/atomikos.wsdl" ,
		      endpointInterface = "com.atomikos.icatch.jaxb.atomikos.v200510.ParticipantPortType")
                      
public class ParticipantPortTypeImpl implements ParticipantPortType {

	private CascadeInfo[] extractCascadeInfo ( 
			List<CascadeInfoType> info , String ourUri )
		{
			CascadeInfo[] ret = new CascadeInfo[ info.size() - 1 ];
			int j = 0;
			for ( int i = 0 ; i < info.size() ; i++ ) {
				CascadeInfoType cit = info.get ( i );
				Configuration.logDebug ( "ParticipantBindingImp: inspecting cascadeInfo uri: " + cit.getParticipant() );
				if ( cit.getParticipant().toString().equals (ourUri)) {
					//skip ourselves
					
				}
				else {
					
					ret[j] = new CascadeInfo();
					ret[j].count = cit.getInvocationCount().intValue();
					ret[j].participant = cit.getParticipant().toString();
					j++;	
				}
			}
			return ret;
		}
		
		private int extractInvocationCount ( 
			List <CascadeInfoType> info , String ourUri )
		{
			int ret = 0;
			for ( int i = 0 ; i < info.size() ; i++ ) {
				CascadeInfoType cit = info.get ( i );
				if ( cit.getParticipant().toString().equals (ourUri)) {
					ret = cit.getInvocationCount().intValue();
				}
				
			}
			return ret;		
		}
		

 
    public void prepare(
        com.atomikos.icatch.jaxb.atomikos.v200510.PrepareMessageType req
    )
    { 
    	try
        {
            HttpTransport transport = AtomikosHttpTransport.getSingleton();
            if ( transport == null ) throw new IllegalStateException ( "Service not initialized" );
            int protocol = transport.getCommitProtocol();
            int format = transport.getFormat();
            PrepareMessage msg = null;
            String senderUri = req.getSender().getReference().toString();
            String targetUri = req.getTarget().getReference().toString();
            String senderAddress = req.getSender().getEndPoint().toString();
            String targetAddress = req.getTarget().getEndPoint().toString();
            boolean checkSiblings = true;
            if ( req.getCascadeInfo() == null || req.getCascadeInfo().size() == 0 )
            	checkSiblings = false;
            if ( checkSiblings ) {
            	List<CascadeInfoType> info = req.getCascadeInfo();
            	CascadeInfo[] siblings = extractCascadeInfo ( info , targetUri );
            	int count = extractInvocationCount ( info , targetUri );
            	msg = new PrepareMessageImp ( protocol , format , 
            		targetAddress , targetUri , 
            		senderAddress , senderUri , 
            		count , siblings );
            }
            else {
            	msg = new PrepareMessageImp ( protocol , 
            		format , targetAddress , targetUri , 
            		senderAddress , senderUri );
            }
            transport.requestReceived ( msg );
        }
        catch (RuntimeException e)
        {
            Configuration.logWarning ( "ParticipantPortImpl: error receiving prepare message" , e );
            throw e;
        }

    }


    public void rollback(
        com.atomikos.icatch.jaxb.atomikos.v200510.TransactionMessageType req
    )
    { 
		HttpTransport transport = AtomikosHttpTransport.getSingleton();
		if ( transport == null ) throw new IllegalStateException ( "Service not initialized" );
		int protocol = transport.getCommitProtocol();
		int format = transport.getFormat();    	
		String senderUri = req.getSender().getReference().toString();
		String targetUri = req.getTarget().getReference().toString();
		String senderAddress = req.getSender().getEndPoint().toString();
		String targetAddress = req.getTarget().getEndPoint().toString();      	
		RollbackMessageImp msg = new RollbackMessageImp ( 
			protocol , format , targetAddress , targetUri, 
			senderAddress , senderUri );
		transport.requestReceived ( msg );
    }


    public void commit(
        com.atomikos.icatch.jaxb.atomikos.v200510.CommitMessageType req
    )
    { 
		HttpTransport transport = AtomikosHttpTransport.getSingleton();
		if ( transport == null ) throw new IllegalStateException ( "Service not initialized" );
		int protocol = transport.getCommitProtocol();
		int format = transport.getFormat();    	
		String senderUri = req.getSender().getReference().toString();
		String targetUri = req.getTarget().getReference().toString();
		String senderAddress = req.getSender().getEndPoint().toString();
		String targetAddress = req.getTarget().getEndPoint().toString();  
		CommitMessageImp msg = null;  	
		boolean onePhase = req.isOnePhase();
		msg = new CommitMessageImp ( protocol , format , 
			targetAddress , targetUri , 
			senderAddress , senderUri , onePhase );
		transport.requestReceived ( msg );
       
    }


    public void forget(
        com.atomikos.icatch.jaxb.atomikos.v200510.TransactionMessageType req
    )
    { 
		HttpTransport transport = AtomikosHttpTransport.getSingleton();
		if ( transport == null ) throw new IllegalStateException ( "Service not initialized" );
		int protocol = transport.getCommitProtocol();
		int format = transport.getFormat();    	
		String senderUri = req.getSender().getReference().toString();
		String targetUri = req.getTarget().getReference().toString();
		String senderAddress = req.getSender().getEndPoint().toString();
		String targetAddress = req.getTarget().getEndPoint().toString();    	
		ForgetMessageImp msg = new ForgetMessageImp (
			protocol , format , targetAddress , targetUri, 
			senderAddress , senderUri );
		transport.requestReceived ( msg );
    }

}
