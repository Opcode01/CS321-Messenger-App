
package MessengerApp;

import java.io.Serializable;

/**
 * Every message that is sent between two users contains three basic pieces of information:
 * where the message came from, who its going to, and what the message contains. These bits
 * of informations are wrapped up nicely in this MessagePacket class. This class is used by the
 * server to route the messages to different users, and used by the clients to display the contents
 * of the message and where it came from. This class is an integral part to the operation of the 
 * entire system.
 * 
 * @author Austin Vickers
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
