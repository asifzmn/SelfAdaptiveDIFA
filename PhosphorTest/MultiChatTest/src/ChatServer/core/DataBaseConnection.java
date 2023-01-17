package ChatServer.core;
import java.sql.Connection;
import java.sql.DriverManager;

public class DataBaseConnection
{

	private String imei = "";
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
 	public String getImei() {
 		return this.imei;
 	}
	protected Connection conn = null;
	public Connection getConnection()
	{
		return conn;
	}
	protected String host = "localhost";
	protected String port = "3306";
	protected String numeBazaDate = "";
	protected String utilizator = "root";
	protected String parola = "";

//	public DataBaseConnection(String numeBazaDate)
//	{
//		/*this.host = host;
//		this.port = port;*/
//		this.numeBazaDate = numeBazaDate;
//		/*this.utilizator = utilizator;
//		this.parola = parola;*/
//	}
//	
	
	public void creareConexiune()
	{		
		String url = "jdbc:mysql://" + this.host + ":" + this.port + "/";
		String driver = "com.mysql.jdbc.Driver";
		//String driver = "org.gjt.mm.mysql.Driver";
		
		try 
		{
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url + this.numeBazaDate, this.utilizator, this.parola);
			
			System.out.println("Connection created!");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void inchidereConexiune()
	{
		try
		{
			conn.close();
			System.out.println("Connection Closed!");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}