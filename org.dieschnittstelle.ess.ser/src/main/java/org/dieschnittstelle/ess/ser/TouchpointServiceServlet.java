package org.dieschnittstelle.ess.ser;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.dieschnittstelle.ess.utils.Utils.*;

import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;

public class TouchpointServiceServlet extends HttpServlet {

	protected static Logger logger = org.apache.logging.log4j.LogManager
			.getLogger(TouchpointServiceServlet.class);

	public TouchpointServiceServlet() {
		show("TouchpointServiceServlet: constructor invoked\n");
	}
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {

		logger.info("doGet()");

		// we assume here that GET will only be used to return the list of all
		// touchpoints

		// obtain the executor for reading out the touchpoints
		TouchpointCRUDExecutor exec = (TouchpointCRUDExecutor) getServletContext()
				.getAttribute("touchpointCRUD");
		try {
			// set the status
			response.setStatus(HttpServletResponse.SC_OK);
			// obtain the output stream from the response and write the list of
			// touchpoints into the stream
			ObjectOutputStream oos = new ObjectOutputStream(
					response.getOutputStream());
			// write the object
			oos.writeObject(exec.readAllTouchpoints());
			oos.close();
		} catch (Exception e) {
			String err = "got exception: " + e;
			logger.error(err, e);
			throw new RuntimeException(e);
		}

	}

	/*
	 * TODO: SER3 server-side implementation of createNewTouchpoint
	 */
	@Override	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {

		// assume POST will only be used for touchpoint creation, i.e. there is
		// no need to check the uri that has been used

		// obtain the executor for reading out the touchpoints from the servlet context using the touchpointCRUD attribute
		TouchpointCRUDExecutor exec = (TouchpointCRUDExecutor) getServletContext().getAttribute("touchpointCRUD");
		try {
			// create an ObjectInputStream from the request's input stream
			ObjectInputStream ois = new ObjectInputStream(request.getInputStream());

			// read an AbstractTouchpoint object from the stream
			AbstractTouchpoint newTp = (AbstractTouchpoint) ois.readObject();
			ois.close();

			// call the create method on the executor and take its return value
			AbstractTouchpoint createdTp = exec.createTouchpoint(newTp);

			// set the response status as successful, using the appropriate
			// constant from HttpServletResponse
			response.setStatus(HttpServletResponse.SC_CREATED);

			// then write the object to the response's output stream, using a
			// wrapping ObjectOutputStream
			ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());

			// and write the object to the stream
			oos.writeObject(createdTp);
			oos.close();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/*
	 * TODO: SER4 server-side implementation of deleteTouchpoint
	 */
	@Override
	protected void doDelete(HttpServletRequest request,
						  HttpServletResponse response) {
		try {
			String path = request.getPathInfo();

			logger.info("path " + path); // z.B. "/1"

			//Wenn nur "/" oder nichts
			if(path.length()<=1){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			path = path.substring(1);	// / weg vom anfang

			int id=Integer.parseInt(path);

			logger.info("doDelete() for id " + id);

			// obtain the executor for reading out the touchpoints from the servlet context using the touchpointCRUD attribute
			TouchpointCRUDExecutor exec = (TouchpointCRUDExecutor) getServletContext().getAttribute("touchpointCRUD");

			// delete tp with id
			boolean deleted =exec.deleteTouchpoint(id);

			// set the response status as successful, using the appropriate
			// constant from HttpServletResponse
			if(deleted){
				response.setStatus(HttpServletResponse.SC_OK);
			}else{
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}

		} catch (Exception e) {
			logger.error("Error in doDelete(): ", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

	}


}
