
/*
	 * This class contains retrieval and insertion methods for the Oracle database. The code 
	 isn't particularly streamlined, but all the methods do what they are supposed to. I just wanted to make sure this got 
	 uploaded.
	 --Jim
	 */
package MessengerApp;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import oracle.jdbc.driver.*;



public class jdbcConnection{ //implements Connection{

	
	private String dbAdminName;
	private String dbAdminPassword;

	public jdbcConnection(String dbAdminName, String dbAdminPassword) {
		this.setDbAdminName(dbAdminName);
		this.setDbAdminPassword(dbAdminPassword);
	}
	
	/* Takes @param user_id and @param password. Executes SQL query where password is 
	 * verified against the user_id, upon verifying the password
	 *  
	 * */
	public boolean authenticate(String user_id, String password) //throws SQLException
	{
		boolean success=true;
		try{  
		Class.forName("oracle.jdbc.driver.OracleDriver");    
		Connection con=DriverManager.getConnection(  
		"jdbc:oracle:thin:@localhost:1521:xe",dbAdminName ,dbAdminPassword);	
		
		PreparedStatement ps = con.prepareStatement("SELECT password FROM user_data WHERE user_id = ?");
		ps.setString(1, user_id);
		// execute select SQL statement
		ResultSet rs = ps.executeQuery();
		String storedPass = null;
		rs.next();
	
		storedPass = rs.getString("password");
		if (password.equals(storedPass) )
		{
			System.out.println("User " + user_id + " password accepted.");
			con.close();
			success = true;
		}
		
		else
		{		
			System.out.println("Invalid password enter by user " + user_id + ".");
			con.close();
			success =  false;
		}
		
		}catch(Exception e){ 
			System.out.println(e);
			success = false;
		}  
		
		return success;
	}
	
	
	/*takes user data from form and creates account, stored in database. **/
	public void createAccount(String userData)
	{	
		String fields[] = userData.split(" ");
		String user_id = fields[0];
		String password = fields[1];
		String query = "INSERT INTO user_data  VALUES (?,?)";
		try{  
			//load the driver class  
			Class.forName("oracle.jdbc.driver.OracleDriver");  
			// create  the connection object  
			Connection con=DriverManager.getConnection( 
				"jdbc:oracle:thin:@localhost:1521:xe",dbAdminName,dbAdminPassword);
		    PreparedStatement ps = con.prepareStatement(query);
		    ps.setString(1, user_id);
		    ps.setString(2, password );	    
		    ps.executeUpdate();
		    System.out.println("New user " + user_id + " successfully created.");
		    //close the connection object  and release resources
			con.close();
			}catch(Exception e){ System.out.println(e);}  	
	}

	public void setDbAdminName(String dbAdminName) {
		this.dbAdminName = dbAdminName;
	}

	public void setDbAdminPassword(String dbAdminPassword) {
		this.dbAdminPassword = dbAdminPassword;
	}
	
}
	
