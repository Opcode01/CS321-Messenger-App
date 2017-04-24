package MessengerApp;

import java.io.Serializable;

/*This type of packet is used by the messengerClient to send a request to the server
 * to add a new user to the database. It is very simple, with userData being the 
 * String that the jdbcConnection class parses.
 * @author Austin Vickers
 */

public class NewUserPacket implements Serializable {

	private String userData;
	
	public NewUserPacket(String newUserData){
		userData = newUserData;
	}

	public String getUserData() {
		return userData;
	}

}
