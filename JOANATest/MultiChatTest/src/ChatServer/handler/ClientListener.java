package ChatServer.handler;
/**
 * Listens for a message received from the current client. When a message is
 * received, it will be forwarded to the ServerDispatcher class.
 */


import static edu.kit.joana.ui.annotations.Level.HIGH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ChatServer.core.DataBaseConnection;
import ChatServer.core.ServerDispatcher;
import edu.kit.joana.ui.annotations.Source;

public class ClientListener extends Thread
{
	// reference to the ServerDispatcher instance
	private ServerDispatcher _serverDispatcher;
	// holds general client info
	private ClientInfo _clientInfo;
	// reads from the socket
	private BufferedReader _in;
	
	/**
	 * Initialize the reader and all the general variables.
	 */
	public ClientListener(ClientInfo clientInfo, ServerDispatcher serverDispatcher) throws IOException
	{
		_clientInfo = clientInfo;
		_serverDispatcher = serverDispatcher;
		
		Socket socket = clientInfo.getSocket();
		_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	@Source(level = HIGH, lineNumber = 40, columnNumber = 1)
	/**
	 * Continuously reads messages from the socket, until it is interrupted.
	 */
	@Override
	public void run()
	{		
		try 
		{
			// get login information
			String username = _in.readLine();
			String password = _in.readLine();
			
			String responseMessage;
			//if (validateLogin(username, password))
			if (username.length()>1 && password.length()>1)
			{
				responseMessage = "ok";
			}
			else
			{
				responseMessage = "nok";
			}
			
			_clientInfo.getSender().sendMessage(responseMessage);
			
			while (!isInterrupted())
			{
				String message = _in.readLine();
				if (message == null)
				{
					// if the received message is null, it means that the
					// client was closed, so there is no need to listen
					// for anymore messages.
					break;
				}
				
				// forward the received message to the ServerDispatcher object
				_serverDispatcher.dispatchMessage(_clientInfo, message);
			}
		} 
		catch (IOException e) 
		{
		       // error reading from the socket
		}
		
		// communication ended, so interrupt the sender too
		_clientInfo.getSender().interrupt();
		
		// also delete the current client from the ServerDispatcher clients list.
		_serverDispatcher.deleteClient(_clientInfo);
	}
	
	/**
	 * 
	 */
	private boolean validateLogin(String username, String password)
	{
		try
		{
			DataBaseConnection dbConnection = new DataBaseConnection("test");
			dbConnection.creareConexiune();
			
			Statement statement = dbConnection.getConnection().createStatement();
			String queryCommand = "SELECT * " +
								  "FROM registered_users " +
								  "WHERE username='" + username + "' " +
								  "AND password='"+ password +"';";
			ResultSet statementResult = statement.executeQuery(queryCommand);
			
			if (statementResult.first())
			{
				dbConnection.inchidereConexiune();
				return true;
			}
			else
			{
				dbConnection.inchidereConexiune();
				return false;
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
}

 