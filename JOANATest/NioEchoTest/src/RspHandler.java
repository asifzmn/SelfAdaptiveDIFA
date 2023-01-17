import edu.kit.joana.ui.annotations.Level;
import edu.kit.joana.ui.annotations.Sink;

public class RspHandler {

	private byte[] rsp = null;
	
	public synchronized boolean handleResponse(byte[] rsp) {
		this.rsp = rsp;
		this.notify();
		return true;
	}
	@Sink(level = Level.LOW, lineNumber = 11, columnNumber = 1)
	public synchronized void waitForResponse() {
		while(this.rsp == null) {
			try {
				this.wait();
			} catch (InterruptedException e) {
			}
		}
		
		System.out.println(new String(this.rsp));
	}
}
