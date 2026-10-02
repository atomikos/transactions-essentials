package client;

import javax.transaction.RollbackException;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.remoting.grpc.TransactionAwareClientInterceptor;

import inventory.InventoryServiceGrpc;
import inventory.InventoryServiceGrpc.InventoryServiceBlockingStub;
import inventory.PurchaseRequest;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import payment.Payment;
import payment.PaymentServiceGrpc;
import payment.PaymentServiceGrpc.PaymentServiceBlockingStub;

public class Client {

	static String cardno = "card10";

	public static void main(String[] args) throws Exception {
		System.setProperty("com.atomikos.icatch.log_base_name", "client");
		System.setProperty("com.atomikos.icatch.rest_port_url", "http://example.com/atomikos");
		TransactionAwareClientInterceptor clientInterceptor = new TransactionAwareClientInterceptor();
		
		PaymentServiceBlockingStub paymentBlockingStub = PaymentServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build()).withInterceptors(clientInterceptor);
		
		InventoryServiceBlockingStub inventoryBlockingStub = InventoryServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build()).withInterceptors(clientInterceptor);
		
		UserTransactionManager utm = new UserTransactionManager();
		utm.init();
		
		try {
			utm.begin();
			pay(cardno, paymentBlockingStub);
			updateInventory(cardno, inventoryBlockingStub);
			utm.commit();
		} catch (RollbackException rb) {
			rb.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			if (utm != null) {
				utm.rollback();
			}
		} finally {
			utm.close();
		}
		utm.close();
	}


	private static void pay(String cardno, PaymentServiceBlockingStub blockingStub) {
		Payment payment = Payment.newBuilder().setCardno(cardno).setAmount(100).build();
		try {
			Payment result = blockingStub.pay(payment);
			System.out.println(result);
		} catch (StatusRuntimeException e) {
			e.printStackTrace();
			return;
		}

	}

	private static void updateInventory(String cardno, InventoryServiceBlockingStub inventoryBlockingStub) {
		PurchaseRequest request = PurchaseRequest.newBuilder().setCardno(cardno).setItemId(12).setQty(1).build();
		try {
			PurchaseRequest purchase = inventoryBlockingStub.purchase(request);
			System.out.println(purchase);
		} catch (StatusRuntimeException e) {
			e.printStackTrace();
			return;
		}

	}
}
