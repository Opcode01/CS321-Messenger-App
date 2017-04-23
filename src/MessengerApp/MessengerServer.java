package MessengerApp;

//import com.sun.deploy.util.SessionState;
import javax.swing.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/*
 * @author Austin Vickers, Huaming Zhang
 * 
 * This class is for the server side of the CS321 Messenger App.
 * This is the server console window. It implements an interface "outputLog"
 * that is used by both the MessageServer and the Responder class to output
 * messages to the console.
 * 
 * Note: Some things are labeled as "Static" and "Final" becuase there should only ever be one instance of this class
 * running on a machine. This also allows us to make the Responder class its own object
 */

public final class MessengerServer extends JFrame implements ServerIO{
	
	private JTextField commandLine;			//Place to enter commands
	private static JTextArea console;		//Viewport to the server console
	private int port = 25565;				//Port the server runs on. MUST MATCH THE PORT OF THE CLIENT!
	//This Map contains all the users currently connected. Written by Matthew Legowski
	private static Map<String, ObjectOutputStream> ClientConnections = new HashMap();
	private jdbcConnection con = new jdbcConnection("unicorn", "messenger");
	private Console SYSconsole;
	
	public static void main(String args[]){
		System.out.println("The server is starting...");
		try{	
			if(args[0].contains("headless")){
				System.out.println("Server running in headless mode");
				MessengerServer server = new MessengerServer(true);			
			}
		}catch(ArrayIndexOutOfBoundsException e){
				System.out.println("running with no args");
				MessengerServer server = new MessengerServer(false);
				server.setDefaultCloseOperation(EXIT_ON_CLOSE);
			}		
	
	}
	
	//Constructor. Sets up GUI stuff, or runs in headless mode if headless == true;
	public MessengerServer(boolean headless){
		if(headless){
			usingConsoleReader();
		}
		else{
			constructGUI();
		}
		//Start running our server
			try{
				startServer();
			}catch(Exception e){
				e.printStackTrace();
				log("\n Could not start server.");
			}
	}
	
	private void constructGUI(){
		//Add a command line so we can interface with the server
			commandLine = new JTextField();
			commandLine.addActionListener(		
					new ActionListener(){
					public void actionPerformed(ActionEvent event){
						command(event.getActionCommand());
						commandLine.setText("");		//Clear the text field after the event happens
					}
				}
			);
			add(commandLine, BorderLayout.SOUTH);
				
			//Create and add a text area for the chats to appear in
			console = new JTextArea();
			add(new JScrollPane(console));
			//Set window size and draw it on the screen.
			setSize(1024, 512);
			setVisible(true);
		
	}
	
	//Used if running a headless implementation of MessengerServer
	private void usingConsoleReader()
	{
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	Thread consoleReader = new Thread(new Runnable(){
		 public void run(){
			 while(true){
			 try{
				 command(in.readLine());
			 }catch(IOException ex){
				 ex.printStackTrace();
			 }
			 }
		 }});
	 consoleReader.start();
	}
	
	//Method starts the server. Copied from "ASimpleServer" code by Huaming Zhang
	private void startServer() throws Exception{
		int clientNumber = 0;
		try (ServerSocket listener = new ServerSocket(port)) {
			log("Waiting for users to connect...\n");
            while (true) {
                new Responder(listener.accept(), clientNumber++).start();
            }
        }
	}
	
	private void shutdownServer(){
		log("Messenger Server shutting down NOW");
		System.exit(0);
	}
	
	public void command(String command) {
	try{
		if(command.contains("/setDBAdminName")){
			con.setDbAdminName(command.substring(16));
			log("Set dbAdminName to: " +command.substring(16));
		}
		else if(command.contains("/setDBAdminPassword")){
			con.setDbAdminPassword(command.substring(20));
			log("Set dbAdminPassword to: " +command.substring(20));
		}
		else if(command.contains("/quit")){
			log("Are you sure you want to shutdown? Type /quit -f to force shutdown");
			if(command.contentEquals("/quit -f")){
				shutdownServer();
			}
		}
		else if(command.contains("/help") || command.contains("?")){
			log("Valid commands are: \n "
					+ "/setDBAdminName [name] \n" 
					+ "/setDBAdminPassword [password] \n"
					+ "/quit \n");	
		}
		else{
			log("Command not recognized. Type /help to see all available commands. \n");
		}
	}catch(Exception e){
		log("Invalid parameters. Please re-type your command and try again.");
	}
		
	}
	
	//Method returns a list of all online users
	public static String[] onlineUsers(){
		Set<String> users = getClientConnections().keySet();
		String[] ret = users.toArray(new String[0]);
		return ret;	
	}
	
	private void SendMessage(MessagePacket packet){
		//TODO: write method for sending a message from the server to a user or
		//broadcast to a group of users. May need to be a part of the Responder class?
	}
	
	/*Implementation of the "ServerIO" interface. Is used by multiple classes 
	 * to output strings to the server console.
	 * (non-Javadoc)
	 * @see MessengerApp.outputLog#log(java.lang.String)
	 */
	public static void log(String message){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					if(console == null){
						System.out.println(message);
					}
					else{
						console.append(message);
					}
				}
			}
		);
	}

	//This is needed by the responder class to get the list of users currently connected
	public static Map<String, ObjectOutputStream> getClientConnections() {
		return ClientConnections;
	}

}
