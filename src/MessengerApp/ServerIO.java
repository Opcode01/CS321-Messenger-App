package MessengerApp;

/*
 * @author Austin Vickers
 * Interface used by the MessengerServer and Responder classes to output strings to a console window.
 */
public interface ServerIO{
	public void log(final String message);		//Used for logging info to the server window
	public void command(String command);		//Used for taking input from the server manager
}
