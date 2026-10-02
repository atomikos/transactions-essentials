package inventory;


import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import com.atomikos.remoting.taas.TransactionProvider;
import com.atomikos.remoting.twopc.AtomikosRestPort;
import com.atomikos.remoting.twopc.ParticipantsProvider;

@Component
public class JerseyConfig extends ResourceConfig
{
	
   public JerseyConfig()
   {
	   register(ParticipantsProvider.class);
	   register(TransactionProvider.class);
       register(AtomikosRestPort.class);
   }
}
