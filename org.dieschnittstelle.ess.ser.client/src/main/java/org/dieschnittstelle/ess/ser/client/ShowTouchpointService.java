package org.dieschnittstelle.ess.ser.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.entities.crm.Address;
import org.dieschnittstelle.ess.entities.crm.StationaryTouchpoint;
import org.dieschnittstelle.ess.utils.Http;

import static org.dieschnittstelle.ess.utils.Utils.*;

public class ShowTouchpointService {

	protected static Logger logger = org.apache.logging.log4j.LogManager
			.getLogger(ShowTouchpointService.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ShowTouchpointService service = new ShowTouchpointService();
		service.run();
	}

	/**
	 * the http client that can be used for accessing the service on tomcat - note that we are usying an async client here
	 */
	private CloseableHttpAsyncClient client;
	
	/**
	 * the attribute that controls whether we are running through (when called from the junit test) or not
	 */
	private boolean stepwise = true;

	/**
	 * constructor
	 */
	public ShowTouchpointService() {

	}

	/*
	 * create the http client - this will be done for each request
	 */
	public void createClient() {
		if (client != null && client.isRunning()) {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		client = Http.createAsyncClient();
		client.start();
	}

	/**
	 * run
	 */
	public void run() {

		// 1) read out all touchpoints
		List<AbstractTouchpoint> touchpoints = readAllTouchpoints();

		// 2) delete the touchpoint after next console input
		if (touchpoints != null && touchpoints.size() > 0) {
			if (stepwise)
				step();

			deleteTouchpoint(touchpoints.get(0));
		}

		// 3) wait for input and create a new touchpoint
		if (stepwise) {
			step();
		}

		Address addr = new Address("Luxemburger Strasse", "10", "13353",
				"Berlin");
		StationaryTouchpoint tp = new StationaryTouchpoint(-1,
				"BHT Verkaufsstand", addr);

		createNewTouchpoint(tp);

		try {
			client.close();
		}
		catch (IOException ioe) {
			logger.error("got IOException trying to close client: " + ioe,ioe);
		}

		show("TestTouchpointService: done.\n");
	}

	/**
	 * read all touchpoints
	 * 
	 * @return
	 */
	public List<AbstractTouchpoint> readAllTouchpoints() {

		logger.info("readAllTouchpoints()");

		createClient();

		logger.debug("client running: {}",client.isRunning());

		// demonstrate access to the asynchronously running servlet (client-side access is asynchronous in any case)
		boolean async = false;

		try {

			// create a GetMethod

			// UE SER1: Aendern Sie die URL von api->gui 403 Forbidden wegen Filter: Nur Anfragen mit dem Header Accept-Language werdej akzeptiert
			HttpGet get = new HttpGet(
					"http://localhost:8080/api/" + (async ? "async/touchpoints" : "touchpoints"));

			//get.setHeader("Accept-Language", "de,en;q=0.9");
			logger.info("readAllTouchpoints(): about to execute request: " + get);

			// mittels der <request>.setHeader() Methode koennen Header-Felder
			// gesetzt werden

			// execute the method and obtain the response - for AsyncClient this will be a future from
			// which the response object can be obtained synchronously calling get() - alternatively, a FutureCallback can
			// be passed to the execute() method
			Future<HttpResponse> responseFuture = client.execute(get, null);
			logger.info("readAllTouchpoints(): received response future...");

			HttpResponse response = responseFuture.get();
			logger.info("readAllTouchpoints(): received response value");

			// check the response status
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				// try to read out an object from the response entity
				ObjectInputStream ois = new ObjectInputStream(response
						.getEntity().getContent());

				List<AbstractTouchpoint> touchpoints = (List<AbstractTouchpoint>) ois
						.readObject();

				logger.info("read touchpoints: " + touchpoints);

				return touchpoints;

			} else {
				String err = "could not successfully execute request. Got status code: "
						+ response.getStatusLine().getStatusCode();
				logger.error(err);
				throw new RuntimeException(err);
			}

		} catch (Exception e) {
			String err = "got exception: " + e;
			logger.error(err, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * TODO SER4
	 * 
	 * @param tp
	 */
	public void deleteTouchpoint(AbstractTouchpoint tp) {
		logger.info("deleteTouchpoint(): will delete: " + tp);

		createClient();

		logger.debug("client running: {}",client.isRunning());
		try {

			// create delete request for the api/touchpoints uri
			HttpDelete delete = new HttpDelete("http://localhost:8080/api/touchpoints/"+tp.getId());

			// execute the request, which will return a Future<HttpResponse> object
			Future<HttpResponse> responseFuture = client.execute(delete, null);

			// get the response from the Future object
			HttpResponse response = responseFuture.get();
				// log the status line
				StatusLine statusLine = response.getStatusLine();
				logger.info("StatusLine: " +  statusLine);

				// evaluate the result using getStatusLine(), use constants in
				// HttpStatus
				int statusCode = statusLine.getStatusCode();
				logger.info("StatusCode: " +  statusCode);

				/* if successful: */
				if (statusCode == HttpStatus.SC_OK) {
					logger.info("Touchpoint successfully deleted.");
				} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
					logger.warn("Touchpoint not found.");
				} else {
					logger.error("Error while deleting with Code: " + statusCode);
				}
		} catch (Exception e) {
			logger.error("Error while deleting: " + e, e);
			throw new RuntimeException(e);
		}


	}

	/**
	 * TODO SER3
	 * 
	 * fuer das Schreiben des zu erzeugenden Objekts als Request Body siehe die
	 * Hinweise auf:
	 * http://stackoverflow.com/questions/10146692/how-do-i-write-to
	 * -an-outpustream-using-defaulthttpclient
	 * 
	 * @param tp
	 */
	public AbstractTouchpoint createNewTouchpoint(AbstractTouchpoint tp) {
		logger.info("createNewTouchpoint(): will create: " + tp);

		createClient();

		logger.debug("client running: {}",client.isRunning());

		try {

			// create post request for the api/touchpoints uri
			HttpPost post = new HttpPost("http://localhost:8080/api/touchpoints");

			// create an ObjectOutputStream from a ByteArrayOutputStream - the
			// latter must be accessible via a variable
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			// write the object to the output stream
			oos.writeObject(tp); //Wandelt Objektdaten in binärformat um und schreibt in bytearrayOS
			oos.close();

			// create a ByteArrayEntity and pass it the byte array from the
			// output stream
			ByteArrayEntity bae = new ByteArrayEntity(baos.toByteArray());
			baos.close();

			// set the entity on the request
			post.setEntity(bae);

			// execute the request, which will return a Future<HttpResponse> object
			Future<HttpResponse> responseFuture = client.execute(post, new FutureCallback<HttpResponse>(){
				@Override
				public void completed(HttpResponse response) {
					logger.error("Request completed!");
				}

				@Override
				public void failed(Exception ex) {
					logger.error("Request failed: " + ex, ex);
				}

				@Override
				public void cancelled() {
					logger.warn("Request was cancelled");
				}
			});

			Thread.sleep(200);

			// get the response from the Future object
			HttpResponse response = responseFuture.get();
			try {
				// log the status line
				StatusLine statusLine = response.getStatusLine();
				logger.info("StatusLine: " +  statusLine);

				// evaluate the result using getStatusLine(), use constants in
				// HttpStatus
				int statusCode = statusLine.getStatusCode();
				logger.info("StatusCode: " +  statusCode);

				/* if successful: */
				if(statusCode == HttpStatus.SC_CREATED) {
					// create an object input stream using getContent() from the
					// response entity (accessible via getEntity())
					HttpEntity responseEntity = response.getEntity();
					ObjectInputStream ois = new ObjectInputStream(responseEntity.getContent());

					// read the touchpoint object from the input stream
					AbstractTouchpoint touchpoint = (AbstractTouchpoint) ois.readObject();
					ois.close();
					logger.info("created new touchpoint: " + touchpoint);

					// return the object that you have read from the response
					return touchpoint;

				}else {
					logger.error("Request failed with status: " + statusCode);
				}
			} catch (Exception e) {
				logger.error("Error reading response: " + e, e);
			}

			return null;
		} catch (Exception e) {
			logger.error("got exception: " + e, e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * 
	 * @param stepwise
	 */
	public void setStepwise(boolean stepwise) {
		this.stepwise = stepwise;
	}

}
