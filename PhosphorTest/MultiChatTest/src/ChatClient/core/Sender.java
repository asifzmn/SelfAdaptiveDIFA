package ChatClient.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Sender extends Thread
{

	private String imei = "";
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
 	public String getImei() {
 		return this.imei;
 	}
	private PrintWriter _out;
	
	public Sender()
	{
		
	}
//	public Sender(PrintWriter out)
//	{
//		_out = out;
//	}
	
	/**
	 * Until interrupted reads messages from the standard input (keyboard)
	 * and sends them to the chat server through the socket.
	 */
	public void run()
	{
		try 
		{
			//FileInputStream fin = new FileInputStream(new File("rand_input.txt"));
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			//BufferedReader in = new BufferedReader(new InputStreamReader(fin));
			
			while (!isInterrupted()) 
			{
				String message = in.readLine();
				if (message == null) break;
				
				_out.println(message);
				_out.flush();
			}
		}
		catch (IOException ioe) 
		{
			// Communication is broken
		}
	}
}