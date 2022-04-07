package part2_1;

import java.net.*;
import java.util.ArrayList;

import java.io.*;

public class UDPClient {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static ArrayList<Block> calculatedBlockchain = new ArrayList<Block>();
	public static int difficulty = 3;

	public static void main(String args[]) {
		// args[1] = IP address of the server

		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			InetAddress aHost = InetAddress.getByName(args[0]);
			int serverPort = 20000;

			// Create a new blockchain with 500 blocks
			for (int i = 0; i < 500; i++) {

				blockchain.add(new Block("SUP, I am block " + i + 1, "" + i));

			}

			// Send the blocks to the server
			for (int i = 0; i < blockchain.size(); i++) {

				Block block = blockchain.get(i);
				String jsonBlock = StringUtil.getJson(block);
				DatagramPacket request = new DatagramPacket(jsonBlock.getBytes(), jsonBlock.length(), aHost,
						serverPort);
				aSocket.send(request);
				
				Thread.sleep(50);

			}

			// Receive mined blocks from the server
			while (!blockchain.isEmpty()) {

				// Receive the block in json format from the server
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(reply);

				// Convert the block from json to block object
				Block receivedBlock = StringUtil.getBlock(new String(reply.getData()));
				calculatedBlockchain.add(receivedBlock);
				blockchain.remove(blockchain.size() - 1);

				// Print the hash of the mined block
				System.out.println("Received block: " + calculatedBlockchain.size());
				System.out.println("	HASH: " + receivedBlock.hash);
				System.out.println("	PREV. HASH: " + receivedBlock.previousHash);
				System.out.println("#############################################");

			}

			System.out.println("Is blockchain valid? " + isChainValid(calculatedBlockchain));

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

	public static Boolean isChainValid(ArrayList<Block> chain) {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');

		// loop through blockchain to check hashes:
		for (int i = 1; i < chain.size(); i++) {
			currentBlock = chain.get(i);
			previousBlock = chain.get(i - 1);
			// compare registered hash and calculated hash:
			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				System.out.println("Current Hashes not equal");
				return false;
			}
			// compare previous hash and registered previous hash
			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			// check if hash is solved
			if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}

		}
		return true;
	}

}