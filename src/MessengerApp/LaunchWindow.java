 package MessengerApp;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

/**
 * This LaunchWindow class holds the main method and presents a GUI that the user can
 * use to select a server to connect to, what port to use, and what username to have.
 * This information is passed to the actual MessengerClient that we use to interact 
 * with the server.
 * 
 * @author Austin
 *
 */

public class LaunchWindow extends JFrame {

	/** TODO: Be able to communicate with server to cross check users credentials with
	 * the server database. 
	*/
	
	private JPanel contentPane;
	private JTextField IPAddressField;
	private JTextField PortField;
	private JTextField UsernameField;
	private JTextArea outputText;
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				try {
					LaunchWindow frame = new LaunchWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
	}

	/**
	 * Auto-Generated code by WindowBuilder to create the launch window GUI.
	 */	
	public LaunchWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 250);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel Title = new JLabel("Welcome to the CS 321 Messenging App!");
		Title.setHorizontalAlignment(SwingConstants.CENTER);
		Title.setFont(new Font("Tahoma", Font.BOLD, 14));
		Title.setBounds(10, 11, 414, 14);
		contentPane.add(Title);
		
		IPAddressField = new JTextField();
		IPAddressField.setBounds(68, 36, 204, 20);
		contentPane.add(IPAddressField);
		IPAddressField.setColumns(20);
		
		PortField = new JTextField();
		PortField.setBounds(338, 36, 86, 20);
		contentPane.add(PortField);
		PortField.setColumns(10);
		
		JLabel lblIP = new JLabel("IP Address:");
		lblIP.setBounds(10, 36, 71, 20);
		contentPane.add(lblIP);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(282, 36, 46, 20);
		contentPane.add(lblPort);
		
		JLabel lblUser = new JLabel("Username:");
		lblUser.setBounds(10, 67, 71, 14);
		contentPane.add(lblUser);
		
		UsernameField = new JTextField();
		UsernameField.setBounds(68, 67, 204, 20);
		contentPane.add(UsernameField);
		UsernameField.setColumns(10);
		
		//Connect button requires an action listener
		JButton btnConnect = new JButton("Connect!");
		btnConnect.setBounds(335, 162, 89, 23);
		btnConnect.addActionListener(		
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
					connect();
				}
			}
		);
		contentPane.add(btnConnect);
		
		outputText = new JTextArea();
		outputText.setBounds(10, 161, 262, 24);
		contentPane.add(outputText);
		
		//TODO: Add password field

	}
	
	/**
	 * Takes the text from the GUI text fields and uses them to create new Messenger Clients
	 */
	public void connect(){
		try{
			String ip = IPAddressField.getText();
			int port = Integer.parseInt(PortField.getText());
			String user = UsernameField.getText();
			
			//We need to run each new client in a Thread because otherwise the program will halt. 
			//RULE OF THUMB: New thread for every new window.
			Thread client = new Thread(new Runnable(){
				public void run(){					
					//TODO: Run code to authenticate user with server
					MessengerClient c = new MessengerClient(ip, port, user);
				}
			});	
			client.start();
			
		//Want to make sure the user is putting in valid text.	
		}catch(NumberFormatException e){
			outputText.append("Invalid input. Please Try again.\n");
		//Just a generic catch for if something else breaks.
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
}
