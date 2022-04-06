package part1_3;

import java.net.*;
import java.io.*;

public class UDPClient{    

    public static void main(String args[]) {  
    // args[0] = message to be sent 
    // args[1] = IP address of the server 1 
    // args[2] = IP address of the server 2

        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            byte [] m = args[0].getBytes();
            InetAddress server1IP = InetAddress.getByName(args[1]);
            InetAddress server2IP = InetAddress.getByName(args[2]);
            int server1Port = 20001;
            int server2Port = 20002;
            
            
            // sending and receiving of server1
            DatagramPacket request1 = new DatagramPacket(m,args[0].length(),server1IP,server1Port);
            aSocket.send(request1);			                        
            byte[] buffer = new byte[1000];
            DatagramPacket reply1 = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply1);	
            
            //putting server1 reply in a string
            String replyServer1 = new String(reply1.getData(),0,reply1.getLength());

            //sending and receiving of server2
            DatagramPacket request2 = new DatagramPacket(replyServer1.getBytes(),replyServer1.length(),server2IP,server2Port);
            aSocket.send(request2);		
            DatagramPacket reply2 = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply2);	

            
            //displaying server2 reply
            
            System.out.println("Received Reply: " + new String(reply2.getData(), 0, reply2.getLength()));	
        }catch (SocketException e){System.out.println("Error Socket: " + e.getMessage());
        }catch (IOException e){System.out.println("Error IO: " + e.getMessage());
        }finally { 
            if(aSocket != null) aSocket.close();
        }
    }
}

                       