package ChatServer.handler;
/**
 * Sends a message received from the ServerDispatcher to the current client.
 * Access to some resources is synchronized to protect data.
 */

import static edu.kit.joana.ui.annotations.Level.LOW;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.net.Socket;
import java.util.ArrayList;

import ChatServer.core.ServerDispatcher;
import edu.kit.joana.ui.annotations.Sink;

public class ClientSender extends Thread
{
	// list with all the messages that need to be sent to the current client
	private ArrayList<String> _messageList = new ArrayList<String>();
	
	// reference to the ServerDispatcher object.
	private ServerDispatcher _serverDispatcher;
	
	// general information about the client
	private ClientInfo _clientInfo;
	
	// used to write into the client socket
	private PrintWriter _out;
	
	/**
	 * Initialize the writer and all the general variables.
	 */
	public ClientSender(ClientInfo clientInfo, ServerDispatcher serverDispatcher) throws IOException
	{
		_clientInfo = clientInfo;
		_serverDispatcher = serverDispatcher;
		
		Socket socket = clientInfo.getSocket();
		_out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	}
	
	/**
	 * Add a message to the message list and notify the getNextMessageFromQueue()
	 * method that the message list is not empty.
	 */
	public synchronized void sendMessage(String message)
	{
		_messageList.add(message);
		notify();
	}
	
	/**
	 * Returns the first message from the message list
	 */
	private synchronized String getNextMessageFromList() throws InterruptedException
	{
		while (_messageList.size()==0)
		{
			// wait until the list is not empty
			wait();
		}
		
		String message = (String) _messageList.get(0);
		_messageList.remove(0);
		return message;
	}
	@Sink(level = LOW, lineNumber = 68, columnNumber = 1)
	/**
	 * Sends the message received as a parameter to the current client.
	 */
	public void sendMessageToClient(String message)
	{
		_out.println(message);
		_out.flush();
	}
	
	/**
	 * Sends messages to the client until it is interrupted.
	 */
	@Override
	public void run()
	{
		try 
		{
			while (!isInterrupted()) 
			{
				String message = getNextMessageFromList();
				sendMessageToClient(message);
			}
		}
		catch (Exception e) 
		{
			// error writing into the socket
		}
		
		// communication ended, so interrupt the listener too
		_clientInfo.getListener().interrupt();
		
		// remove the current client from the client info list.
		_serverDispatcher.deleteClient(_clientInfo);
	}
}