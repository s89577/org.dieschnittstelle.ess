package org.dieschnittstelle.ess.mip.components.crm.crud.api;

import java.util.List;

import org.dieschnittstelle.ess.mip.components.crm.api.CrmException;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

public interface TouchpointCRUD {

	public AbstractTouchpoint createTouchpoint(AbstractTouchpoint Touchpoint) throws CrmException;

	public AbstractTouchpoint readTouchpoint(long id);

	public List<AbstractTouchpoint> readAllTouchpoints();
	
	public AbstractTouchpoint updateTouchpoint(AbstractTouchpoint Touchpoint);
		
	public boolean deleteTouchpoint(int id);

}
