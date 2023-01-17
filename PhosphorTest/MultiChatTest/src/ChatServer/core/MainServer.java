package ChatServer.core;

import ChatServer.handler.ClientInfo;
import ChatServer.handler.ClientListener;
import ChatServer.handler.ClientSender;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * MainServer class is the place where the server side application starts.
 * It sets up a socket and starts to listen for any clients that want to
 * connect to it.
 * 
 * After the server accepts a connection, it also instantiates three objects:
 * 
 *    - ClientListener object: extends the Thread class and listens for any
 *    messages that the specific client sends, and, then, forwards them
 *    to the ServerDispatcher class;
 *    
 *    - ClientSender object: Has a list of messages that will be sent to the
 *    client.
 *    
 *    - ClientInfo object: Contains the needed information about a client.
 *    Information like: reference to the socket, listener and sender instances
 *    assigned to the specific client and also the user name of the client.
 *    
 * The other important class of the server is the ServerDispatcher. After a
 * message is received by a ClientListener object, the message is sent to this
 * class and, from here, it is sent to all the clients. To be more precise,
 * it is sent to the ClientSender object from all the clients.
 * 
 * There is only one ServerDispatcher thread for all the clients.
 * 
 * The ClientListener, ClientSender and ServerDispatcher classes all extend
 * the Thread class. This enables all of them to run in a separate thread,
 * leaving the operating system to handle their parallel execution. Also,
 * some resources from each class need to be accessed only by one thread at
 * a time, thereby the access to this resources needs to be syncronized.
 */

public class MainServer
{

	private String imei = "";
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
 	public String getImei() {
 		return this.imei;
 	}
	static void __link() {
		//distEA.distMonitor.__link(); 
	}
	
	// the default listening port
	public static int LISTENING_PORT = 3000;
	
	/**
	 * Accept connections.
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException
	{
/*		Runtime.getRuntime().addShutdownHook( new Thread()
        {
          public void run()
          {
            System.out.println( "Shutdown signal caught!" ) ;
          }
        } ) ;*/
		
		// Open server socket for listening
		ServerSocket serverSocket = null;
		
		if (args.length > 0) {
			LISTENING_PORT = Integer.parseInt(args[0]);
		}
		
		try
		{
			// create a new socket that will be used to accept clients
			serverSocket = new ServerSocket(LISTENING_PORT);
			System.out.println("ChatServer started on port " + LISTENING_PORT);
		} 
		catch (IOException e) 
		{
			System.err.println("Can not start listening on port " + LISTENING_PORT);
			e.printStackTrace();
			System.exit(-1);
		}
		
		// start the ServerDispatcher thread
		ServerDispatcher serverDispatcher = new ServerDispatcher();
		serverDispatcher.start();
		
		// Accept and handle client connections
		while (true) 
		{
			try 
			{
				// wait for a client to connect
				Socket socket = serverSocket.accept();
				
				// save the client info
				ClientInfo clientInfo = new ClientInfo();
				clientInfo.setSocket(socket);
				
				// create a listener and sender for the client
				ClientListener clientListener = new ClientListener(); //(clientInfo, serverDispatcher);
				ClientSender clientSender = new ClientSender(); // (clientInfo, serverDispatcher);
				
				// save references to the listener and sender
				clientInfo.setListener(clientListener);
				clientInfo.setSender(clientSender);
				
				// start the listener and sender threads
				clientListener.start();
				clientSender.start();
				
				// add the current client to the clients pool
				serverDispatcher.addClient(clientInfo);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
