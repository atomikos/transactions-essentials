package payment;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.atomikos.remoting.jaxrs.TransactionAwareRestContainerFilter;
import com.atomikos.remoting.twopc.AtomikosRestPort;
import com.atomikos.remoting.twopc.ParticipantsProvider;

public class PaymentServer {
	
	private static final String PAYMENT_BASE_URL = "http://localhost:9002/";

	public static void main(String... args) throws Exception {
		System.setProperty("com.atomikos.icatch.log_base_name","payment");
		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		sf.setProvider(new JacksonJsonProvider());
		sf.setProvider(new TransactionAwareRestContainerFilter());
		sf.setProvider(new ParticipantsProvider());
		sf.setResourceProvider(new SingletonResourceProvider(new PaymentResource(),true));
		sf.setResourceProvider(new SingletonResourceProvider(new AtomikosRestPort()));
		sf.setAddress(PAYMENT_BASE_URL);
		sf.create();
	}
	
}
