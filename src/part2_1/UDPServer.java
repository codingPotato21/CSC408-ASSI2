package part2_1;

import java.net.*;
import java.util.ArrayList;

import java.io.*;

public class UDPServer {

	private static ArrayList<Block> receivedBlockchain = new ArrayList<>();
	private static ArrayList<Block> minedBlockchain = new ArrayList<>();
	private static int difficulty = 3;
	private static InetAddress aHost;
	private static int serverPort;

	public static void main(String args[]) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(20000);
			System.out.println("Server is ready and accepting clients' requests ... ");

			byte[] buffer = new byte[1000];
			while (receivedBlockchain.size() < 500) {

				// Receive the block from the client in json format
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);

				// Convert the json data into a block
				String receivedJson = new String(request.getData(), request.getOffset(), request.getLength());
				receivedBlockchain.add(StringUtil.getBlock(receivedJson));
				
				System.out.println("Received block: " + receivedBlockchain.size());

				aHost = request.getAddress();
				serverPort = request.getPort();

			}

			// Mine the received blocks
			receivedBlockchain.forEach((block) -> {
				addBlock(block, minedBlockchain);
			});

			// Send the mined blocks 1 by 1 back to the client
			final DatagramSocket clientSocket = aSocket;
			minedBlockchain.forEach((block) -> {

				try {
					clientSocket.send(new DatagramPacket(StringUtil.getJson(block).getBytes(),
							StringUtil.getJson(block).length(), aHost, serverPort));
					Thread.sleep(5);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});

		} catch (SocketException e) {
			System.out.println("Error Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}

	public static void addBlock(Block newBlock, ArrayList<Block> chain) {
		if (chain.size() != 0)
			newBlock.previousHash = chain.get(chain.size() - 1).hash;
		newBlock.mineBlock(difficulty);
		chain.add(newBlock);
	}

}