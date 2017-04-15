
package MessengerApp;

import java.io.Serializable;

/*
 * @author Austin Vickers
 * 
 */

public class MessagePacket implements Serializable {

	private String sender;
	private String message;
	private String recipient;
	private boolean authentication;
	private String password;
	
	//Method to create a normal message packet
	public MessagePacket(String s1, String s2, String s3){
		sender = s1;
		message = s2;
		recipient = s3;
	}
	
	//Method used to create an authentication MessagePacket
	public MessagePacket(String userID, String pass, boolean auth){
		sender = userID;
		password = pass;
		authentication = auth;	//Should be set as false if coming from the user. The user will wait
								//for the boolean to be set by the server and returned. 
	}
	
	//Getters and Setters
	public String getSender(){
		return sender;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getRecipient(){
		return recipient;
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
}
