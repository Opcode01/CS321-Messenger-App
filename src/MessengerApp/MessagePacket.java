
package MessengerApp;

import java.io.Serializable;

/*
 * @author Austin Vickers
 * 
 */

public class MessagePacket implements Serializable {

	public String sender;
	public String message;
	public String recipient;
	
	public MessagePacket(String s1, String s2, String s3){
		sender = s1;
		message = s2;
		recipient = s3;
	}
}
