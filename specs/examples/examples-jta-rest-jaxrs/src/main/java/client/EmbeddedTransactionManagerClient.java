package client;

import static javax.ws.rs.client.ClientBuilder.newClient;

import javax.transaction.RollbackException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.remoting.jaxrs.TransactionAwareRestClientFilter;
import com.atomikos.remoting.twopc.ParticipantsProvider;

import inventory.PurchaseRequest;
import payment.Payment;

public class EmbeddedTransactionManagerClient {
	static String cardno = "card10";
	
	static Client paymentClient = newClient().register(JacksonJsonProvider.class).register(ParticipantsProvider.class).register(TransactionAwareRestClientFilter.class);
	static Client inventoryClient = newClient().register(JacksonJsonProvider.class).register(ParticipantsProvider.class).register(TransactionAwareRestClientFilter.class);

	
	public static void main(String[] args) throws Exception {
		System.setProperty("com.atomikos.icatch.log_base_name","client");
		UserTransactionManager utm = new UserTransactionManager();
		utm.init();
		callBothInJtaTransaction();
		utm.close();
	}


	private static void callBothInJtaTransaction() throws Exception {
		
		UserTransactionManager utm = new UserTransactionManager();
		try {			
			utm.begin();
			pay(cardno, paymentClient);
			updateInventory(cardno, inventoryClient);
			utm.commit();
		} catch (RollbackException rb ) {
			rb.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			if (utm != null) {
				utm.rollback();
			}
		}
		
	}
	
	
	private static void pay(String cardno, Client paymentClient) {
		Payment payment = new Payment();
		payment.amount =10;
		payment.cardno = cardno;
		
		WebTarget paymentServer = paymentClient.target("http://localhost:9002/payment");
		
		paymentServer.path("pay").request(MediaType.APPLICATION_JSON)
				.buildPost(Entity.entity(payment, MediaType.APPLICATION_JSON_TYPE))
				.invoke();
	}
	
	

	private static void updateInventory(String cardno,
			Client inventoryClient) {
		PurchaseRequest purchaseRequest = new PurchaseRequest();
		purchaseRequest.cardno=cardno;
		purchaseRequest.itemId=20;
		purchaseRequest.qty=2;
		   
		WebTarget inventoryServer = inventoryClient.target("http://localhost:9001/inventory");
		inventoryServer.path("purchase").request(MediaType.APPLICATION_JSON)
					.buildPost(Entity.entity(purchaseRequest, MediaType.APPLICATION_JSON_TYPE))
					.invoke(Response.class);
		
	}

}
