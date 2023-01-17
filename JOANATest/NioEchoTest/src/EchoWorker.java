import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import edu.kit.joana.ui.annotations.Level;
import edu.kit.joana.ui.annotations.Sink;

public class EchoWorker implements Runnable {

	private List queue = new LinkedList();
	@Sink(level = Level.LOW, lineNumber = 11, columnNumber = 1)
	public void processData(NioServer server, SocketChannel socket, byte[] data, int count) {
		System.out.println("received data from " + socket + " :" + new String(data));
		byte[] dataCopy = new byte[count];
		System.arraycopy(data, 0, dataCopy, 0, count);
		synchronized(queue) {
			queue.add(new ServerDataEvent(server, socket, dataCopy));
			queue.notify();
		}		
	}
	@Sink(level = Level.LOW, lineNumber = 21, columnNumber = 1)
	public void run() {
		ServerDataEvent dataEvent;
		
		while(true) {
			// Wait for data to become available
			synchronized(queue) {
				while(queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
					}
				}
				dataEvent = (ServerDataEvent) queue.remove(0);
			}
			
			// Return to sender
			dataEvent.server.send(dataEvent.socket, dataEvent.data);
			System.out.println("sending data to " + dataEvent.socket + " :" + new String(dataEvent.data));
		}
	}
}
