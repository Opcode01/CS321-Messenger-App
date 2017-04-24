package MessengerApp;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import java.awt.Font;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

/**
 * This LaunchWindow class holds the main method and presents a GUI that the user can
 * use to select a server to connect to, what port to use, and what username to have.
 * This information is passed to the actual MessengerClient that we use to interact 
 * with the server.
 * 
 * @author Nick Bollis and Austin
 *
 */


public class LaunchWindow extends JFrame {

    /** TODO: Be able to communicate with server to cross check users credentials with
     * the server database. 
     * Written by @author Nick Bollis
    */
  public static void main(String[] args) throws Exception {
    
    //changes the icon of the LaunchWindow page
    URL url1= new URL("http://data.whicdn.com/images/7883185/large.gif");
    final List<Image> icon=new ArrayList<Image>();
    icon.add(ImageIO.read(url1));
    
    EventQueue.invokeLater(new Runnable(){
        public void run(){
            try {
                LaunchWindow frame = new LaunchWindow();    //create the JFrame
                frame.setIconImages(icon);  //JFrame formatting
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }); 
  }
    //Variable Declarations
    private JPanel contentPane;
    private JTextField IPAddressField;
    private JTextField PortField;
    private JTextField UsernameField;
    private JTextField passwordField;
    private JTextArea outputText;

    /**
     * The following code is for GUI construction.
     * All elements and formatting changes to LaunchWindow were made here.
     * Uses MigLayout to auto-resize elements in the case the size of the 
     * window is changed.
     * Written by @author Nick Bollis
     */ 
    public LaunchWindow() {
      setMinimumSize(new Dimension(900, 600));
      setVisible(true);
      setTitle("UNICORN Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setBackground(new Color(255, 211, 246));
        contentPane.setLayout(new MigLayout("", "[10px,grow,center][150px:n:150px,center][250px:n:250px,center][10px,grow,center][70px:n:70px,center][200px:n:200px,center][10px,grow,center]", "[50px:n,grow,center][50px:n,grow,center][50px:n,grow,center][50px:n,grow,center][50px:n,grow,center]"));
        
        JLabel Title = new JLabel("Login to UNICORN Messenger");
        Title.setHorizontalAlignment(SwingConstants.CENTER);
        Title.setFont(new Font("Tahoma", Font.BOLD, 27));
        contentPane.add(Title, "cell 0 0 7 1,growx,aligny center");
        
        IPAddressField = new JTextField();
        contentPane.add(IPAddressField, "cell 2 1,growx,aligny center");
        IPAddressField.setColumns(20);
        
        PortField = new JTextField();
        contentPane.add(PortField, "cell 5 1,growx,aligny center");
        PortField.setColumns(10);
        
        JLabel lblIP = new JLabel("IP Address:");
        lblIP.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblIP, "cell 1 1,alignx center,aligny center");
        
        JLabel lblPort = new JLabel("Port:");
        lblPort.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblPort, "cell 4 1,alignx center,aligny center");
        
        JLabel lblUser = new JLabel("Username:");
        lblUser.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblUser, "cell 1 2,alignx center,aligny center");
        
        UsernameField = new JTextField();
        contentPane.add(UsernameField, "cell 2 2,growx,aligny center");
        UsernameField.setColumns(10);
        
        passwordField = new JPasswordField();
        contentPane.add(passwordField, "cell 2 3,growx,aligny center");
        passwordField.setColumns(10);
        
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblPassword, "cell 1 3,alignx center,aligny center");
        
        outputText = new JTextArea();
        outputText.setFont(new Font("Tahoma", Font.PLAIN, 20));
        outputText.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        outputText.setBackground(new Color(255, 211, 246));
        outputText.setEditable(false);
        contentPane.add(outputText, "cell 1 4 5 1,growx,aligny center");
        
        JGradientButton btnCreateAccount = new JGradientButton("Create Account");
        btnCreateAccount.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contentPane.add(btnCreateAccount, "cell 4 2 2 1,growx,aligny center");
        
        JGradientButton btnConnect = new JGradientButton("Connect!");
        btnConnect.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contentPane.add(btnConnect, "cell 4 3 2 1,growx,aligny center");
        
        /**The rest of this code in this class was written by 
        *@author Austin
        *
        */
        btnCreateAccount.addActionListener(     
                new ActionListener(){
                    public void actionPerformed(ActionEvent event){
                      createAccount();
                }
            }
        );
        
        btnConnect.addActionListener(       
            new ActionListener(){
                public void actionPerformed(ActionEvent event){
               connect();
            }
        }
    );
   
    
}
        
    /**JGradientButton class written by @author Nick Bollis
     * An extension of JButton to add a gradient to buttons for added style
     */
        public static class JGradientButton extends JButton {
          public JGradientButton(String name) {
              super(name);
              setContentAreaFilled(false);
          }

          @Override
          protected void paintComponent(Graphics g) {
              final Graphics2D g2 = (Graphics2D) g.create();
              g2.setPaint(new GradientPaint(new Point(0, 0), Color.WHITE, new Point(0, getHeight()), Color.PINK.darker()));
              g2.fillRect(0, 0, getWidth(), getHeight());
              g2.dispose();
              super.paintComponent(g);
          }
      }
    

    
    /**
     * Takes the text from the GUI text fields and uses them to create new Messenger Clients
     * Written by @author Austin
     */
  public void connect(){
        try{
            String ip = IPAddressField.getText();
            int port = Integer.parseInt(PortField.getText());
            String user = UsernameField.getText();
            String pass = passwordField.getText();
            
            //We need to run each new client in a Thread because otherwise the program will halt. 
            //RULE OF THUMB: New thread for every new window.
            Thread client = new Thread(new Runnable(){
                public void run(){                  
                    //TODO: Run code to authenticate user with server
                    MessengerClient c = new MessengerClient(ip, port, user, pass);
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

/////NEW METHOD/////////
public void createAccount()
  {   
    try{
      String ip = IPAddressField.getText();
      int port = Integer.parseInt(PortField.getText());
    
      Thread newAccountWindow = new Thread(new Runnable(){
      public void run() {
          // TODO Auto-generated method stub
          createAccount register = new createAccount(ip, port);
          register.setVisible(true);
      }
    });
      newAccountWindow.start();
    }catch(NumberFormatException e){
        JOptionPane.showMessageDialog(null, "Please enter a server name with port to create an account on.");
    }
  }
      
///////     END NEW METHOD    ????????????????????????   
        
 
}
