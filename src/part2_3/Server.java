package part2_3;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {

	private int serverID;
	private InetAddress address = null;
	private int port = -1;
	private DatagramSocket socket = null;
	
	public Server(int serverID, InetAddress address, int port, DatagramSocket socket) {
		
		this.serverID = serverID;
		this.address = address;
		this.port = port;
		this.socket = socket;
		
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public DatagramSocket getSocket() {
		return socket;
	}
	
	public int getServerID() {
		return serverID;
	}
	
}
