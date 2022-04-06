package part1_4;

import java.net.*;
import java.io.*;

public class UDPClient {

	public static void main(String args[]) {
		// args[0] = message to be sent
		// args[1] = IP address of the server 1
		// args[2] = IP address of the server 2

		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] m = args[0].getBytes();
			InetAddress server1IP = InetAddress.getByName(args[1]);
			int server1Port = 20001;

			// Communication with server 1.
			DatagramPacket request1 = new DatagramPacket(m, args[0].length(), server1IP, server1Port);
			aSocket.send(request1);
			byte[] buffer = new byte[1000];
			DatagramPacket reply1 = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply1);

			System.out.println("Received Reply: " + new String(reply1.getData(), 0, reply1.getLength()));
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