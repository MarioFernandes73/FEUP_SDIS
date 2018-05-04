package peer;

import messages.PacketHandler;
import utils.Constants;
import rmi.RMIInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class Peer implements RMIInterface{

    private Integer id;
    private boolean bootPeer;
    private HashMap<Integer,Address> forwardingTable = new HashMap<>();
    private DatagramSocket socket;

    public Peer(Integer id, boolean bootPeer) throws SocketException {
        this.id = id;
        this.bootPeer = bootPeer;
        socket = new DatagramSocket();
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
