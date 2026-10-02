package payment;

import io.grpc.stub.StreamObserver;
import payment.PaymentServiceGrpc.PaymentServiceImplBase;

public class PaymentServiceImpl extends PaymentServiceImplBase {

	PaymentJdbc repository = new PaymentJdbc();
	@Override
	public void pay(Payment request, StreamObserver<Payment> responseObserver) {
		System.out.println("pay "+request);
		
		try {
			repository.performPayment(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		responseObserver.onNext(request);
		responseObserver.onCompleted();
		System.out.println("pay end "+request);
	}
	
	
	
	
	public void setup() throws Exception {
		repository.populateDb();
	}
}
