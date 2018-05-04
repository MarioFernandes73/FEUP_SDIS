package peer;

import messages.PacketHandler;
import utils.Constants;
import rmi.RMIInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Peer implements RMIInterface{

    private Integer id;
    private static boolean isBootPeer;
    private static String ip;
    private static String bootPeerIP;
    private HashMap<Integer,Address> forwardingTable = new HashMap<>();
    private static int networkSize;
    private static int peerLimit;
    private DatagramSocket socket;
    

    public Peer(Integer id, boolean isBootPeer) throws SocketException {
        this.id = id;
        this.isBootPeer = isBootPeer;
        socket = new DatagramSocket();
    }
    
    public static void main(String[] args) {

    	if(setupPeer(args) != 0)
    		return;
    	
    	/*...*/
    	
    }
    
    public static int setupPeer(String args[]){

    	int verify = verifyArgs(args);
    	
    	switch(verify)
    	{
    		case 0:
    			System.out.println("Setup successuful.");
    			break;
    		case 1:
    			System.out.println("Incorrect number of arguments.");
    			break;
    		case 2: 
    			System.out.println("Error. First argument should be 'boot' or 'normal'");
    			break;
    		case 3:
    			System.out.println("Error. Invalid peer limit. Should be 1-1000");
    			break;
    		case 4:
    			System.out.println("Error. Invalid IP:PORT format");
    			break;
    	}
		
    	return verify;
    	
    }
    
    public static int verifyArgs(String args[])
    {
    	if(args.length != 2)
    		return 1;
    	
    	if(args[0].equals("boot"))
    	{
    		isBootPeer = true;
    		int lim = Integer.parseInt(args[1]);
    		if(lim >= 1 && lim <= 1000)
    		{
    			peerLimit = lim;
    		}
    		else
    		{
    			return 3;
    		}
    	}
    	else if(args[0].equals("normal"))
    	{
    		isBootPeer = false;
    		if(args[1].matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}:[0-9]{1,5}"))
    		{
    			bootPeerIP = args[1];
    		}
    		else
    			return 4;
    	}
    	else 
    		return 2;
    	
		return 0;
    }
    
    public static String getPublicIP() {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        return in.readLine();
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

    //RMI Funtions
    @Override
    public String backup(String fileName, int replicationDegree, boolean enhancement) throws RemoteException {

    }

    @Override
    public String restore(String fileName, boolean enhancement) throws RemoteException {

    }

    @Override
    public String delete(String fileName, boolean enhancement) throws RemoteException {

    }

    @Override
    public String reclaim(int space, boolean enhancement) throws RemoteException {

    }

    @Override
    public String state() throws RemoteException {

    }
}
