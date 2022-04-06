package part1_2;

import java.net.*;
import java.io.*;

public class UDPClient {

	public static void main(String args[]) {
		// args[1] = IP address of the server

		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			InetAddress aHost = InetAddress.getByName(args[0]);
			int serverPort = 20000;
			for (int i = 0; i < 500; i++) {
				String msg = i + "";
				DatagramPacket request = new DatagramPacket(msg.getBytes(), msg.length(), aHost, serverPort);
				aSocket.send(request);
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(reply);
				System.out.println("Received Reply: " + new String(reply.getData(), 0, reply.getLength()));
			}
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