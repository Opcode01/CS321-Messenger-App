package MessengerApp;

import java.io.Serializable;

public class NewUserPacket implements Serializable {

	private String userData;
	
	public NewUserPacket(String newUserData){
		userData = newUserData;
	}

	public String getUserData() {
		return userData;
	}

}
