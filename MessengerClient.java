package MessengerApp;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

/**
 * @author Austin Vickers and Nick Bollis
 * This class is the client side of the CS321 Messenger App.
 * It constructs a GUI that take input from the user and display
 * responses from the server. Uses some modified code from Bucky Roberts available on 
 * GitHub: /buckyroberts/Source-Code-from-Tutorials/Java_Intermediate/57_javaIntermediate.java
 */

public class MessengerClient extends JFrame{
    
    private JTextField userText;                //The text field the user types into
    private ObjectOutputStream output;          //The steam sending messages to the server
    private ObjectInputStream input;            //The stream taking in messages from the server
    private String serverIP;                    //IP address of the server we want to connect to
    private String username;                    //The username of this Client instance
    private String recipient;                   //The recipient the user has selected
    private String newUserData;                 //Used only if this is a brand new user
    private DefaultListModel<String> userList;  //The people this client can talk to
    private Socket connection;                  //The actual socket used to establish the connection
    private int serverPort;                     //The port that our program uses to connect
    private String userPassword;                //The password of the user using this client
    private boolean newUserFlag = false;        //If this is a new user, use this to call the register user method
    private JTextArea chatWindow;
    private JTextArea scrollPane;
    
    //Constructor
    /**
     * @wbp.parser.constructor
     */
    public MessengerClient(String host, int port, String username, String password){
        super("UNICORN Client");    
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
        super("UNICORN Client");    
        serverIP = host;
        serverPort = port;      
        this.username = username;
        this.userPassword = password;
        
        //Call the method to register a new user
        newUserFlag = true;
        this.newUserData = newUserData;
        
        //Construct GUI:
        constructGUI();
        
        //Start running our client
        startRunning();
        
    }
    
    //@SuppressWarnings("unchecked")
    /**
     * ConstructGUI method written by @author Nick Bollis
     * Constructs a GUI for the Messenger Client in order to :
     *  1. view online users
     *  2. refresh the list of online users
     *  3. instant message other users
     * MiGLayout used as design layout
     */
    public void constructGUI(){
        
        try
        {
          URL url1= new URL("http://data.whicdn.com/images/7883185/large.gif");
          final List<Image> icon=new ArrayList<Image>();
          icon.add(ImageIO.read(url1));
          setIconImages(icon); 
        } catch (Exception e)
        {
          e.printStackTrace();
        } 
        setVisible(true);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600)); 
        getContentPane().setBackground(new Color(255, 211, 246));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new MigLayout("", "[200px,grow,left][608px,grow]", "[100px,grow,center][353px,grow,center][39px,grow,center]"));
        
        JLabel lblOnlineUsers = new JLabel("Online Users:");
        getContentPane().add(lblOnlineUsers, "cell 0 0,alignx center,aligny center");
        lblOnlineUsers.setFont(new Font("Calibri", Font.BOLD, 20));
        
        JScrollPane scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, "cell 1 0 1 2,grow");
        
        JTextArea chatWindow = new JTextArea();
        chatWindow.setFont(new Font("Calibri", Font.PLAIN, 15));
        chatWindow.setEditable(false);
        scrollPane.setViewportView(chatWindow);
        
        JList<String> OnlineUsers = new JList<String>();
        getContentPane().add(OnlineUsers, "flowx,cell 0 1");
        OnlineUsers.setBackground(Color.LIGHT_GRAY);
        OnlineUsers.setVisibleRowCount(10);
        OnlineUsers.setAlignmentX(Component.RIGHT_ALIGNMENT);
        OnlineUsers.setAlignmentY(Component.TOP_ALIGNMENT);
        OnlineUsers.setToolTipText("Online Users");
        OnlineUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        OnlineUsers.setFont(new Font("Arial", Font.BOLD, 24));  
        OnlineUsers.setModel(userList);
        OnlineUsers.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent arg0) {
                recipient = OnlineUsers.getSelectedValue();
                chatWindow.setText("\n");
                showMessage("Now talking to "+recipient +". \n");
            }
        });
        userList = new DefaultListModel<String>();
        
        LaunchWindow.JGradientButton btnRefresh = new LaunchWindow.JGradientButton("Refresh User List");
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.setHorizontalTextPosition(SwingConstants.CENTER);
        getContentPane().add(btnRefresh, "cell 0 2,grow");
        btnRefresh.setFont(new Font("Tahoma", Font.PLAIN, 17));
        btnRefresh.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg) {
                try{
                    requestOnlineUsers();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
        
        userText = new JTextField();
        getContentPane().add(userText, "cell 1 2,growx,aligny top");
        userText.setEditable(false);
        
        //Adds an ActionListener to detect when the user presses "Enter" in the user text field.
        userText.addActionListener(     
                new ActionListener(){
                public void actionPerformed(ActionEvent event){
                    sendMessage(event.getActionCommand());
                    userText.setText("");       //Clear the text field after the event happens
                }
            }
        );
        //Set window size and draw it on the screen.
        setSize(640, 480);
        setVisible(true);
    }

/**
 * The rest of the code in this file was written by @author Austin
 */
//Connect to server
public void startRunning(){
    try{
        connectToServer();
        setupStreams();
        if(newUserFlag){
            registerAccount(newUserData);
        }
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
    NewUserPacket packet = new NewUserPacket(userData);
    output.writeObject(packet);
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