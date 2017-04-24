package MessengerApp;

import java.io.Serializable;
import java.util.Set;

/*This packet is used by the client to send requests to the server.
 * The server will read the request, and if it is able to process it, 
 * it will send back the Object requested by the client and set success to true
 * If it cannot complete the request, it will send back the packet with success = false.
 *  This type of packet is intentionally generic to support multiple types of requests.
 *  @author Austin Vickers
*/

public class ServiceRequest implements Serializable {
	
	private String request;
	private boolean success = false;
	private String[] response;
	
	public ServiceRequest(String service){
		setRequest(service);
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String[] getResponse() {
		return response;
	}

	public void setResponse(String[] set) {
		this.response = set;
	}
	
}
