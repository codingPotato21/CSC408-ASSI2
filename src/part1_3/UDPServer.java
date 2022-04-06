package part1_3;

import java.net.*;
import java.util.Date;
import java.io.*;

public class UDPServer{    
   public static void main(String args[]) { 
	// args [0] = serverID 
	DatagramSocket aSocket = null;
    try{	    	
    	int serverID = Integer.parseInt(args[0]);
	    aSocket = new DatagramSocket(20000+serverID);
	    byte[] buffer = new byte[1000]; 			
	   	System.out.println("Server" + serverID+" is ready and accepting clients' requests ... ");
		while(true){ 				
			DatagramPacket request = new DatagramPacket(buffer,buffer.length);
			aSocket.receive(request);
            String msg = new String(request.getData(),0,request.getLength());
			
            msg+= "Omar's Server"+serverID+ " :) " + new Date()+"   ";
            			
			DatagramPacket reply = new DatagramPacket(msg.getBytes(),
					msg.length(), request.getAddress(),request.getPort());
			aSocket.send(reply);
		}		
 	}catch (SocketException e){System.out.println("Error Socket: " + e.getMessage());
 	}catch (IOException e) {System.out.println("Error IO: " + e.getMessage());
	}finally {
		if(aSocket != null) aSocket.close();
	}
   }
}