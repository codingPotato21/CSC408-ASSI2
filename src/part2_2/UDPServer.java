package part2_2;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class UDPServer {

	private static Block receivedBLock = null;
	private static Block blockToMine = null;
	private static InetAddress clientAddress = null;
	private static int clientPort = 0;

	private static DatagramSocket aSocket;

	public static void main(String args[]) {
		// args[0] = ID of the server

		try {
			aSocket = new DatagramSocket(20000 + Integer.parseInt(args[0]));
			System.out.println("Server is ready and accepting clients' requests ... ");

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
					receivedBLock = StringUtil.getBlock(receivedJson);

					System.out.println("Server " + Integer.parseInt(args[0]) + " Received block" + ", from client: "
							+ request.getAddress() + ":" + request.getPort());

					blockToMine = receivedBLock;

				} else {

					// Receive the block from the client in json format
					DatagramPacket command = new DatagramPacket(buffer, buffer.length);
					aSocket.setSoTimeout(5);

					try {
						aSocket.receive(command);
					} catch (SocketTimeoutException te) {
						
						// Start mining the block
						int miningStatus = blockToMine.mineBlock();
						System.out.println(miningStatus);
						if (miningStatus > 0) {

							try {
								final DatagramSocket clientSocket = aSocket;
								clientSocket.send(new DatagramPacket(StringUtil.getJson(blockToMine).getBytes(),
										StringUtil.getJson(blockToMine).length(), clientAddress, clientPort));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						continue;

					}

					String commandString = new String(command.getData(), command.getOffset(), command.getLength());

					if (commandString.equals("stop")) {

						System.out.println(
								"Server " + Integer.parseInt(args[0]) + " Received a stop command! EXITING....");
						break;

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

}