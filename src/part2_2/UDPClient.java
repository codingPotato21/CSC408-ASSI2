package part2_2;

import java.net.*;

import java.io.*;

public class UDPClient {

	public static int difficulty = 3;

	public static void main(String args[]) {
		// args[0] = IP address of the server
		// args[1] = Port number of the 1st server

		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			InetAddress aHost = InetAddress.getByName(args[0]);
			int serverPort = 20000 + Integer.parseInt(args[1]);

			Block blockToMine = new Block("I am a new block", "0", difficulty);

			// Send the block to the server
			String jsonBlock = StringUtil.getJson(blockToMine);
			DatagramPacket request = new DatagramPacket(jsonBlock.getBytes(), jsonBlock.length(), aHost, serverPort);
			aSocket.send(request);

			// Receive mined block from the server
			while (true) {

				// Receive the block in json format from the server
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(reply);

				// Convert the block from json to block object
				Block receivedBlock = StringUtil.getBlock(new String(reply.getData()));

				// Print the hash of the mined block
				System.out.println("Received block: ");
				System.out.println("	HASH: " + receivedBlock.hash);
				System.out.println("	PREV. HASH: " + receivedBlock.previousHash);
				System.out.println("#############################################");

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