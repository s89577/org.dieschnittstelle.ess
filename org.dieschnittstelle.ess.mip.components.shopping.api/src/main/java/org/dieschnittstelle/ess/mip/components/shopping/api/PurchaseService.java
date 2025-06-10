package org.dieschnittstelle.ess.mip.components.shopping.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

// TODO: PAT1: this is the interface to be provided as a rest service if rest service access is used
@Path( "/shoppingcarts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PurchaseService {

	@POST //Weil nicht idempotent ist
	@Path("/{shoppingCartId}/purchase")
	void purchaseCartAtTouchpointForCustomer(@PathParam("shoppingCartId") long shoppingCartId, @QueryParam("touchpointId") long touchpointId, @QueryParam("customerId") long customerId) throws ShoppingException;
	
}
