package inventory;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

@Path("/inventory")
public class InventoryResource {

	private static final Logger LOGGER = LoggerFactory.createLogger(InventoryResource.class);

	InventoryJdbc inventoryJdbc = new InventoryJdbc();


	@GET
	@Path("/stock/{itemId}")
	public Response getStock(@PathParam("itemId") int itemId) {

		try {
			int res = inventoryJdbc.getStock(itemId);
			return Response.ok().entity(res).build();
		} catch (Exception e) {
			return Response.serverError().entity(e).build();
		}

	}

	@POST
	@Path("/purchase")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response purchase(PurchaseRequest purchaseRequest) throws Exception {
			inventoryJdbc.purchase(purchaseRequest);
			return Response.ok(purchaseRequest).build();
	}

	@PostConstruct
	public void setup() {
		try {
			inventoryJdbc.setup();
		} catch (Exception e) {
			LOGGER.logError("logCloud not running ?", e);
		}
		
	}

}
