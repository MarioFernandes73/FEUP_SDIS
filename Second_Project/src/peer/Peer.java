package peer;

import messages.PacketHandler;
import utils.Constants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Peer {

    private static Integer id;
    private static boolean isBootPeer;
    private static String ip;
    private static int port;
    private static String bootPeerIP;
    private static int bootPeerPort;
    private static HashMap<Integer,Address> forwardingTable = new HashMap<>();
    private static int peerLimit;
    private static int networkSize;
    private static DatagramSocket socket;
    
    public static void main(String[] args) throws IOException {

    	if(!verifyArgs(args))
    		return;
    	
        ip = getPublicIP();

        if(isBootPeer)
        	receiveNewPeer();
        else
        	connectToBootPeer();
   
    }

    public static boolean verifyArgs(String args[])
    {
    	if(args.length != 3)
    	{
    		System.out.println("Incorrect number of arguments.");
    		return false;
    	}

    	if(args[0].equals("boot"))
    	{
    		isBootPeer = true;
    		
    		int lim = Integer.parseInt(args[2]);
    		if(lim >= 1 && lim <= 1000)
    		{
    			peerLimit = lim;
    		}
    		else
    		{
    			System.out.println("Error. Invalid peer limit. Should be 1-1000.");
    			return false;
    		}
    	}
    	else if(args[0].equals("normal"))
    	{
    		isBootPeer = false;
    		if(args[2].matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}:[0-9]{1,5}"))
    		{
    			String[] bootPeerAddress = args[2].split(":");
    			bootPeerIP = bootPeerAddress[0];
    			bootPeerPort = Integer.parseInt(bootPeerAddress[1]);
    		}
    		else
    		{
    			System.out.println("Error. Invalid IP:PORT format.");
    			return false;
    		}
    	}
    	else
    	{
    		System.out.println("Error. First argument should be 'boot' or 'normal'.");
    		return false;
    	}
    	
		if(args[1].matches("[0-9]{1,5}"))
		{
			port = Integer.parseInt(args[1]);
		}
		else
		{
			System.out.println("Error. Invalid port.");
			return false;
		}

		System.out.println("Setup successuful.");
		return true;
    }
    
    public static void connectToBootPeer() throws IOException {
    	
        socket = new DatagramSocket();
    	
    	String msg = ip + ":" + port;
       	byte[] data = msg.getBytes();
       	
    	InetAddress bootPeerAddress = InetAddress.getByName(bootPeerIP);
 
        DatagramPacket packet = new DatagramPacket(data, data.length, bootPeerAddress, bootPeerPort);
        socket.send(packet);
        
    }
    
    public static void receiveNewPeer() throws IOException {
        
    	socket = new DatagramSocket(port);
    	
    	byte[] buffer = new byte[256];
    	DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    	socket.receive(packet);
     
    	String msg = new String(packet.getData(), 0, packet.getLength());  	
    	String[] addressParts = msg.split(":");
    	Address address = new Address(addressParts[0], Integer.parseInt(addressParts[1]));
    	
    	forwardingTable.put(forwardingTable.size(), address);
    	showForwardingTable();

    }
    
    public static void showForwardingTable() {
    	System.out.println("\nPeerID - Address");
    	for(Map.Entry<Integer, Address> entry : forwardingTable.entrySet()) {
            Integer key = entry.getKey();
            Address add = entry.getValue();
            System.out.println(key + " - " + add.getIp() + ":" + add.getPort());
        }
    }
    
    public void send(Integer id, byte[] buf) throws IOException {
        Address address = forwardingTable.get(id);
        if(address == null){
            Integer tempId = -1;
            for(Map.Entry<Integer, Address> entry : forwardingTable.entrySet()) {
                Integer key = entry.getKey();
                if(key > tempId && key < id){
                    tempId = key;
                }
            }
            if(tempId == -1){
                System.out.println("DEU -1 NO SEND PARA UM PEER DO PEER " + this.id);
            }
            address = forwardingTable.get(tempId);
        }
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address.getInetAddress(), address.getPort());
        socket.send(packet);
    }

    public void receive() throws IOException {
        byte[] buf = new byte[Constants.MAX_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        new PacketHandler(packet).run();
    }
    
    public static String getPublicIP() throws IOException {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        return in.readLine();
    }

}
