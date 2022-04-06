package part1_4;

import java.net.*;
import java.util.Date;
import java.io.*;

public class UDPServer {
	public static void main(String args[]) {
		// args[0] = Server ID

		DatagramSocket aSocket = null;
		InetAddress server2IP = null;
		try {
			int serverID = Integer.parseInt(args[0]);
			if (serverID == 1)
				server2IP = InetAddress.getByName(args[1]);

			aSocket = new DatagramSocket(20000 + serverID);
			byte[] buffer = new byte[1000];
			System.out.println("Server " + serverID + " is ready and accepting clients' requests ... ");
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String msg = new String(request.getData(), 0, request.getLength());

				InetAddress destination;
				int destinationPort;

				if (serverID == 1) {
					msg += " [" + "Server" + serverID + " " + new Date() + ";" + request.getAddress() + ";"
							+ request.getPort() + "] ";

					destination = server2IP;
					destinationPort = 20002;
				} else {
					String received[] = msg.split(";");
					msg += "Server" + serverID + new Date();
					destination = InetAddress.getByName(received[1].replace("/", ""));
					destinationPort = Integer.parseInt(received[2].replace("]", ""));
				}

				DatagramPacket reply = new DatagramPacket(msg.getBytes(), msg.length(), destination, destinationPort);
				aSocket.send(reply);
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