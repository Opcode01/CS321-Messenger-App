package MessengerApp;

import javax.swing.*;
//import SimpleServer.SimpleServer.Responder;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

/*
 * @author Austin Vickers, Huaming Zhang
 * 
 * This class is for the server side of the CS321 Messenger App.
 * This is the server console window. It implements an interface "outputLog"
 * that is used by both the MessageServer and the Responder class to output
 * messages to the console.
 */

public class MessengerServer extends JFrame implements outputLog{
	
	private JTextField commandLine;			//Place to enter commands
	private JTextArea console;				//Viewport to the server console
	private int port = 25565;				//Port the server runs on. MUST MATCH THE PORT OF THE CLIENT!
	private jdbcConnection connection;		//Connection to the database needed to authenticate users
	
	public static void main(String args[]){
		System.out.println("The server is starting...");
		MessengerServer server = new MessengerServer();
		server.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//Constructor. Sets up GUI stuff.
	public MessengerServer(){	
		
		connection = new jdbcConnection();
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
        private ObjectOutputStream output;
        private ObjectInputStream input;
        private String welcomeMessage;
        private jdbcConnection con;
        
        //Constructor for the Responder Class
        public Responder(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("\nNew connection with client# " + clientNumber + " at " + socket);
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
            	output.flush();
            	
            	if(authenticateUser((MessagePacket) input.readObject()) == false){
            		log("\nUser Authentication failed!\n");
            		input.close();
                	output.close();
                    socket.close();
            		return;
            	}
            	
       
                // Send a welcome message to the client.
            	welcomeMessage = "Hello, you are client #" + clientNumber + ".\n";
            	output.writeObject(new MessagePacket("Server", welcomeMessage, "Client" ));
            	
                // Get messages from the clients and print them out in the console.
                while (true) {
                	MessagePacket newMessage;
                    String message;
                    String sender;
					try {
						newMessage = (MessagePacket) input.readObject();
						sender = newMessage.getSender();
						message = newMessage.getMessage();
						log("\n" + sender + " - " + message);
						if (message == null || message.equals("q")) {
	                        break;
	                    }
					} catch (ClassNotFoundException e) {
						log("\n Server cannot read this object.");
					}
                }
            } catch (IOException e) {
                log("\n Error handling client# " + clientNumber + ": " + e);
            } catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				log("\n Server cannot read this object");
			} finally {
                try {
                	input.close();
                	output.close();
                    socket.close();
                } catch (IOException e) {
                    log("\n Couldn't close a socket, what's going on?");
                }
                log("\n Connection with client# " + clientNumber + " closed\n");
            }
        }
        
        public boolean authenticateUser(MessagePacket authPacket) throws IOException{
        	//First packet received should be a MessagePacket with the auth value as false      		
       		String userID = authPacket.getSender();
       		String userPass = authPacket.getPassword();
       		if(con.authenticate(userID, userPass) == true){
       			authPacket.setAuthState(true);
       			output.writeObject(authPacket);
       			return true;
        	}
        	else{
       			authPacket.setAuthState(false);
       			output.writeObject(authPacket);
       			
       			return false;
       		}
        }
                
    }        
	
	public void command(String command){
		//TODO: write code to take commands from the server operator
	}
	
	public void MessageRouter(MessagePacket packet){
		//TODO: write code to route the message packets to the appropriate user
		//Possibly should be a separate class.
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
