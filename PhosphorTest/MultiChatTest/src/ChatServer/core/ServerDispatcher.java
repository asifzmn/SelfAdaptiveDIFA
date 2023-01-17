package ChatServer.core;


import ChatServer.handler.ClientInfo;

import java.net.Socket;
import java.util.ArrayList;

/**
 * This class stores a list of all the received messages and sends all
 * of them to each client.
 * 
 * A list of messages is needed, so that a message will not be lost if
 * the ServerDispatcher is sending a previous message to the clients.
 * 
 * The ServerDispatcher also holds a list of all the clients ( actually
 * a list of all the clients information), so that it can format the
 * received message and send it to all the clients.
 */

public class ServerDispatcher extends Thread
{

	private String imei = "";
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
 	public String getImei() {
 		return this.imei;
 	}
	private ArrayList<String> _messageList = new ArrayList<String>();
	private ArrayList<ClientInfo> _clientsInfoList = new ArrayList<ClientInfo>();
	
	/**
	 * Add a new client to the list. Access to the client info
	 * list needs to be synchronized.
	 */
	public synchronized void addClient(ClientInfo clientInfo)
	{
		_clientsInfoList.add(clientInfo);
	}
	
	/**
	 * Deletes the client from the client pool.
	 */
	public synchronized void deleteClient(ClientInfo clientInfo)
	{
		int clientIndex = _clientsInfoList.indexOf(clientInfo);
		
		if (clientIndex != -1)
		{
			_clientsInfoList.remove(clientIndex);
		}
	}
	
	/**
	 * Formats and adds a new message to the list. It also sends a notify
	 * that will be handled by the getNextMessageFromQueue() method. The
	 * notify tells the getNextMessageFromQueue() method that the message
	 * list is not empty.
	 */
	public synchronized void dispatchMessage(ClientInfo clientInfo, String message)
	{
		Socket socket = clientInfo.getSocket();
		String senderIP = socket.getInetAddress().getHostAddress();
		String senderPort = "" + socket.getPort();
		
		// format the message
		message = senderIP + ":" + senderPort + " : " + message;
		
		_messageList.add(message);
		notify();
	}
	
	/**
	 * Waits until the message list is not empty and then returns the 
	 * first message from the list.
	 */
	private synchronized String getNextMessageFromQueue() throws InterruptedException
	{
		while (_messageList.size() == 0)
		{
			wait();
		}
		
		String message = (String) _messageList.get(0);
		_messageList.remove(0);
		
		return message;
	}
	
	/**
	 * Forwards the message received as a parameter to all the clients from
	 * the clients list. The message is sent to the ClientSender object and
	 * from there it will be sent to the actual client.
	 */
	private synchronized void sendMessageToAllClients(String message)
	{
		for (int i = 0; i < _clientsInfoList.size(); i++) 
		{
			ClientInfo clientInfo = (ClientInfo)_clientsInfoList.get(i);
			clientInfo.getSender().sendMessage(message);
		}
	}
	
	/**
	 * Overrides the run() method inherited from the Thread class. In an
	 * infinitely cycle all the messages from the list are sent to all the
	 * clients.
	 */
	@Override
	public void run()
	{
		try 
		{
			while (true) 
			{
				String message = getNextMessageFromQueue();
				sendMessageToAllClients(message);
			}
		} 
		catch (InterruptedException e) 
		{
			// Thread interrupted. Stop its execution
		}
	}
}