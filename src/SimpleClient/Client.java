/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpleClient;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;




/**
 * A simple client for the SimpleServer.
 */
public class Client {

    private BufferedReader in;
    private PrintWriter out;
   
    private String login;
    private String passwd;
    private BufferedReader input;
    

    /**
     * Constructs the client by wrapping its input stream.
     */
    public Client() {
        input = new BufferedReader (new InputStreamReader(System.in));
    }
    
    public void logIn () {
        System.out.println("Please input your username:");
        
        try {
            login = input.readLine();
            out.println(login);
            String response;
            response = in.readLine();
            System.out.println("Welcome you, " + response);
        }
        
        catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Please input your password:");
        
        try {
            passwd = input.readLine();
            out.println(passwd);
            String response;
            response = in.readLine();
            System.out.println("Password verified, thank you!");
              
        }
        
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public void communicate () {
        System.out.println("Please input your message:");
        while (true) {
            try {
                String message = input.readLine();
                out.println(message);
                String response = in.readLine();
                if (response == null || response.equals("")) 
                    System.exit(0);
                else 
                    System.out.println(response);

  
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
      
    

    /**
     * Implements the connection to the localhost (i.e., running on the same machine),
     * connecting, setting up streams, and
     * consuming the welcome messages from the server.  The SimpleServer
     * protocol says that the server sends two lines of text to the
     * client immediately after establishing a connection.
     */
    public void connectToServer() throws IOException {

        
        // Make connection and initialize streams
        Socket socket = new Socket("localhost", 9001);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Consume the initial welcoming messages from the server
        for (int i = 0; i < 2; i++) {
            System.out.print(in.readLine() + "\n");
        }
    }

    /**
     * Runs the client application.
     */
    public static void main(String[] args) throws Exception {
        try{
    	Client client = new Client();
        client.connectToServer();
        client.logIn();
        client.communicate();
        }catch(ConnectException e){
        	System.out.println("Could not connect to server. Connection refused.");
        	System.exit(0);
        }
    }
}

