package MessengerApp;

/*This packet is used by the client to send requests to the server.
 * The server will read the request, and if it is able to process it, 
 * it will send back the Object requested by the client and set success to true
 * If it cannot complete the request, it will send back the packet with success = false.
 *  This type of packet is intentionally generic to support multiple types of requests.
 */

public class ServiceRequest {
	
	private String request;
	private boolean success = false;
	private Object response;
	
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

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}
	
}
