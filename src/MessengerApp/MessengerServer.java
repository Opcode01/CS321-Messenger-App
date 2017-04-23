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
 */

public class MessengerServer extends JFrame implements ServerIO{
	
	private JTextField commandLine;			//Place to enter commands
	private JTextArea console;				//Viewport to the server console
	private int port = 25565;				//Port the server runs on. MUST MATCH THE PORT OF THE CLIENT!
	//This Map contains all the users currently connected. Written by Matthew Legowski
	private Map<String, ObjectOutputStream> ClientConnections = new HashMap();
	
	public static void main(String args[]){
		System.out.println("The server is starting...");
		MessengerServer server = new MessengerServer();
		server.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//Constructor. Sets up GUI stuff.
	public MessengerServer(){	
		
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
		//Start running our server
		try{
		startServer();
		}catch(Exception e){
			e.printStackTrace();
			log("\n Could not start server.");
		}
	}
	
	//Method starts the server. Copied from "ASimpleServer" code by Huaming Zhang
	public void startServer() throws Exception{
		int clientNumber = 0;
		try (ServerSocket listener = new ServerSocket(port)) {
			log("Waiting for users to connect...\n");
            while (true) {
                new Responder(listener.accept(), clientNumber++).start();
            }
        }
	}
	
	/*NOTE: This is a NESTED CLASS. A subclass of MessengerServer.
	 * Copied and modified from "ASimpleServer" code by Huaming Zhang
	 */
	private class Responder extends Thread{
        private final Socket socket;
        private final int clientNumber;
        private String username;
        private ObjectOutputStream output;
        private ObjectInputStream input;
        private String welcomeMessage;
        private jdbcConnection con;
        
        //Constructor for the Responder Class
        public Responder(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("\nNew connection with client# " + clientNumber + " at " + socket + "\n");
            con = new jdbcConnection();
        }

        /**
         * Services this thread's client by first sending the
         * client a welcome message then repeatedly reading strings
         * and sending back the version of the string.
         */
        @Override
        public void run() {
            try {
            	//Set up the in and out streams for this client.
            	input = new ObjectInputStream(socket.getInputStream());
            	output = new ObjectOutputStream(socket.getOutputStream());
            	
                // Get messages from the clients and print them out in the console.
                while (true) {
                	MessagePacket newMessage;
                    String message;
                    String sender;
                    String recipient;
					try {
						Object received = input.readObject();
						if(received.getClass().getName() == "MessengerApp.MessagePacket"){						
							newMessage = (MessagePacket) received;
							sender = newMessage.getSender();
							message = newMessage.getMessage();
							recipient = newMessage.getRecipient();
							log("\n" + sender + " - " + message + " to " +recipient);
							if (message == null || message.equals("q")) {
								break;
	                    	}
							MessageRouter(newMessage);
						}
						else if(received.getClass().getName() == "MessengerApp.ServiceRequest"){
							//Check to see what service the user would like
							ServiceRequest service = (ServiceRequest) received;
							service.setResponse(onlineUsers());
							service.setSuccess(true);
							output.writeObject(service);
						}
						else if(received.getClass().getName() == "MessengerApp.NewUserPacket" ){
							NewUserPacket newUser = (NewUserPacket) received;
							con.createAccount(newUser.getUserData());
							log("Adding new user ");
						}
						
						else if(received.getClass().getName() == "MessengerApp.AuthenticationPacket"){
							log("Recieved auth request from client #" + clientNumber +"\n");
							if(!authenticateUser(received)){
			            		log("User Authentication failed!\n");
			            		input.close();
			                	output.close();
			                    socket.close();
			            		return;
			            	}
							else{
								   // Send a welcome message to the client.
			             welcomeMessage = "Welcome, " +username +" you are client #" + clientNumber + ".\n";
			             output.writeObject(new MessagePacket("SERVER::", welcomeMessage, "Client" ));	
							}
						}
						else{
							//If received something we didn't expect:
							log("\n " + received.getClass().getName());
							break;
						}
						
					} catch (ClassNotFoundException e) {
						log("\n Server cannot read this object.");
					}
                }
            } catch (IOException e) {
                log("\n Error handling client# " + clientNumber + ": " + e);
            }
			finally {
                try {
                	ClientConnections.remove(username);
                	input.close();
                	output.close();
                    socket.close();
                } catch (IOException e) {
                    log("\n Couldn't close a socket, what's going on?");
                }
                log("\nConnection with client# " + clientNumber + " closed\n");
            }
        }
        
        public boolean authenticateUser(Object Packet) throws IOException{
        	//First packet received should be a MessagePacket with the auth value as false    
        	AuthenticationPacket authPacket = (AuthenticationPacket) Packet;
       		String userID = authPacket.getSender();
       		String userPass = authPacket.getPassword();
       		
       		if(con.authenticate(userID, userPass)){
       			authPacket.setAuthState(true);
       			output.writeObject(authPacket);
       			username = userID;
				    ClientConnections.put(username, output);
       			return true;
        	}
        	else{
       			authPacket.setAuthState(false);
       			output.writeObject(authPacket);
       			
       			return false;
       		}
       		
        }
        public void MessageRouter(MessagePacket packet) throws IOException{
        	String errorMessage;
        	
    		if(ClientConnections.containsKey(packet.getRecipient())) {
    			try {
    				ClientConnections.get(packet.getRecipient()).writeObject(packet);
    			} catch (IOException e) {
    				errorMessage = " Could not deliver message to user " + packet.getRecipient();
    				log(errorMessage);
    				output.writeObject(new MessagePacket("SERVER::", errorMessage, username));
    			}
    		}else{
    			errorMessage =" Cannot deliver message, user is not connected: "+packet.getRecipient();
    			log(errorMessage);
    			output.writeObject(new MessagePacket("SERVER::", errorMessage, username));
    			
    		}
    		
    	}
                
    }        
	
	public void command(String command){
		//TODO: write code to take commands from the server operator
	}
	
	//Method returns a list of all online users
	public String[] onlineUsers(){
		Set<String> users = ClientConnections.keySet();
		String[] ret = users.toArray(new String[0]);
		return ret;	
	}
	
	public void SendMessage(MessagePacket packet){
		//TODO: write method for sending a message from the server to a user or
		//broadcast to a group of users. May need to be a part of the Responder class?
	}
	
	/*Implementation of the "outputLog" interface. Is used by multiple classes 
	 * to output strings to the server console.
	 * (non-Javadoc)
	 * @see MessengerApp.outputLog#log(java.lang.String)
	 */
	public void log(final String message){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					console.append(message);
				}
			}
		);
	}
}
