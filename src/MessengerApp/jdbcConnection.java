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



public class jdbcConnection implements Connection{


	public jdbcConnection() {
		
	}
	
	public jdbcConnection(String dbq){
		String query = dbq; //holds argument for database query
	try{  
	//load the driver class  
	Class.forName("oracle.jdbc.driver.OracleDriver");  
	//create  the connection object  
	Connection con = DriverManager.getConnection(  
	"jdbc:oracle:thin:@localhost:1521:xe","system","system");  
	/*setup is for local oracledatabase.for remote we need our ip and port number here^^,
	XE is the oracle service running, followed by db username and pass*/
	//create the statement object  
	Statement stmt = con.createStatement();  	  
	//execute query 
	ResultSet rs = stmt.executeQuery(query);  
	ResultSetMetaData resultSetMetaData = rs.getMetaData();
	int columnCount = resultSetMetaData.getColumnCount();
    //Cycle through output
	while(rs.next())
	{
		for(int i = 1; i <= columnCount; i++)
		{
	        if(!(i==columnCount))
	        {
	        	System.out.print(rs.getString(i)+"\t");
	        }
	        else
	        {
	            System.out.println(rs.getString(i));
	        }
		}   
	}
	//close the connection object  and release resources
	con.close();  
	}catch(Exception e){ System.out.println(e);}  
}
	
	/*Query displays result set of who is online.**/
	public String[] online()
	{
		String[] onlineUsers = {""};
		String query = "SELECT user_number, user_id FROM user_data where connection_status = 1";
		try{  
			//load the driver class  
			Class.forName("oracle.jdbc.driver.OracleDriver");  
			//create  the connection object  
			Connection con = DriverManager.getConnection(  
			"jdbc:oracle:thin:@localhost:1521:xe","system","system");  
			//create the statement object  
			Statement stmt = con.createStatement();  	  
			//execute query 
			ResultSet rs = stmt.executeQuery(query);  
			ResultSetMetaData resultSetMetaData = rs.getMetaData();
			int columnCount = resultSetMetaData.getColumnCount();
			
		    //Cycle through output
			while(rs.next())
			{
				for(int i = 1; i <= columnCount; i++)
				{
			        	onlineUsers[i] = rs.getString(i);
			        	//System.out.print(rs.getString(i)+"\t");
			    }
			    
			}
			
			//close the connection object  and release resources
			con.close();  
			return onlineUsers;
			
			}catch(Exception e){ 
				System.out.println(e);
				return onlineUsers;
			}  
	}
	
	/*Query displays result set of who is offline.**/
	public void offline()
	{
		String query = "select user_number, user_id FROM user_data where connection_status = 0";
		try{  
			//load the driver class  
			Class.forName("oracle.jdbc.driver.OracleDriver");  
			//create  the connection object  
			Connection con = DriverManager.getConnection(  
			"jdbc:oracle:thin:@localhost:1521:xe","system","system");  
			//create the statement object  
			Statement stmt = con.createStatement();  	  
			//execute query 
			ResultSet rs = stmt.executeQuery(query);  
			ResultSetMetaData resultSetMetaData = rs.getMetaData();
			int columnCount = resultSetMetaData.getColumnCount();
		    //Cycle through output
			while(rs.next())
			{
				for(int i = 1; i <= columnCount; i++)
				{
			        if(!(i==columnCount))
			        {
			        	System.out.print(rs.getString(i)+"\t");
			        }
			        else
			        {
			            System.out.println(rs.getString(i));
			        }
				}   
			}
			//close the connection object  and release resources
			con.close();  
			}catch(Exception e){ System.out.println(e);}  
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
		"jdbc:oracle:thin:@localhost:1521:xe","unicorn" ,"messenger");	
		
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
	
	/* Takes @param user_id. Executes SQL query where @param connection_status is
	 * set to offline (0); 
	 **/
	public void setOffline(String user_id)
	{
			try{  
			//step1 load the driver class  
			Class.forName("oracle.jdbc.driver.OracleDriver");  
			//step2 create  the connection object  
			Connection con=DriverManager.getConnection(  
			"jdbc:oracle:thin:@localhost:1521:xe","system","system");
			

			String query = "UPDATE user_data SET connection_status = '0' where user_id = ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, user_id);
			// execute select SQL statement
			ResultSet rs = ps.executeQuery();
			//String storedPass = null;
			rs.next();
			System.out.println("User " + user_id + "status is set to offline." );
			}catch(Exception e){ System.out.println(e);}  		
	}
		
	/* Takes @param user_id. Executes SQL update where @param connection_status is
	 * set to online (1); 
	 **/
	public void setOnline(String user_id)
	{
			try{  
			Class.forName("oracle.jdbc.driver.OracleDriver");  
			Connection con=DriverManager.getConnection(  
			"jdbc:oracle:thin:@localhost:1521:xe","system","system");

			String query = "UPDATE user_data SET connection_status = '1' where user_id = ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, user_id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			System.out.println("User " + user_id + "status is set to online." );
			}catch(Exception e){ System.out.println(e);}  
	}
	
	//select a user's data for review, 
	/*takes @param user_id and returns basic user info **/
	public void displayData(String user_id)
	{
		try{  
			//step1 load the driver class  
			Class.forName("oracle.jdbc.driver.OracleDriver");  
			//step2 create  the connection object  
			Connection con=DriverManager.getConnection(  
			"jdbc:oracle:thin:@localhost:1521:xe","system","system");
			String query = "SELECT user_id, connection_status, email, first_name, last_name, address, city, state FROM user_data WHERE user_id = ?";////need method to retrieve user name here, or default for self view"
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, user_id);
			ResultSet rs = ps.executeQuery();  
			ResultSetMetaData resultSetMetaData = rs.getMetaData();
			int columnCount = resultSetMetaData.getColumnCount();
		    //Cycle through output
			while(rs.next())
			{
				for(int i =1; i <= columnCount; i++)
				{
					if(!(i == columnCount))
			        {
			           System.out.print(rs.getString(i)+"\t");
			        }
					else
					{
			           System.out.println(rs.getString(i));
					}
				}
			}
			//close the connection object  and release resources
			con.close();
			}catch(Exception e){ System.out.println(e);}  
	}	
	
	/*takes user data from form and creates account, stored in database. User connection status is set to online by default.**/
	public void createAccount(String user_number, String user_id, String password, String email, String first_name, String last_name, String address, String city, String state, String connection_status)
	{
		String query = "INSERT INTO user_data  VALUES (?,?,?,?,?,?,?,?,?,?)";
		try{  
			//load the driver class  
			Class.forName("oracle.jdbc.driver.OracleDriver");  
			// create  the connection object  
			Connection con=DriverManager.getConnection( 
				"jdbc:oracle:thin:@localhost:1521:xe","jims","oracle");
		    PreparedStatement ps = con.prepareStatement(query);
		    ps.setString(1, user_number);
		    ps.setString(2, user_id );
		    ps.setString(3, password );
		    ps.setString(4, email);				    
		    ps.setString(5, first_name);
		    ps.setString(6, last_name);
		    ps.setString(7, address);
		    ps.setString(8, city);
		    ps.setString(9, state );
		    ps.setString(10,connection_status );    
		    ps.executeUpdate();
		    System.out.println("New user " + user_id + " successfully created.");
		    //close the connection object  and release resources
			con.close();
			}catch(Exception e){ System.out.println(e);}  	
	}
	

	
	
/*INTERFACE METHODS*/		
	
	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void abort(Executor executor) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void close() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void commit() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Statement createStatement() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean getAutoCommit() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String getCatalog() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getClientInfo(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getNetworkTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getTransactionIsolation() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isReadOnly() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isValid(int timeout) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String nativeSQL(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void rollback() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setCatalog(String catalog) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setHoldability(int holdability) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Savepoint setSavepoint() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setSchema(String schema) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		// TODO Auto-generated method stub
		
	}}
