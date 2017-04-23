package MessengerApp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/*NOTE: This is a NESTED CLASS. A subclass of MessengerServer.
 * Copied and modified from "ASimpleServer" code by Huaming Zhang
 */
public class Responder extends Thread implements ServerIO{
	
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
        con = new jdbcConnection("unicorn", "messenger");
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
						service.setResponse(MessengerServer.onlineUsers());
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
		             welcomeMessage = "Welcome, " +username +" you are client #" + clientNumber + ".\n"
		            		 			+"Type 'q' to close your connection. \n";
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
            	MessengerServer.getClientConnections().remove(username);
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
			    MessengerServer.getClientConnections().put(username, output);
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
    	
		if(MessengerServer.getClientConnections().containsKey(packet.getRecipient())) {
			try {
				MessengerServer.getClientConnections().get(packet.getRecipient()).writeObject(packet);
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

	public void log(String message) {
		// TODO Auto-generated method stub
		MessengerServer.log(message);
	}

	@Override
	public void command(String command) {
		// TODO Auto-generated method stub
	}

            
}        
