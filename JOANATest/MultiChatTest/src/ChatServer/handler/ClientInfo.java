package ChatServer.handler;
/**
 * Contains the needed information about a client that is currently
 * connected to the server.
 */

import java.net.Socket;

public class ClientInfo
{
	/**
	 * Holds the socket instance assigned to a
	 * specific client.
	 */
	private Socket _socket = null;
	public void setSocket(Socket socket)
	{
		this._socket = socket;
	}
	public Socket getSocket()
	{
		return _socket;
	}

	/**
	 * Holds the listener instance assigned to a
	 * specific client.
	 */
	private ClientListener _clientListener = null;
	public void setListener(ClientListener clientListener)
	{
		this._clientListener = clientListener;
	}
	public ClientListener getListener()
	{
		return _clientListener;
	}

	/**
	 * Holds the sender instance assigned to a
	 * specific client.
	 */
	private ClientSender _clientSender = null;
	public void setSender(ClientSender clientSender)
	{
		this._clientSender = clientSender;
	}
	public ClientSender getSender()
	{
		return _clientSender;
	}
}