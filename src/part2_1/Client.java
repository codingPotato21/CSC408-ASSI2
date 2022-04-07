package part2_1;

import java.net.InetAddress;
import java.util.ArrayList;

public class Client {

	public ArrayList<Block> blockchain = new ArrayList<>();
	private InetAddress clientAddress;
	private int clientPort;
	
	public Client(InetAddress clientAddress, int clientPort) {
		
		this.clientAddress = clientAddress;
		this.clientPort = clientPort;
		
	}
	
	public InetAddress getAddress() {
		return clientAddress;
	}
	
	public int getPort() {
		return clientPort;
	}
	
}
