package part2_3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class MasterServer {

	private static ArrayList<Server> servers = new ArrayList<>();
	private static Block receivedBLock = null;
	private static Block blockToMine = null;
	private static String minedBlockJson = "";
	private static InetAddress clientAddress = null;
	private static int clientPort = -1;

	private static DatagramSocket aSocket;

	public static void main(String args[]) {
		// args[0] = Number of slave servers
		// args[1] = Nonce starting range for each server ex. 100

		try {
			aSocket = new DatagramSocket(20000);
			System.out.println("Master Server " + " is ready and accepting clients' requests ... ");

			// Create a list of slave servers
			for (int i = 1; i <= Integer.parseInt(args[0]); i++) {

				final int serverID = i;
				if (i == 1) {
					new Thread(() -> new UDPServer(serverID, 0, (Integer.parseInt(args[1])))).start();
				} else {
					new Thread(() -> new UDPServer(serverID,
							((Integer.parseInt(args[1]) * serverID) - Integer.parseInt(args[1])) + 1,
							(Integer.parseInt(args[1]) * serverID))).start();
				}
				DatagramSocket aSocket = new DatagramSocket();
				InetAddress aHost = InetAddress.getByName("127.0.0.1");
				int serverPort = 20000 + i;

				servers.add(new Server(i, aHost, serverPort, aSocket));

			}

			byte[] buffer = new byte[1000];
			while (true) {

				if (receivedBLock == null) {

					// Receive the block from the client in json format
					DatagramPacket request = new DatagramPacket(buffer, buffer.length);
					aSocket.receive(request);

					clientAddress = request.getAddress();
					clientPort = request.getPort();

					// Convert the json data into a block
					String receivedJson = new String(request.getData(), request.getOffset(), request.getLength());
					if (receivedJson.equals("stop"))
						continue;
					receivedBLock = StringUtil.getBlock(receivedJson);

					System.out.println("Master Server " + " Received block" + ", from client: " + request.getAddress()
							+ ":" + request.getPort());

					blockToMine = receivedBLock;

				} else {

					// Send block to all slave servers
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

							minedBlockJson = new String(serverReply.getData(), serverReply.getOffset(),
									serverReply.getLength());
							receivedMinedBlock = true;
							winnerServerID = servers.get(i).getServerID();

						}

					}

					// Print the hash of the mined block
					System.out.println("Received Mined block from server " + winnerServerID);
					System.out.println("Sending stop command to all slave servers...");
					System.out.println("Sending mined block to the client...");
					System.out.println("#############################################");

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

					// Send the solved block to the client
					DatagramPacket solvedBlock = new DatagramPacket(minedBlockJson.getBytes(), minedBlockJson.length(),
							clientAddress, clientPort);
					aSocket.send(solvedBlock);

					receivedBLock = null;
					blockToMine = null;

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

}
