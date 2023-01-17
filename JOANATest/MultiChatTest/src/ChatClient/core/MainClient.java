package ChatClient.core;

import static edu.kit.joana.ui.annotations.Level.HIGH;
import static edu.kit.joana.ui.annotations.Level.LOW;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import edu.kit.joana.ui.annotations.Sink;
import edu.kit.joana.ui.annotations.Source;

/**
 * The client side of the application consist of two classes:
 * 
 * - MainClient class creates the connection with the server and creates
 * a new thread for writing messages to the server. After creating the
 * thread it starts to listen for messages received from the server. By
 * creating a separate thread for that sends messages to the server, we
 * leave the operating system to handle the parallel processing of the
 * write and read operations.
 * 
 * - Sender class only sends the messages that the user inputs, to the
 * server.
 */

public class MainClient
{
	public static  String SERVER_HOSTNAME = "localhost";
	public static  int SERVER_PORT = 3000;
	@Sink(level = LOW, lineNumber = 35, columnNumber = 1)
	static class ShutDownHandler extends Thread {
		 
		Thread th;
 
		public ShutDownHandler(Thread t) {
			th = t;
		}
 
		@Override
		public void run() {
			System.out.println("\nControl+C caught. We clean up before quitting...");
 
			while (th.isAlive()) {
				System.out.println("Main thread is still alive. Waiting...");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("OK cleaned up. Quitting...");
		}
	}
	@Source(level = HIGH, lineNumber = 59, columnNumber = 1)
	@Sink(level = LOW, lineNumber = 60, columnNumber = 1)
	public static void main(String[] args)  throws InterruptedException
	{
/*		Runtime.getRuntime().addShutdownHook( new Thread()
        {
          public void run()
          {
            System.out.println( "Shutdown signal caught!" ) ;
          }
        } ) ;*/
		
		BufferedReader in = null;
		PrintWriter out = null;
		
		if (args.length > 0) {
			SERVER_HOSTNAME = args[0];
		}
		if (args.length > 1) {
			SERVER_PORT = Integer.parseInt(args[1]);
		}
		
		try 
		{
			// Connect to Nakov Chat Server
			Socket socket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			System.out.println("Connected to server " + SERVER_HOSTNAME + ":" + SERVER_PORT);
		}
		catch (IOException e) 
		{
			System.err.println("Can not establish connection to " + 
								SERVER_HOSTNAME + ":" + SERVER_PORT);
			
			e.printStackTrace();
			System.exit(-1);
		}
		
		// request the clients user name and password
		Scanner console = new Scanner(System.in);
		
		System.out.print("Username: ");
		String userName = console.nextLine();
		out.println(userName);
		
		System.out.print("Password: ");
		String password = console.nextLine();
		out.println(password);
		
		out.flush();
		
		try
		{
			String loginResponse = in.readLine();
			
			if (loginResponse.equalsIgnoreCase("ok"))
			{
				System.out.println("Login successful!");
			}
			else
			{
				System.out.println("Login failed!");
				System.exit(-1);
			}
		} 
		catch (IOException e)
		{
			System.out.print("Communication problem.");
			System.exit(-1);
		}
		
		// Create and start Sender thread
		Sender sender = new Sender(out);
		sender.setDaemon(true);
		sender.start();
		
		/*
		ShutDownHandler sdh = new ShutDownHandler(sender);
		Runtime.getRuntime().addShutdownHook( sdh );
		*/
		try 
		{
			// Read messages from the server and print them
			String message;
			while ((message=in.readLine()) != null) 
			{
				System.out.println(message);
			}
		} 
		catch (IOException ioe) 
		{
			System.err.println("Connection to server broken.");
			ioe.printStackTrace();
		}
	}
}