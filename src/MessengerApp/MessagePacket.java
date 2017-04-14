
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
	
	public MessagePacket(String s1, String s2, String s3){
		sender = s1;
		message = s2;
		recipient = s3;
	}
	
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
