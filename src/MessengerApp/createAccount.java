/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MessengerApp;

/**
 *
 * @author tr1ck
 */
public class createAccount extends javax.swing.JFrame {

	private String user;
	private String password;
	private String ip;
	private int port;
	
    /**
     * Creates new form createAccount
     */
    public createAccount(String ip, int port) {
    	this.ip = ip;
    	this.port = port;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        sendBtn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        password2 = new javax.swing.JPasswordField();
        password1 = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        userName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        verificationText = new javax.swing.JLabel();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        sendBtn.setText("Register");
        sendBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendBtnActionPerformed(evt);
            }
        });

        jLabel4.setText("Verify Password:");

        password2.setText("");

        password1.setText("");

        jLabel3.setText("Enter Password:");

        userName.setText("");

        jLabel2.setText("Select user name:");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel1.setText("Enter new user information.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(verificationText, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(sendBtn)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(11, 11, 11)
                                    .addComponent(jLabel4)))
                            .addGap(61, 61, 61)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(password2, javax.swing.GroupLayout.PREFERRED_SIZE, 173,javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(userName, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(password1, javax.swing.GroupLayout.Alignment.TRAILING)))))
                .addContainerGap(91, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(58, 58, 58)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(userName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(password1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(password2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(verificationText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addComponent(sendBtn)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>                        

    private void sendBtnActionPerformed(java.awt.event.ActionEvent evt) {                                        
        String user =  userName.getText();
        String pass1 = new String(password1.getPassword());
        String pass2 = new String(password2.getPassword());
        
        boolean success =validateData(user, pass1, pass2);
        if(!success)
		{
                  success = validateData(user, pass1,pass2);
		}
        else{
            String newAcctData = (user+" "+pass1); //argument for createAccount method in messengerServer
            connectNewUser(newAcctData);
        }
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
			verificationText.setText("All Data Successfully Enterd.");
                        super.update(this.getGraphics());
                        //verificationText.setVisible(true);
            this.user = user;
            this.password = pass1;
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


    // Variables declaration - do not modify                     
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPasswordField password1;
    private javax.swing.JPasswordField password2;
    private javax.swing.JButton sendBtn;
    private javax.swing.JTextField userName;
    private javax.swing.JLabel verificationText;
    // End of variables declaration                   
}


