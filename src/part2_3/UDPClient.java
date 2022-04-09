package part2_3;

import java.net.*;
import java.util.ArrayList;

import java.io.*;

public class UDPClient {

	private static ArrayList<Block> blockchain = new ArrayList<>();
	private static ArrayList<Block> minedBlockchain = new ArrayList<>();
	private static ArrayList<Server> servers = new ArrayList<>();

	public static int difficulty = 2;

	public static void main(String args[]) {
		// args[0] = IP address of the server
		// args[1] = Number of running servers

		try {

			blockchain.add(new Block("SUP im the first block", "0", difficulty));
			blockchain
					.add(new Block("SUP im the second block", blockchain.get(blockchain.size() - 1).hash, difficulty));
			blockchain.add(new Block("SUP im the third block", blockchain.get(blockchain.size() - 1).hash, difficulty));

			for (int i = 1; i <= Integer.parseInt(args[1]); i++) {

				DatagramSocket aSocket = new DatagramSocket();
				InetAddress aHost = InetAddress.getByName(args[0]);
				int serverPort = 20000 + i;

				servers.add(new Server(i, aHost, serverPort, aSocket));

			}

			// Receive mined block from the server
			while (true) {

				if (!blockchain.isEmpty()) {

					// Reset the sockets timeout to the default value
					servers.forEach((server) -> {
						try {
							server.getSocket().setSoTimeout(1800000);
						} catch (SocketException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					});

					Block blockToMine = blockchain.get(0);

					if (!minedBlockchain.isEmpty())
						blockToMine.previousHash = minedBlockchain.get(minedBlockchain.size() - 1).hash;

					// Send the block to the servers
					String jsonBlock = StringUtil.getJson(blockToMine);
					servers.forEach((server) -> {
						DatagramPacket request = new DatagramPacket(jsonBlock.getBytes(), jsonBlock.length(),
								server.getAddress(), server.getPort());
						try {
							server.getSocket().send(request);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});

					// Receive the block in json format from any of the servers
					byte[] buffer = new byte[1000];
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

					boolean receivedMinedBlock = false;
					int winnerServerID = -1;
					while (!receivedMinedBlock) {

						for (int i = 0; i < servers.size(); i++) {

							DatagramPacket serverReply = new DatagramPacket(buffer, buffer.length);
							DatagramSocket socket = servers.get(i).getSocket();
							socket.setSoTimeout(50);

							try {
								socket.receive(serverReply);
							} catch (SocketTimeoutException te) {
								continue;
							}

							reply = serverReply;
							receivedMinedBlock = true;
							winnerServerID = servers.get(i).getServerID();

						}

					}

					// Send stop command to servers
					servers.forEach((server) -> {
						
						String stopString = "stop";
						DatagramPacket stop = new DatagramPacket(stopString.getBytes(), stopString.length(),
								server.getAddress(), server.getPort());
						try {
							server.getSocket().send(stop);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					});

					// Convert the block from json to block object
					Block receivedBlock = StringUtil.getBlock(new String(reply.getData()));

					// Print the hash of the mined block
					System.out.println("Received Mined block from server " + winnerServerID);
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