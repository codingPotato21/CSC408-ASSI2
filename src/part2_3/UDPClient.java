package part2_3;

import java.net.*;
import java.util.ArrayList;

import java.io.*;

public class UDPClient {

	private static ArrayList<Block> blockchain = new ArrayList<>();
	private static ArrayList<Block> minedBlockchain = new ArrayList<>();

	public static int difficulty = 2;

	public static void main(String args[]) {
		// args[0] = IP address of the server

		try {

			blockchain.add(new Block("SUP im the first block", "0", difficulty));
			blockchain
					.add(new Block("SUP im the second block", blockchain.get(blockchain.size() - 1).hash, difficulty));
			blockchain.add(new Block("SUP im the third block", blockchain.get(blockchain.size() - 1).hash, difficulty));

			DatagramSocket aSocket = new DatagramSocket();
			InetAddress aHost = InetAddress.getByName(args[0]);
			int serverPort = 20000;

			// Receive mined block from the server
			while (true) {

				if (!blockchain.isEmpty()) {

					Block blockToMine = blockchain.get(0);

					if (!minedBlockchain.isEmpty())
						blockToMine.previousHash = minedBlockchain.get(minedBlockchain.size() - 1).hash;

					// Send the block to the main server
					String jsonBlock = StringUtil.getJson(blockToMine);
					DatagramPacket request = new DatagramPacket(jsonBlock.getBytes(), jsonBlock.length(), aHost,
							serverPort);
					aSocket.send(request);

					// Receive the block in json format from any of the servers
					byte[] buffer = new byte[1000];
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
					DatagramPacket serverReply = new DatagramPacket(buffer, buffer.length);
					aSocket.receive(serverReply);

					// Convert the block from json to block object
					Block receivedBlock = StringUtil.getBlock(new String(serverReply.getData()));

					// Print the hash of the mined block
					System.out.println("Received Mined block from Master Server ");
					System.out.println("	HASH: " + receivedBlock.hash);
					System.out.println("	PREV. HASH: " + receivedBlock.previousHash);
					System.out.println("#############################################");

					// Add the mined block to the solved blockchain
					minedBlockchain.add(receivedBlock);

					// Remove the unmined block that got mined from the blockchain
					if (!blockchain.isEmpty()) {
						blockchain.remove(0);
					} else {
						break;
					}

				} else {
					break;
				}

			}

			System.out.println("Is blockchain valid? " + isChainValid(minedBlockchain));

		} catch (SocketException e) {
			System.out.println("Error Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error IO: " + e.getMessage());
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