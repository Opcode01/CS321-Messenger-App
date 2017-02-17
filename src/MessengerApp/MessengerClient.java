package MessengerApp;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/*
 * @author Austin Vickers
 * This class is the client side of the CS321 Messenger App.
 * It constructs a GUI that take input from the user and display
 * responses from the server. Uses some modified code from Bucky Roberts available on 
 * GitHub: /buckyroberts/Source-Code-from-Tutorials/Java_Intermediate/57_javaIntermediate.java
 */

public class MessengerClient extends JFrame{
	
	private JTextField userText;				//The text field the user types into
	private JTextArea chatWindow;				//The window where messages will appear
	private ObjectOutputStream output;			//The steam sending messages to the server
	private ObjectInputStream input;			//The stream taking in messages from the server
	private MessagePacket message;				//The message object to send back and forth				
	private String serverIP;					//IP address of the server we want to connect to
	private String username;					//The username of this Client instance
	private String recipient;					//The person this client is currently talking to
	private Socket connection;					//The actual socket used to establish the connection
	private int serverPort;						//The port that our program uses to connect
	
	//Constructor
	public MessengerClient(String host, int port){
		super("Client");						//Title of the window
		serverIP = host;
		serverPort = port;
		
		//TO-DO: Add code that handles routing messages to other users.
		username = "USER";		
		recipient = "SERVER";
		
		userText = new JTextField();			//Creates a new text field we can enter a message into
		userText.setEditable(false);			//Make sure we don't accept any text just yet
		//Adds an ActionListener to detect when the user presses "Enter" in the user text field.
		userText.addActionListener(		
				new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand());
					userText.setText("");		//Clear the text field after the event happens
				}
			}
		);
		add(userText, BorderLayout.SOUTH);		//Add this element to the window
		
		//Create and add a text area for the chats to appear in
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		//Set window size and draw it on the screen.
		setSize(640, 480);
		setVisible(true);
		//Start running our client
		startRunning();
	}
	
	//Connect to server
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException eofE){
			showMessage("\n Client terminated the connection");
		}catch(IOException ioE){
			ioE.printStackTrace();
			showMessage("\n Server refused the connection. Cannot connect to server");
		}finally{
			closeConnection();
		}
	
	}
	
	//Connect to server
	private void connectToServer() throws IOException{
		showMessage("Attempting connection... \n");
		connection = new Socket(serverIP, serverPort);
		showMessage("Connection Established! Connected to: " + connection.getInetAddress().getHostName());
	}
	
	//Set up streams
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		//showMessage("\n The streams are now set up! \n");
	}
	
	//While chatting with server
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (MessagePacket) input.readObject();				
				showMessage("\n" + message.sender + " - " + message.message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("Unknown data received!");
			}
		}while(!message.message.equals("END"));	
	}
	
	//Close connection
	private void closeConnection(){
		showMessage("\n Closing the connection!");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//Send message to server
	private void sendMessage(String message){
		try{
			output.writeObject(new MessagePacket(username, message, recipient));
			output.flush();
			showMessage("\n" + username + " - " + message);
		}catch(IOException ioException){
			chatWindow.append("\n Message Failed to Send");
		}
	}
	
	//update chat window
	private void showMessage(final String message){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(message);
				}
			}
		);
	}
	
	//allows user to type
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(tof);
				}
			}
		);
	}
}