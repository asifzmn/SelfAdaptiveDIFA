import java.nio.channels.SocketChannel;

class ServerDataEvent {
	private String imei = "";
	
	public void setImei(String imei) {
		this.imei = imei;
	}
	
 	public String getImei() {
 		return this.imei;
 	}
	public NioServer server;
	public SocketChannel socket;
	public byte[] data;
	
	public ServerDataEvent(NioServer server, SocketChannel socket, byte[] data) {
		this.server = server;
		this.socket = socket;
		this.data = data;
	}
}