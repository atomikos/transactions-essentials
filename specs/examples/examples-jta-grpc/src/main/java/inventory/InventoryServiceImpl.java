package inventory;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

import inventory.InventoryServiceGrpc.InventoryServiceImplBase;
import io.grpc.stub.StreamObserver;

public class InventoryServiceImpl extends InventoryServiceImplBase {
	private static final Logger LOGGER = LoggerFactory.createLogger(InventoryServiceImpl.class);

	
	InventoryJdbc inventoryJdbc = new InventoryJdbc();

	
	@Override
	public void purchase(PurchaseRequest purchaseRequest, StreamObserver<PurchaseRequest> responseObserver) {
		
		System.out.println("purchaseRequest "+purchaseRequest);
		try {
			inventoryJdbc.purchase(purchaseRequest);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("purchaseRequest end "+purchaseRequest);
		
		responseObserver.onNext(purchaseRequest);	
		responseObserver.onCompleted();
		
	}
	
	public void setup() {
		try {
			inventoryJdbc.setup();
		} catch (Exception e) {
			LOGGER.logError("logCloud not running ?", e);
		}

	}
}
