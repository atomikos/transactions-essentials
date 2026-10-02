package client;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.atomikos.remoting.taas.RestTransactionServiceImp;
import com.atomikos.remoting.taas.TransactionProvider;

 /**
  * Provided for future demos - does not work yet in this release.
  *
  */
public class RestTransactionServiceInstance {


	private static final String COORDINATOR_BASE_URL = "http://localhost:9000/";
	
	
	public static void main(String... args) throws Exception {
		List<Object> providers = new ArrayList<>();
		providers.add(new JacksonJsonProvider());
		providers.add(new TransactionProvider());
		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		sf.setProviders(providers);
		
		RestTransactionServiceImp restAcidTxService = new RestTransactionServiceImp();
		restAcidTxService.init();
		sf.setResourceProvider(new SingletonResourceProvider(restAcidTxService));
		sf.setAddress(COORDINATOR_BASE_URL);
		sf.create();
		
	}
}
