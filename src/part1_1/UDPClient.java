package part1_1;

import java.net.*;
import java.io.*;

public class UDPClient {

	public static void main(String args[]) {
		// args[0] = message to be sent to the server;
		// args[1] = IP address of the server

		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] m = args[0].getBytes();
			InetAddress aHost = InetAddress.getByName(args[1]);
			int serverPort = 20000;
			DatagramPacket request = new DatagramPacket(m, args[0].length(), aHost, serverPort);
			aSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			System.out.println("Received Reply: " + new String(reply.getData(), 0, reply.getLength()));
		} catch (SocketException e) {
			System.out.println("Error Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}
}