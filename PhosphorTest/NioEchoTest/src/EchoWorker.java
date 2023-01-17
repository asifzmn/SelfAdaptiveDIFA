import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class EchoWorker implements Runnable {
	private String imei = "";
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
 	public String getImei() {
 		return this.imei;
 	}
	private List queue = new LinkedList();
	
	public void processData(NioServer server, SocketChannel socket, byte[] data, int count) {
		System.out.println("received data from " + socket + " :" + new String(data));
		byte[] dataCopy = new byte[count];
		System.arraycopy(data, 0, dataCopy, 0, count);
		synchronized(queue) {
			queue.add(new ServerDataEvent(server, socket, dataCopy));
			queue.notify();
		}		
	}
	
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
