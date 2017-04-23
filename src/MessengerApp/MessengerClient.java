package MessengerApp;

import java.io.*;
import java.net.*;
import java.util.Set;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
	private String serverIP;					//IP address of the server we want to connect to
	private String username;					//The username of this Client instance
	private String recipient;					//The recipient the user has selected
	private DefaultListModel<String> userList;	//The people this client can talk to
	private Socket connection;					//The actual socket used to establish the connection
	private int serverPort;						//The port that our program uses to connect
	private String userPassword;				//The password of the user using this client
	
	//Constructor
	public MessengerClient(String host, int port, String username, String password){
		super("Client");	
		serverIP = host;
		serverPort = port;
		this.username = username;
		this.userPassword = password;
		
		//Construct GUI:
		constructGUI();
		
		//Start running our client
		startRunning();
	}
	
	//A separate constructor is used in the case that we want to connect with a new user
	public MessengerClient(String host, int port, String username, String password, String newUserData){
		super("Client");	
		serverIP = host;
		serverPort = port;		
		this.username = username;
		this.userPassword = password;
		
		//Call the method to register a new user
		try{
			registerAccount(newUserData);
		}catch(IOException e){
			e.printStackTrace();
		}
		
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
		userList = new DefaultListModel<String>();
		OnlineUsers.setModel(userList);
		OnlineUsers.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent arg0) {
				recipient = OnlineUsers.getSelectedValue();
				chatWindow.setText("\n");
				showMessage("Now talking to "+recipient +". \n");
			}
		});
		
		panel.add(OnlineUsers);
		
		JLabel lblOnlineUsers = new JLabel("Online Users:");
		lblOnlineUsers.setFont(new Font("Calibri", Font.BOLD, 20));
		panel.add(lblOnlineUsers, BorderLayout.NORTH);
		
		JButton btnRefresh = new JButton("Refresh User List");
		btnRefresh.setForeground(Color.BLUE);
		btnRefresh.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnRefresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg) {
				try{
					requestOnlineUsers();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		});
		
		panel.add(btnRefresh, BorderLayout.SOUTH);
		
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
			requestAuthentication();
			requestOnlineUsers();
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
	
	private void requestOnlineUsers() throws IOException{
		output.writeObject(new ServiceRequest("getOnlineUsers"));
		output.flush();
	}
	
	private void requestAuthentication() throws IOException{
		//Send out auth request to server
		output.writeObject(new AuthenticationPacket(username, userPassword));
		output.flush();
		showMessage("\nRequesting authentication from server\n");
	}
  
  //new code here***************************************
  public void registerAccount(String userData)throws IOException
  {
      //Send out newAccount request to server
		output.writeObject(new NewUserPacket(userData));
	  	output.flush();
	  	showMessage("\nCreating account on server\n");
	  	
	
   }
	///End new code/E********************************************************
  
	//While chatting with server
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{				
				//Copied Looks for the same things as the Server does, but has different implementation.				
				Object received = input.readObject();
				if(received.getClass().getName() == "MessengerApp.MessagePacket"){	
					MessagePacket message = (MessagePacket) received;
					showMessage("\n" + message.getSender() + " - " + message.getMessage());
					
				}
				else if(received.getClass().getName() == "MessengerApp.ServiceRequest"){
					//Check to see what service the user would like
					ServiceRequest service = (ServiceRequest) received;
					if(service.isSuccess()){
						String[] string = service.getResponse();
						userList.clear();
						for(int i = 0; i < string.length; i++){
							userList.addElement(string[i]);
						}
						
					}
					else{
						showMessage("Cannot understand server response: " +service.getRequest());
					}	
				}
				else if(received.getClass().getName() == "MessengerApp.AuthenticationPacket"){					
					AuthenticationPacket response = (AuthenticationPacket) received;
					if(!response.getAuthState()){
						showMessage("\nInvalid user/pass. Server refused the connection.");
						break;
					}
				}				
				
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("Unknown data received!");
			}
		}while(true);	
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