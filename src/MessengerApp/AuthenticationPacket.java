package MessengerApp;

import java.io.Serializable;

/*This class is sent from the client to the server and is used by the server to run
 * authentication. The server acts as an interface in between the jdbcConnection and
 * the client.
 * 
 * @author Austin Vickers
 */

public class AuthenticationPacket implements Serializable {

	private String sender;
	private boolean authentication;
	private String password;
	
	public AuthenticationPacket(String userID, String pass){
		setSender(userID);
		password = pass;
		authentication = false;	//Should be set as false if coming from the user. The user will wait
								//for the boolean to be set by the server and returned. 
	}
	
	public boolean getAuthState() {
		return authentication;
	}

	public void setAuthState(boolean authentication) {
		this.authentication = authentication;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
				
}
