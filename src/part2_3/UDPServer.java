package part2_3;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class UDPServer {

	private static Block receivedBLock = null;
	private static Block blockToMine = null;
	private static InetAddress clientAddress = null;
	private static int clientPort = -1;
	private int nonceStart = -1;
	private int nonceEnd = -1;
	private int serverID;

	private static DatagramSocket aSocket;

	public UDPServer(int serverID, int nonceStart, int nonceEnd) {

		// Assign vriables
		this.serverID = serverID;
		this.nonceStart = nonceStart;
		this.nonceEnd = nonceEnd;

		// Start the server
		init();

	}

	public void init() {

		try {
			aSocket = new DatagramSocket(20000 + serverID);
			System.out.println("Server " + serverID + " is ready and accepting MasterServer requests ... ");
			System.out.println("Server nonce limit: " + nonceStart + " To: " + nonceEnd);

			byte[] buffer = new byte[1000];
			while (true) {

				if (receivedBLock == null) {

					// Receive the block from the Master server in json format
					DatagramPacket request = new DatagramPacket(buffer, buffer.length);
					aSocket.setSoTimeout(1800000);
					aSocket.receive(request);

					clientAddress = request.getAddress();
					clientPort = request.getPort();

					// Convert the json data into a block
					String receivedJson = new String(request.getData(), request.getOffset(), request.getLength());
					if (receivedJson.equals("stop"))
						continue;
					System.out.println(receivedJson);
					receivedBLock = StringUtil.getBlock(receivedJson);

					System.out.println("Server " + serverID + " Received block" + ", from client: "
							+ request.getAddress() + ":" + request.getPort());

					blockToMine = receivedBLock;

				} else {

					// Attempt to receive stop command from the client
					DatagramPacket command = new DatagramPacket(buffer, buffer.length);
					aSocket.setSoTimeout(50);

					boolean stopped = true;
					try {
						aSocket.receive(command);
					} catch (SocketTimeoutException te) {

						// Start mining the block
						int miningStatus = blockToMine.mineBlock(nonceStart, nonceEnd);
						if (miningStatus > 0) {

							try {
								aSocket.send(new DatagramPacket(StringUtil.getJson(blockToMine).getBytes(),
										StringUtil.getJson(blockToMine).length(), clientAddress, clientPort));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} else {
							if (miningStatus == -2) {
								stopped = false;
							} else {
								continue;
							}
						}

					}

					if (!stopped) {

						String commandString = new String(command.getData(), command.getOffset(), command.getLength());

						System.out
								.println("Server " + serverID + " Received stop command from the client! Pausing....");

					} else {
						System.out.println("Server " + serverID + " Failed to mine the block!");
					}

					receivedBLock = null;
					blockToMine = null;
					clientAddress = null;
					clientPort = -1;

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