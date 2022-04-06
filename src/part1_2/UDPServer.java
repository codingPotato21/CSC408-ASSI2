package part1_2;

import java.net.*;
import java.util.Hashtable;
import java.io.*;

public class UDPServer {
	public static void main(String args[]) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(20000);
			byte[] buffer = new byte[1000];
			System.out.println("Server is ready and accepting clients' requests ... ");
			Hashtable<String, Integer> clients = new Hashtable<String, Integer>();
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				System.out.println("Received from: " + request.getAddress() + "/" + request.getPort());

				String clientID = request.getAddress() + "/" + request.getPort();
				Integer clientSum = clients.get(clientID);
				if (clientSum == null)
					clientSum = 0;
				String msg = new String(request.getData(), 0, request.getLength());
				clientSum += Integer.parseInt(msg);
				clients.put(clientID, clientSum);
				msg = clientSum + "";
				
				DatagramPacket reply = new DatagramPacket(msg.getBytes(), msg.length(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
			
				Thread.sleep(2000);
			
			}
		} catch (SocketException e) {
			System.out.println("Error Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error IO: " + e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}
}