package payment;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

@Path("/payment")
public class PaymentResource {

	private static final Logger LOGGER = LoggerFactory.createLogger(PaymentResource.class);
	PaymentJdbc paymentJdbc = new PaymentJdbc();

	
	
	/**
	 * @see PaymentServer
	 */
	@POST
	@Path("/pay")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response pay(Payment payment) throws Exception {
		paymentJdbc.performPayment(payment);
		return Response.ok().entity(payment).build();
	}


	@PostConstruct
	public void populateDb()  {
		try {
			paymentJdbc.populateDb();
		} catch (Exception e) {
			LOGGER.logError("logCloud not running ?", e);
		}
	}

}
