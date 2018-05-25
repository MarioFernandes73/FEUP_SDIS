package peer;

import client.Client;
import filesmanager.BackedUpFileInfo;
import filesmanager.Chunk;
import filesmanager.FilesManager;
import messages.Message;
import messages.MessagesRecords;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Peer {

    private String id;
    private boolean isBootPeer;
    private String ip;
    private int port;
    private String bootPeerIP;
    private int bootPeerPort;
    private ConcurrentHashMap<String, TCPSendChannel> forwardingTable = new ConcurrentHashMap<>();
    private int peerLimit;
    private int networkSize;

	private ArrayList<String> temporaryContacts = new ArrayList<String>();
	
    private FilesManager filesManager;

    private MessagesRecords records;

    private DatagramSocket sendSocket;
    private DatagramSocket receiveSocket;

    Peer(String args[]) throws IOException {
        if (!verifyArgs(args))
            return;

        this.filesManager = new FilesManager(this.id);
        this.records = new MessagesRecords(this.id);

        ip = InetAddress.getLocalHost().getHostAddress();
        //getPublicIP();

        id = ip + ":" + port;

        if (isBootPeer) {
            bootPeer();
        } else {
            normalPeer();
        }
    }

    private boolean verifyArgs(String args[]) {
        if (args.length != 3) {
            System.out.println("Incorrect number of arguments.");
            return false;
        }

        switch (args[0]) {
            case "boot":
                isBootPeer = true;

                int lim = Integer.parseInt(args[2]);
                if (lim >= 1 && lim <= 1000) {
                    peerLimit = lim;
                } else {
                    System.out.println("Error. Invalid peer limit. Should be 1-1000.");
                    return false;
                }
                break;
            case "normal":
                isBootPeer = false;
                if (args[2].matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}:[0-9]{1,5}")) {
                    String[] bootPeerAddress = args[2].split(":");
                    bootPeerIP = bootPeerAddress[0];
                    bootPeerPort = Integer.parseInt(bootPeerAddress[1]);
                } else {
                    System.out.println("Error. Invalid IP:PORT format.");
                    return false;
                }
                break;
            default:
                System.out.println("Error. First argument should be 'boot' or 'normal'.");
                return false;
        }

        if (args[1].matches("[0-9]{1,5}")) {
            port = Integer.parseInt(args[1]);
        } else {
            System.out.println("Error. Invalid port.");
            return false;
        }

        System.out.println("Setup successuful.");
        return true;
    }

    private void normalPeer() throws IOException {
        sendSocket = new DatagramSocket();

        byte[] data = id.getBytes();

        InetAddress bootPeerAddress = InetAddress.getByName(bootPeerIP);
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, bootPeerAddress, bootPeerPort);
        sendSocket.send(sendPacket);

        receiveSocket = new DatagramSocket(port);

        byte[] buffer = new byte[256];
        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
        receiveSocket.receive(receivePacket);

        String tableInfo = new String(receivePacket.getData(), 0, receivePacket.getLength());
        if (!tableInfo.equals(""))
            fillForwardingTable(tableInfo);
    }

    private void bootPeer() throws IOException {
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

        StringBuilder table = new StringBuilder();
        for(Entry<String, TCPSendChannel> entry : forwardingTable.entrySet())
        {
        	table.append(entry.getKey()).append("\n");
        }

        byte[] data = table.toString().getBytes();

        DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(peerIP), Integer.parseInt(peerPort));
        sendSocket.send(sendPacket);

        forwardingTable.put(peerIP+":"+peerPort, new TCPSendChannel(this, peerAddress));
        showForwardingTable();
    }
	
    public String getContacts(){
    	String contacts = "";
    	for(Entry<String, TCPChannel> entry : forwardingTable.entrySet())
    	{
    		contacts += entry.getKey() + "-";
    	}
    	return contacts;
    }
    
    public boolean hasChunk(String fileID, int chunkNo)
    {
    	return filesManager.hasChunk(fileID, chunkNo);
    }
    
    public void addTemporaryContacts(String contacts){
    	String[] array_contacts = contacts.split("-");
    	for(int i = 0; i < array_contacts.length; i++)
    	{
    		temporaryContacts.add(array_contacts[i]);
    	}
    }
    
	public String chooseContact() {
		// TODO Auto-generated method stub
		return null;
	}

    private void fillForwardingTable(String tableInfo) throws NumberFormatException, UnknownHostException, SocketException {
        String[] rows = tableInfo.split("\n");
        for (String row : rows) {
            String addressParts[] = row.split(":");
            Address address = new Address(addressParts[0], Integer.parseInt(addressParts[1]));
            forwardingTable.put(addressParts[0]+":"+addressParts[1],new TCPSendChannel(this, address));
            System.out.println("Added " + addressParts[0] + ":" + addressParts[1] + " to the the Forwarding Table.");
        }
    }

    private void showForwardingTable() {
        System.out.println("\nForwarding Table:");
        for(Entry<String, TCPSendChannel> entry : forwardingTable.entrySet())
            System.out.println(entry.getKey());
    }
    
    public void setAlivePeer(String peerID)
    {
        forwardingTable.get(peerID).updateLastTimeAlive();
    }
    
    
    /**
     * Returns address of the peer with the id peerID if it exists in the forwardingTable and null otherwise
     * @param peerID String with the id of the peer
     * @return address of the peer if it exists in forwardingTable and null otherwise
     */
    public TCPSendChannel getConnectionAddress(String peerID) {
        return forwardingTable.get(peerID);
    }
    
    /**
     * Adds a peer to the forwardingTable storing its' address using its' id as key
     * Doesn't check if connection limit is exceeded
     * @param peerId String with the new peer's Id
     * @param addressToAdd new peer's Address
     */
    public void addPeer(String peerId, Address addressToAdd) throws SocketException {
		forwardingTable.put(peerId, new TCPSendChannel(this, addressToAdd));
	}
    
    public void removePeer(String peerId) {
		forwardingTable.remove(peerId);
	}
    
    /**
     * Returns the number of connections this peer has
     * @return number of entries in the forwardingTable
     */
    public int getNumberConnections() {
		return forwardingTable.size();
	}
    
    /**
     * Returns the peer's current limit of connections
     * @return peerLimit
     */
    public int getPeerLimit() {
    	return peerLimit;
    }
    
    /**
     * Switches the peerLimit for newLimit if the former is smaller
     * @param newLimit new int value for peerLimit
     */
    public void changePeerLimit(int newLimit) {
    	if(newLimit > peerLimit)
    		peerLimit = newLimit;
    }

    public String getPublicIP() throws IOException {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        return in.readLine();
    }
    
    public String getIP() {
    	return ip;
    }
    
    public int getPort() {
    	return port;
    }

    public String getEncryptedFileName(Client client, File file) {
        return "";
    }

    public boolean checkIfFileExists(String encryptedFileId) {
        return false;
    }

    public ArrayList<Chunk> splitToChunks(File file) {
        return new ArrayList<>();
    }

    public void clearStoredMessagesOfFile(String encryptedFileId) {
    }

    public void updateBackedUpFiles(BackedUpFileInfo newBackedUpFile) {
    }

    public void saveAllInfo() {
        this.filesManager.saveFiles();
    }

    public MessagesRecords getRecords(){
        return this.records;
    }

    public String getId() {
        return this.id;
    }

    public boolean canAddPeers() {
        return this.forwardingTable.size() < this.peerLimit;
    }

    public void sendMessage(String targetId, Message message) {
        for(String peerId : this.forwardingTable.keySet()){
            if(peerId.equals(targetId) ){
                this.forwardingTable.get(peerId); // send message to this address;
                break;
            }
        }
    }

    public void startConnection(String peerId, Address addressToAdd){

    }

    /* RMI methods */
/*
	public String backup(String fileName, int replicationDegree, boolean enhancement) throws RemoteException {
		System.out.println("Starting to backup " + fileName);
		Thread thread = new Thread(new BackupInitiator(this, fileName, replicationDegree, enhancement));
		thread.start();
		return null;
	}
    
    public String restore(String fileName, boolean enhancement) throws RemoteException {
		System.out.println("Starting to restore " + fileName);
		Thread thread = new Thread(new RestoreInitiator(this, fileName));
		thread.start();
		return null;
	}

    public String delete(String fileName, boolean enhancement) throws RemoteException {
		System.out.println("Starting to delete " + fileName);
		Thread thread = new Thread(new FileDeleteProtocol(this, fileName, enhancement));
		thread.start();
		return null;
	}
*/
    
}
