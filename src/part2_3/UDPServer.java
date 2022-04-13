package part2_3;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class UDPServer implements Runnable {

	private Block receivedBLock = null;
	private Block blockToMine = null;
	private InetAddress clientAddress = null;
	private int clientPort = -1;
	private int nonceStart = -1;
	private int nonceEnd = -1;
	private int serverID;

	private static DatagramSocket aSocket;

	public UDPServer(int serverID, int nonceStart, int nonceEnd) {

		// Assign vriables
		this.serverID = serverID;
		this.nonceStart = nonceStart;
		this.nonceEnd = nonceEnd;

	}

	@Override
	public void run() {

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

					System.out.println("Server " + serverID + " Waiting for block to mine from Master Server.");

					// Receive the block from the Master server in json format
					DatagramPacket request = new DatagramPacket(buffer, buffer.length);
					aSocket.setSoTimeout(1800000);
					aSocket.receive(request);

					clientAddress = request.getAddress();
					clientPort = request.getPort();

					// Convert the json data into a block
					String receivedJson = new String(request.getData(), request.getOffset(), request.getLength());
					if (receivedJson.equalsIgnoreCase("stop")) {
						continue;
					}
					System.out.print("Server " + serverID + " Received block json: ");
					System.out.println(receivedJson);
					receivedBLock = StringUtil.getBlock(receivedJson);

					System.out.println("Server " + serverID + " Received block" + ", from Master Server: "
							+ request.getAddress() + ":" + request.getPort());

					blockToMine = receivedBLock;

				} else {

					// Attempt to receive stop command from the client
					DatagramPacket command = new DatagramPacket(buffer, buffer.length);
					aSocket.setSoTimeout(50);

					boolean failed = false;
					try {
						aSocket.receive(command);
					} catch (SocketTimeoutException te) {

						// Break in case there is no block to mine
						if (blockToMine == null) {
							System.out.println("Server " + serverID + " block is null can't mine.");
							continue;
						}

						// Start mining the block
						int miningStatus = blockToMine.mineBlock(nonceStart, nonceEnd);
						if (miningStatus > 0) {

							// block was mined send it to master server
							try {
								aSocket.send(new DatagramPacket(StringUtil.getJson(blockToMine).getBytes(),
										StringUtil.getJson(blockToMine).length(), clientAddress, clientPort));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							receivedBLock = null;
							blockToMine = null;
							clientAddress = null;
							clientPort = -1;

							System.out.println("Server " + serverID + " Mined the block and sent it to Master Server.");

							continue;

						} else {

							// block wasn't mined either stop or continue
							// stop in case nonce limit was reached
							if (miningStatus == -2) {
								failed = true;
							} else {
								continue;
							}

						}

					}

					if (!failed) {

						String commandString = new String(command.getData(), command.getOffset(), command.getLength());

						System.out.println(
								"Server " + serverID + " Received stop command from the Master Server! Pausing....");

					} else {

						System.out.println("Server " + serverID + " Failed to mine the block!");

					}

					receivedBLock = null;
					blockToMine = null;
					clientAddress = null;
					clientPort = -1;

					continue;

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