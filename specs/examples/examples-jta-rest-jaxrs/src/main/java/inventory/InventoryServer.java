package inventory;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.atomikos.remoting.jaxrs.TransactionAwareRestContainerFilter;
import com.atomikos.remoting.twopc.AtomikosRestPort;
import com.atomikos.remoting.twopc.ParticipantsProvider;



public class InventoryServer {

	private static final String INVENTORY_BASE_URL = "http://localhost:9001/";
	
	
	public static void main(String... args) throws Exception {
		System.setProperty("com.atomikos.icatch.log_base_name","inventory");
		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		sf.setProvider(new JacksonJsonProvider());
		sf.setProvider(new TransactionAwareRestContainerFilter());
		sf.setProvider(new ParticipantsProvider());
		sf.setResourceProvider(new SingletonResourceProvider(new InventoryResource(),true));
		sf.setResourceProvider(new SingletonResourceProvider(new AtomikosRestPort()));
		sf.setAddress(INVENTORY_BASE_URL);
		sf.create();
	}
	
}
