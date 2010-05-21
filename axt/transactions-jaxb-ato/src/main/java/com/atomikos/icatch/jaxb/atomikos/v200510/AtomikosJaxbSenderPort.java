
package com.atomikos.icatch.jaxb.atomikos.v200510;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import com.atomikos.icatch.msg.CascadeInfo;
import com.atomikos.icatch.msg.CommitMessage;
import com.atomikos.icatch.msg.ErrorMessage;
import com.atomikos.icatch.msg.ForgetMessage;
import com.atomikos.icatch.msg.HttpTransport;
import com.atomikos.icatch.msg.IllegalMessageTypeException;
import com.atomikos.icatch.msg.PrepareMessage;
import com.atomikos.icatch.msg.PreparedMessage;
import com.atomikos.icatch.msg.ReplayMessage;
import com.atomikos.icatch.msg.RollbackMessage;
import com.atomikos.icatch.msg.SenderPort;
import com.atomikos.icatch.msg.StateMessage;
import com.atomikos.icatch.msg.TransactionMessage;
import com.atomikos.icatch.msg.TransportException;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005-2007, Atomikos. All rights reserved.
 * 
 * A sender strategy for the Axis transport.
 * By factoring the sending logic out, the transport can be 
 * initialized by the core, without requiring JAXWS classes
 * in the core classpath. This facilitates integration into
 * other appservers.
 *
 * 
 */
public class AtomikosJaxbSenderPort implements SenderPort
{

	private static final String ATO_NS_URI = "http://www.atomikos.com/schemas/2005/10/transactions";
	private static final String ATO_WSDL_URI = ATO_NS_URI + "/atomikos.wsdl";
	
	private <T> T getPort ( String address,
			String wsdlUrlAsString,
			String nsUri , String serviceName , String portName , 
			Class <T> portType ) throws MalformedURLException, SOAPException 
	{
		T ret = null;
		URL wsdlUrl = new URL ( wsdlUrlAsString );
		Service s = Service.create(wsdlUrl , new QName(nsUri, serviceName));
		QName portQName = new QName ( nsUri , portName );
		ret = s.getPort ( portQName , portType );
		BindingProvider bp = ( BindingProvider ) ret;
		((BindingProvider) ret).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY , address );
		
		return ret;
	}
	
	public AtomikosJaxbSenderPort ( HttpTransport transport )
	{
		transport.setSenderPort ( this );
	}

	private CoordinatorPortType getCoordinatorPort ( String portAddress ) throws MalformedURLException, SOAPException
	{
		return getPort ( portAddress , ATO_WSDL_URI , ATO_NS_URI , 
				"TransactionService" , "CoordinatorPort" , CoordinatorPortType.class );
	}
	
	private ParticipantPortType getParticipantPort ( String portAddress ) throws MalformedURLException, SOAPException
	{
		return getPort ( portAddress , ATO_WSDL_URI , ATO_NS_URI , 
				"TransactionService" , "ParticipantPort" , ParticipantPortType.class );
	}
	

	private ParticipantPortType getParticipantPort ( Object address ) 
	throws MalformedURLException, SOAPException
	{
		ParticipantPortType ret = null;
		String addr = ( String ) address;
		ret = getParticipantPort ( addr );
		
		return ret;	
	}
    
	private CoordinatorPortType getCoordinatorPort ( Object address ) throws MalformedURLException, SOAPException
	{
		CoordinatorPortType ret = null;
		String addr = ( String ) address;
		ret = getCoordinatorPort ( addr );
		return ret;
	}
    
	private AddressType getAddressType ( Object endpoint , String reference ) 
	{
		AddressType ret = null;
		String address = ( String ) endpoint;
		ret = new AddressType();
		ret.setEndPoint ( address );
		ret.setReference ( reference );
		return ret;
	}

	

	/**
	 * @see com.atomikos.icatch.msg.AbstractTransport#send(com.atomikos.icatch.msg.TransactionMessage)
	 */
	public void send ( TransactionMessage msg ) throws TransportException, IllegalMessageTypeException
	{
		Configuration.logDebug ( "Transport: about to send msg " + msg );
		try
		{
			AddressType sender = getAddressType ( msg.getSenderAddress() , msg.getSenderURI() );
			AddressType target = getAddressType ( msg.getTargetAddress() , msg.getTargetURI() );
			if ( msg instanceof PrepareMessage ) {
				PrepareMessage pmsg = ( PrepareMessage ) msg;
				PrepareMessageType toSend = new PrepareMessageType();
				boolean checkSiblings = pmsg.getCascadeInfo() != null;
				toSend.setSender ( sender );
				toSend.setTarget ( target );
				if ( checkSiblings ) {
					CascadeInfo[] info = pmsg.getCascadeInfo();
					List<CascadeInfoType> siblingInfo = toSend.getCascadeInfo();
					
					for ( int i = 0 ; i < info.length ; i++ ) {
						CascadeInfoType siblingInfoEl = new CascadeInfoType();
						siblingInfoEl.setParticipant (  info[i].participant );
						siblingInfoEl.setInvocationCount ( new BigInteger ( ""+info[i].count ) );
						siblingInfo.add ( siblingInfoEl );
					}
				}
				ParticipantPortType participant = getParticipantPort ( msg.getTargetAddress() );
				participant.prepare ( toSend );
			}
			else if ( msg instanceof CommitMessage ) {
				CommitMessage cmsg = ( CommitMessage ) msg;
				CommitMessageType toSend = new CommitMessageType();
				toSend.setSender ( sender );
				toSend.setTarget ( target );
				if ( cmsg.isOnePhase() ) {
					toSend.setOnePhase ( true );
				}
				ParticipantPortType participant = getParticipantPort ( msg.getTargetAddress() );
				participant.commit ( toSend );
			}
			else if ( msg instanceof RollbackMessage ) {
				RollbackMessage rmsg = ( RollbackMessage ) msg;
				TransactionMessageType toSend = new TransactionMessageType();
				toSend.setSender ( sender );
				toSend.setTarget ( target );
				ParticipantPortType participant = getParticipantPort ( msg.getTargetAddress() );
				participant.rollback ( toSend );
			}
			else if ( msg instanceof ForgetMessage ) {
				ForgetMessage fmsg = ( ForgetMessage ) msg;
				TransactionMessageType toSend = new TransactionMessageType();
				toSend.setSender ( sender );
				toSend.setTarget ( target );
				ParticipantPortType participant = getParticipantPort ( msg.getTargetAddress() );
				participant.forget ( toSend );           	
			}
			else if ( msg instanceof StateMessage ) {
				StateMessage smsg = ( StateMessage ) msg;
				StateMessageType toSend = new StateMessageType();
				toSend.setSender ( sender );
				toSend.setTarget ( target );
				toSend.setCommitted ( smsg.hasCommitted() );

				CoordinatorPortType coordinatorPort = getCoordinatorPort ( msg.getTargetAddress() );
				coordinatorPort.participantStateNotification ( toSend ); 
            	
			}
			else if ( msg instanceof PreparedMessage ) {
				PreparedMessage pmsg = ( PreparedMessage ) msg;
				PreparedMessageType toSend = new PreparedMessageType();
				toSend.setSender ( sender );
				toSend.setTarget ( target );
				toSend.setReadOnly ( pmsg.isReadOnly() );
				CoordinatorPortType coordinator = getCoordinatorPort ( msg.getTargetAddress() );
				coordinator.prepared ( toSend );
			}
			else if ( msg instanceof ReplayMessage ) {
				TransactionMessageType toSend = new TransactionMessageType();
				toSend.setSender ( sender );
				toSend.setTarget ( target );
				CoordinatorPortType coordinator = getCoordinatorPort ( msg.getTargetAddress() );
				coordinator.replayCompletion ( toSend );
			}
			else if ( msg instanceof ErrorMessage ) {
				ErrorMessage emsg = ( ErrorMessage ) msg;
				ErrorMessageType toSend = new ErrorMessageType();
				toSend.setSender ( sender );
				toSend.setTarget ( target );
				toSend.setErrorCode ( new BigInteger ( ""+emsg.getErrorCode() ) );
				CoordinatorPortType coordinator = getCoordinatorPort ( msg.getTargetAddress() );
				coordinator.participantErrorNotification ( toSend );
			}
			Configuration.logInfo ( "Transport: msg sent: " + msg );
		}
		catch (MalformedURLException e)
		{
			Configuration.logWarning ( e.getMessage() , e );
			throw new TransportException ( e );
		}
		catch ( Exception e )
		{
			Configuration.logWarning ( e.getMessage() , e );
			throw new TransportException ( e );
		}
        
	} 

}
