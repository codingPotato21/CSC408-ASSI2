package part2_2;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class UDPServer {

	public static ArrayList<Block> minedBlockchain = new ArrayList<>();
	public static ArrayList<Client> clients = new ArrayList<>();

	public static int difficulty = 3;
	private static DatagramSocket aSocket;

	public static void main(String args[]) {
		try {
			aSocket = new DatagramSocket(20000);
			System.out.println("Server is ready and accepting clients' requests ... ");

			byte[] buffer = new byte[1000];
			while (true) {

				// Receive the block from the client in json format
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);

				// Convert the json data into a block
				String receivedJson = new String(request.getData(), request.getOffset(), request.getLength());

				boolean foundClient = false;
				for (int i = 0; i < clients.size(); i++) {

					if (clients.get(i).getAddress().toString().equals(request.getAddress().toString())
							&& clients.get(i).getPort() == request.getPort()) {

						clients.get(i).blockchain.add(StringUtil.getBlock(receivedJson));

						System.out.println("Received block: " + clients.get(i).blockchain.size() + ", from client: "
								+ clients.get(i).getAddress() + ":" + clients.get(i).getPort());

						foundClient = true;

					}

				}

				if (!foundClient) {

					Client newClient = new Client(request.getAddress(), request.getPort());
					newClient.blockchain.add(StringUtil.getBlock(receivedJson));
					clients.add(newClient);

					System.out.println("Received block: " + newClient.blockchain.size() + ", from client: "
							+ newClient.getAddress() + ":" + newClient.getPort());

				}

				// Check if we received the full blockchain for all clients
				boolean startMining = true;
				for (int i = 0; i < clients.size(); i++) {
					if (clients.get(i).blockchain.size() < 500)
						startMining = false;
				}

				if (startMining) {

					// Check if there are any active clients
					if (!clients.isEmpty()) {

						// Loop through all clients in the list
						clients.forEach((client) -> {

							minedBlockchain = new ArrayList<Block>();

							// Mine the received blocks
							for (int i = 0; i < client.blockchain.size(); i++) {
								Block block = client.blockchain.get(i);
								addBlock(block, minedBlockchain);
							}

							client.blockchain = new ArrayList<Block>();

							// Send the mined blocks 1 by 1 back to the client
							minedBlockchain.forEach((block) -> {

								try {
									final DatagramSocket clientSocket = aSocket;
									clientSocket.send(new DatagramPacket(StringUtil.getJson(block).getBytes(),
											StringUtil.getJson(block).length(), client.getAddress(), client.getPort()));
									Thread.sleep(20);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							});

						});

						// Remove clients with resolved blockchains
						ArrayList<Client> resolvedClietnsIndex = new ArrayList<>();
						for (int i = 0; i < clients.size(); i++) {
							if (clients.get(i).blockchain.isEmpty())
								resolvedClietnsIndex.add(clients.get(i));
						}
						resolvedClietnsIndex.forEach((client) -> clients.remove(client));

					}

				}

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

	public static void addBlock(Block newBlock, ArrayList<Block> chain) {
		if (chain.size() != 0)
			newBlock.previousHash = chain.get(chain.size() - 1).hash;
		newBlock.mineBlock(difficulty);
		chain.add(newBlock);
	}

}