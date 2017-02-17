/**
 * 
 */
package MessengerApp;

import javax.swing.JFrame;

/**
 * @author Austin
 *
 */
public class main {

	/**
	 * Main method used to start up a single MessengerClient or multiple clients.
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MessengerClient client = new MessengerClient("localhost", 6789);
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
