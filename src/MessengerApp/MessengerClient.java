package MessengerApp;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
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
	private String userPassword;				//The password of the user using this client
	
	//Constructor
	public MessengerClient(String host, int port, String username, String password){
		super("Client");	
		serverIP = host;
		serverPort = port;		
		//TODO: Function calls to code that handles routing messages to other users.		
		this.username = username;
		this.userPassword = password;
		recipient = "SERVER";
		
		//Construct GUI:
		constructGUI();
		
		//Start running our client
		startRunning();
	}
	
	//@SuppressWarnings("unchecked")
	public void constructGUI(){
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JDesktopPane desktopPane = new JDesktopPane();
		getContentPane().add(desktopPane, BorderLayout.CENTER);
		
		//Create and add a text area for the chats to appear in
		chatWindow = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(chatWindow);
		scrollPane.setBounds(183, 0, 439, 410);
		desktopPane.add(scrollPane);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 184, 411);
		desktopPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JList<String> OnlineUsers = new JList<String>();
		OnlineUsers.setBackground(Color.LIGHT_GRAY);
		OnlineUsers.setVisibleRowCount(10);
		OnlineUsers.setAlignmentX(Component.RIGHT_ALIGNMENT);
		OnlineUsers.setAlignmentY(Component.TOP_ALIGNMENT);
		OnlineUsers.setToolTipText("Online Users");
		OnlineUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		OnlineUsers.setFont(new Font("Arial", Font.BOLD, 24));	
		ListModel<String> bigData = new AbstractListModel<String>() {
			 private String[] friendsList = {""};				//List of online users we can chat with
		     public int getSize() { return friendsList.length; }
		     public String getElementAt(int index) { return friendsList[index]; }
		     public void addElement(String e){
		    	 friendsList[friendsList.length] = e;
		     }
		 };
		OnlineUsers.setModel(bigData);
		
		panel.add(OnlineUsers);
		
		JLabel lblOnlineUsers = new JLabel("Online Users:");
		lblOnlineUsers.setFont(new Font("Calibri", Font.BOLD, 20));
		panel.add(lblOnlineUsers, BorderLayout.NORTH);
		
		userText = new JTextField();
		getContentPane().add(userText, BorderLayout.SOUTH);
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
		//Set window size and draw it on the screen.
		setSize(640, 480);
		setVisible(true);
	}
	
	//Connect to server
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			if(!requestAuthentication()){
				showMessage("\nInvalid user/pass. Server refused the connection.");
			}
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
	
	private boolean requestAuthentication() throws IOException{
		//Send out auth request to server
		output.writeObject(new MessagePacket(username, userPassword, false));
		output.flush();
		try{
			MessagePacket response = (MessagePacket) input.readObject();
			if(response.getAuthState() == true)
				return true;
			else
				return false;
		}
		catch(ClassNotFoundException e){
			showMessage("Could not read data from server");
			return false;
		}
	}
	
	//While chatting with server
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (MessagePacket) input.readObject();				
				showMessage("\n" + message.getSender() + " - " + message.getMessage());
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("Unknown data received!");
			}
		}while(!message.getMessage().equals("END"));	
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