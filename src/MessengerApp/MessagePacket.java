
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
	
	
	//Method to create a normal message packet

	/**
	 *
	 * @param sender, sender's user ID
	 * @param message, message being sent
	 * @param recipient, USER ID of a user or #chatroom
	 */
	public MessagePacket(String sender, String message, String recipient){
		this.sender = sender;
		this.message = message;
		this.recipient = recipient;
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

}
