package inventory;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;

import com.atomikos.remoting.grpc.TransactionAwareServerInterceptor;
import com.atomikos.remoting.twopc.AtomikosRestPort;
import com.atomikos.remoting.twopc.ParticipantsProvider;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class InventoryServer {

	public static void main(String[] args) throws Exception {
		System.setProperty("com.atomikos.icatch.rest_port_url", "http://localhost:8081/atomikos/");
		System.setProperty("com.atomikos.icatch.log_base_name","inventory");
		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		sf.setResourceProvider(new SingletonResourceProvider(new AtomikosRestPort()));
		sf.setProvider(new ParticipantsProvider());
		sf.setAddress("http://localhost:8081/");
		sf.create();
		
		InventoryServiceImpl bindableService = new InventoryServiceImpl();
		bindableService.setup();
		final Server server = ServerBuilder.forPort(8080)
                .addService(bindableService)
                .intercept(new TransactionAwareServerInterceptor())
                .build();

        server.start();
        server.awaitTermination();
	}
}
