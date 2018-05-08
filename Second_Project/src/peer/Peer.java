package peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;

public class Peer {

    private static String id;
    private static boolean isBootPeer;
    private static String ip;
    private static int port;
    private static String bootPeerIP;
    private static int bootPeerPort;
    private static ArrayList<Address> forwardingTable = new ArrayList<Address>();
    private static int peerLimit;
    private static int networkSize;
    
    private static DatagramSocket sendSocket;
    private static DatagramSocket receiveSocket;

    public static void main(String[] args) throws IOException {

    	if(!verifyArgs(args))
    		return;
    	
        ip = InetAddress.getLocalHost().getHostAddress();
        //getPublicIP();
        
        id = ip + ":" + port;

        if(isBootPeer)
        {
        	bootPeer();
        }
        else
        {
        	normalPeer();
        }
       
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
    
    public static void normalPeer() throws IOException
    {
    	sendSocket = new DatagramSocket();
    	
       	byte[] data = id.getBytes();
       	
    	InetAddress bootPeerAddress = InetAddress.getByName(bootPeerIP);
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, bootPeerAddress, bootPeerPort);
        sendSocket.send(sendPacket);
        
    	receiveSocket = new DatagramSocket(port);
    	
    	byte[] buffer = new byte[256];
    	DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
    	receiveSocket.receive(receivePacket);
    	
    	String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());  	
    	System.out.println(msg);
    }
    
    public static void bootPeer() throws IOException
    {
    	receiveSocket = new DatagramSocket(port);
		
    	byte[] buffer = new byte[256];
    	DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
    	receiveSocket.receive(receivePacket);
    	 
    	String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());  	
    	String[] addressParts = msg.split(":");
    	String peerIP = addressParts[0];
    	String peerPort = addressParts[1];
    	Address peerAddress = new Address(peerIP, Integer.parseInt(peerPort));
    	
    	sendSocket = new DatagramSocket();
    	
    	String table = "Table\n";
    	for(Address add : forwardingTable)
    		table += add.getIp() + ":" + add.getPort() + "\n";
    	 
    	byte[] data = table.getBytes();
    	 
    	DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(peerIP), Integer.parseInt(peerPort));
    	sendSocket.send(sendPacket);
    	    	
    	forwardingTable.add(peerAddress);
    	showForwardingTable();
    }

    public static void showForwardingTable() {
    	System.out.println("\nForwarding Table");
    	for(Address address : forwardingTable) {
            System.out.println(address.getIp() + ":" + address.getPort());
        }
    }
    
    public static String getPublicIP() throws IOException {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        return in.readLine();
    }

}
