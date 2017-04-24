package MessengerApp;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.JPasswordField;
import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

/**
 * This createAccount class presents a GUI that allows users to enter credentials and create a
 * new user account to be used on our server.
 * @author tr1ck and Nick Bollis
 */
public class createAccount extends JFrame {

  
  //Variable Declarations
  private JLabel jLabel1 = new JLabel();
  private JLabel jLabel3 = new JLabel();
  private JLabel jLabel2 = new JLabel();
  private JLabel jLabel4 = new JLabel();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JTextArea jTextArea1 = new JTextArea();
  private JPasswordField password1 = new JPasswordField();
  private JPasswordField password2 = new JPasswordField();
  private LaunchWindow.JGradientButton sendBtn = new LaunchWindow.JGradientButton("Register"); 
  private JTextField userName = new JTextField();
  private JLabel verificationText = new JLabel();
  
  private String user;
  private String password;
  private String ip;
  private int port;
    
    //Create and Display the form
    /**Constructor. This method as well as initComponents are used for GUI construction
     * and event handling. Written by @author Nick Bollis unless otherwise stated.
     * All elements and formatting changes to createAccount were made here and in the
     * initComponent method.
     * Uses MigLayout to auto-resize elements in the case the size of the 
     * window is changed
     */
  
    public createAccount(String ip, int port){
      this.ip=ip;   //written by @author Jim
      this.port=port; //written by @author Jim
      
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
      setTitle("New User");
      setVisible(true);
      setLocationRelativeTo(null);
      setMinimumSize(new Dimension(600, 300));
      setResizable(false);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * 
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        jLabel4.setText("Verify Password:");
        jLabel3.setText("Enter Password:");
        jLabel2.setText("Select user name:");
        jLabel1.setFont(new Font("Tahoma", 1, 16)); // NOI18N
        jLabel1.setText("Enter new user information.");
        getContentPane().setLayout(new MigLayout("", "[228px][73px][44px][129px]", "[20px:n:20px][39px:n:39px][39px:n:39px][39px:n:39px][41px:n:41px][10px:n:10px]"));
        getContentPane().setBackground(new Color(255, 211, 246));
        getContentPane().add(verificationText, "cell 0 5 2 1,growx,aligny top");
        getContentPane().add(jLabel1, "cell 0 0,alignx center,aligny center");
        getContentPane().add(sendBtn, "cell 3 4,alignx center,aligny center");
        getContentPane().add(jLabel3, "cell 0 2,alignx center,aligny center");
        getContentPane().add(jLabel2, "cell 0 1,alignx center,aligny center");
        getContentPane().add(jLabel4, "cell 0 3,alignx center,aligny center");
        getContentPane().add(password2, "cell 1 3 3 1,growx,aligny center");
        getContentPane().add(userName, "cell 1 1 3 1,growx,aligny center");
        getContentPane().add(password1, "cell 1 2 3 1,growx,aligny center");
        sendBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        /**Event handling written by 
         * @author Jim
         */
        sendBtn.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
              sendBtnActionPerformed(evt);
          }
      });
        pack();
    }                      

    private void sendBtnActionPerformed(ActionEvent evt) {                                        
        String user =  userName.getText();
        String pass1 = new String(password1.getPassword());
        String pass2 = new String(password2.getPassword());
        
        boolean success =validateData(user, pass1, pass2);
            if(!success)
        {
                    success = validateData(user, pass1,pass2);
        }
            String newAcctData = user+" "+pass1; //argument for createAccount method in messengerServer
            connectNewUser(newAcctData);
}         
    
    public boolean validateData(String user, String pass1, String pass2)
    {
        if ((user.isEmpty()) || (pass1.isEmpty()) || (pass2.isEmpty()))
                {   
                    verificationText.setText("Please complete all fields");
                    super.update(this.getGraphics());
                    return false;
                }   
        if(pass1.equals(pass2))
        {
            verificationText.setText("All Data Successfully Entered.");
                        super.update(this.getGraphics());
                        //verificationText.setVisible(true);
            this.user=user;
            this.password=pass1;
                        return true;
        }
        else 
        {
            verificationText.setText("Password mismatch. Try again.");
            super.update(this.getGraphics());
                        //verificationText.setVisible(true);
                        return false;
            //add code to reset password fields
        }
}
    
    public void connectNewUser(String data)
    {
      try{
            //We need to run each new client in a Thread because otherwise the program will halt. 
            //RULE OF THUMB: New thread for every new window.
            Thread client = new Thread(new Runnable(){
                public void run(){                  
                    //TODO: Run code to authenticate user with server
                    MessengerClient c = new MessengerClient(ip, port, user, password, data);//allow access of new account create method
                    
                 }
            }); 
            client.start();
            
        //Want to make sure the user is putting in valid text.  
        }catch(NumberFormatException e){
            //outputText.append("Invalid input. Please Try again.\n");
        //Just a generic catch for if something else breaks.
        }catch(Exception e){
            e.printStackTrace();
        }
      }

}               


