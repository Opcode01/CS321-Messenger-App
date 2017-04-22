package MessengerApp;

import javax.swing.*;
import SimpleServer.SimpleServer.Responder;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Austin Vickers, Huaming Zhang
 * 
 * This class is for the server side of the CS321 Messenger App.
 * This is exactly the same as the regular Messenger Server, but it runs
 * without all the extra GUI stuff in a "Headless" mode. This makes things
 * easier for running on command-line only OS like Linux.  It implements an 
 * interface "outputLog" that is used by both the MessageServer and the 
 * Responder class to output messages to the console.
 */

public class MessengerServerHeadless implements ServerIO{
	
	private int port = 25565;			//Port the server runs on. MUST MATCH THE PORT OF THE CLIENT!
	
	public static void main(String args[]){
		System.out.println("The server is starting...");
		MessengerServerHeadless server = new MessengerServerHeadless();
	}
	
	//Constructor. Sets up GUI stuff.
	public MessengerServerHeadless(){	
		System.out.println("Server running in headless mode...");
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
        
        //Constructor for the Responder Class
        public Responder(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("\nNew connection with client# " + clientNumber + " at " + socket);
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
						MessageRouter(newMessage);
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
	}
	
	public void command(String command){
		//TODO: write code to take commands from the server operator
	}
	
	public void MessageRouter(MessagePacket packet){
		/*TODO: write code to route the message packets to the appropriate user
		* Possibly should be a separate class.
		*/
		
	}
	
	public void SendMessage(MessagePacket packet){
		/*TODO: write method for sending a message from the server to a user or
		broadcast to a group of users. May need to be a part of the Responder class?
		*/
	}
	
	/*Implementation of the "outputLog" interface. Is used by multiple classes 
	 * to output strings to the server console.
	 * (non-Javadoc)
	 * @see MessengerApp.outputLog#log(java.lang.String)
	 */
	public void log(final String message){
		System.out.print(message);
	}
}
