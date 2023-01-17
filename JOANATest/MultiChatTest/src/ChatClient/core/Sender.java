package ChatClient.core;

import static edu.kit.joana.ui.annotations.Level.HIGH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


import edu.kit.joana.ui.annotations.EntryPoint;
import edu.kit.joana.ui.annotations.EntryPointKind;
import edu.kit.joana.ui.annotations.AnnotationPolicy;
import edu.kit.joana.ui.annotations.Level;
import edu.kit.joana.ui.annotations.Sink;
import edu.kit.joana.ui.annotations.Source;
class Sender extends Thread
{
	private PrintWriter out2;
	
	public Sender(PrintWriter out)
	{
		out2 = out;
	}
	
	/**
	 * Until interrupted reads messages from the standard input (keyboard)
	 * and sends them to the chat server through the socket.
	 */
	@Source(level = HIGH, lineNumber = 32, columnNumber = 1)
	@Sink(level = Level.LOW, annotate = AnnotationPolicy.ANNOTATE_CALLEE)
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
				
				out2.println(message);
				out2.flush();
			}
		}
		catch (IOException ioe) 
		{
			// Communication is broken
		}
	}
}
