package client;

import static javax.ws.rs.client.ClientBuilder.newClient;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.JsonMappingException;

import com.atomikos.remoting.support.HeaderNames;
import com.atomikos.remoting.taas.TransactionProvider;
import com.atomikos.remoting.twopc.ParticipantsProvider;

import inventory.PurchaseRequest;
import payment.Payment;

/**
 * Provided for future demos - does not work yet in this release.
 *
 */

public class TaasClient {
	static String cardno = "card10";
	
	static Client restAcidTxClient = newClient().register(JacksonJsonProvider.class);
	static Client paymentClient = newClient().register(JacksonJsonProvider.class).register(ParticipantsProvider.class);
	static Client inventoryClient = newClient().register(JacksonJsonProvider.class).register(ParticipantsProvider.class);

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		
		WebTarget restAcid = restAcidTxClient.target("http://localhost:9000/atomikos");
		restAcid.register(new  TransactionProvider());
		String propagation = restAcid.path("begin")
				.queryParam("timeout", 100000)
				.request(HeaderNames.MimeType.APPLICATION_VND_ATOMIKOS_JSON)
				.buildPost(Entity.entity("", HeaderNames.MimeType.APPLICATION_VND_ATOMIKOS_JSON))
				.invoke(String.class);

	   
		String paymentParticipantExtent = pay(cardno, paymentClient, propagation);
		String inventoryParticipantExtent = updateInventory(cardno, inventoryClient, propagation);
		
		String[] extents = {paymentParticipantExtent, inventoryParticipantExtent};
		 
		Response response = restAcid.path("commit").request(HeaderNames.MimeType.APPLICATION_VND_ATOMIKOS_JSON)
		.buildPost(Entity.entity(extents, HeaderNames.MimeType.APPLICATION_VND_ATOMIKOS_JSON))
		.invoke(Response.class);
		
		System.out.println(response.getStatus());
	}

	private static String pay(String cardno, Client paymentClient, String propagation) {
		Payment payment = new Payment();
		payment.amount =10;
		payment.cardno = cardno;
		
		WebTarget paymentServer = paymentClient
				.target("http://localhost:9002/payment");
		Response paymentResponse = paymentServer.path("pay").request(MediaType.APPLICATION_JSON)
				.header(HeaderNames.PROPAGATION_HEADER_NAME, propagation)
				.buildPost(Entity.entity(payment, MediaType.APPLICATION_JSON_TYPE))
				.invoke();
		
		return paymentResponse.getHeaderString(HeaderNames.EXTENT_HEADER_NAME);
	}

	private static String updateInventory(String cardno,
			Client inventoryClient, String propagation) {
		PurchaseRequest purchaseRequest = new PurchaseRequest();
		purchaseRequest.cardno=cardno;
		purchaseRequest.itemId=20;
		purchaseRequest.qty=2;
		   
		WebTarget inventoryServer = inventoryClient.target("http://localhost:9001/inventory");
		Response inventoryResponse = inventoryServer.path("purchase").request(MediaType.APPLICATION_JSON)
				.header(HeaderNames.PROPAGATION_HEADER_NAME, propagation)
					.buildPost(Entity.entity(purchaseRequest, MediaType.APPLICATION_JSON_TYPE))
					.invoke(Response.class);
		return inventoryResponse.getHeaderString(HeaderNames.EXTENT_HEADER_NAME);
	}
}
