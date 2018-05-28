package peer;

import client.Client;
import client_peer_file_transfer.ClientChunkTransfer;
import client_peer_file_transfer.Partition;
import filesmanager.BackedUpFileInfo;
import filesmanager.Chunk;
import filesmanager.FilesManager;
import messages.Message;
import messages.MessageBuilder;
import messages.MessagesRecords;
import protocols.initiators.BackupInitiator;
import protocols.initiators.DeleteInitiator;
import protocols.initiators.RestoreInitiator;
import protocols.protocols.CheckContactsAlive;
import protocols.protocols.CheckIfNeedConnections;
import protocols.protocols.SendAlive;
import rmi.RMIInterface;
import utils.Constants;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Peer implements RMIInterface {

    private String id;
    private String accessPoint;
    private boolean isBootPeer;
    private String ip;
    private int port;
    private Address bootPeerAddress;
    private ConcurrentHashMap<String, TCPSendChannel> forwardingTable = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Address> backupForwardingTable = new ConcurrentHashMap<>();
    private int peerLimit = Constants.DEFAULT_CONNECTION_LIMIT;

    private ConcurrentHashMap<String, ArrayList<Chunk>> clientTransferChunks = new ConcurrentHashMap<>(); // <fileId, chunks>

	private ArrayList<String> temporaryContacts = new ArrayList<String>();
	
    private FilesManager filesManager;

    private MessagesRecords records;

    private TCPReceiveChannel receiveChannel;
    private CheckContactsAlive checkContactsAlive;
	private SendAlive sendAlive;

    Peer(String args[]) throws IOException {
        if (!verifyArgs(args))
            return;
        System.out.println("Everything ok");
        this.ip = InetAddress.getLocalHost().getHostAddress();
        //this.ip = getPublicIP();

        this.id = ip + "." + port;

        this.filesManager = new FilesManager(this.id);
        this.records = new MessagesRecords(this.id);

        this.receiveChannel = new TCPReceiveChannel(this);
        new Thread(this.receiveChannel).start();

        if(!isBootPeer){
            String[] msgArgs = new String[]{
                    Constants.MessageType.CONNECT.toString(),
                    this.getId(),
                    this.ip + ":" + Integer.toString(this.port)
            };
            this.sendMessageToAddress(this.bootPeerAddress, MessageBuilder.build(msgArgs));
        }
        
        this.checkContactsAlive = new CheckContactsAlive(this);
        new Thread(this.checkContactsAlive).start();
        this.sendAlive = new SendAlive(this);
        new Thread(this.sendAlive).start();
        new Thread(new CheckIfNeedConnections(this)).start();

        System.out.println("Setup successuful.");
    }

    private boolean verifyArgs(String args[]) {


        switch (args[0]) {
            case "boot":
                if (args.length != 3) {
                    System.out.println("Incorrect number of arguments.");
                    return false;
                }

                this.accessPoint = args[2];
                isBootPeer = true;

                break;
            case "normal":
                if (args.length != 4) {
                    System.out.println("Incorrect number of arguments.");
                    return false;
                }
                isBootPeer = false;
                if (args[2].matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}:[0-9]{1,5}")) {
                    String[] bootPeerAddress = args[2].split(":");
                    try {
                        this.bootPeerAddress = new Address(bootPeerAddress[0], Integer.parseInt(bootPeerAddress[1]));
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                } else {
                    System.out.println("Error. Invalid IP:PORT format.");
                    return false;
                }
                this.accessPoint = args[3];
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

        return true;
    }
	
    public String getContactsExcept(String senderId){
    	StringBuilder contacts = new StringBuilder();
    	for(Entry<String, TCPSendChannel> entry : forwardingTable.entrySet())
    	{
    	    if(entry.getKey().equals(senderId)){
    	        continue;
            }
    		contacts.append(entry.getKey())
                    .append("-")
                    .append(entry.getValue().getAddress().getIp())
                    .append(":")
                    .append(entry.getValue().getAddress().getPort())
                    .append("-");
    	}
    	return contacts.toString();
    }

    public String getAccessPoint() {
        return this.accessPoint;
    }

    public boolean hasChunk(String chunkId)
    {
    	return filesManager.hasChunk(chunkId);
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
    
    public ConcurrentHashMap<String, TCPSendChannel> getForwardingTable() {
        return forwardingTable;
    }
    
    public void updateBackupTable(HashMap<String, Address> backupForwardingTable)
    {
    	this.backupForwardingTable.clear();
    	for(Entry<String, Address> entry : backupForwardingTable.entrySet())
			this.backupForwardingTable.put(entry.getKey(), entry.getValue());
    }
    
    public ConcurrentHashMap<String, Address> getBackupForwardingTable()
    {
    	return backupForwardingTable;
    }
    
    public void setAlivePeer(String peerID)
    {
        if(forwardingTable.containsKey(peerID))
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
		forwardingTable.get(peerId).updateLastTimeAlive();
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

    public boolean checkIfFileExists(String encryptedFileId) {
        return false;
    }

    public ArrayList<Chunk> splitToChunks(File file, String encryptedFileId) {
        ArrayList<Chunk> chunks = new ArrayList<>();
        int partitionSize = Constants.MAX_CHUNK_SIZE;
        int partitionsQuantity = (int) (file.length() / partitionSize) + 1;
        byte[] fileBytes = new byte[0];
        try {
            fileBytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int partitionNo = 0; partitionNo < partitionsQuantity; partitionNo++) {

            if (partitionNo == partitionsQuantity - 1) {
                partitionSize = fileBytes.length % Constants.MAX_CHUNK_SIZE;
            }

            // copy file data to partition
            int partitionStart = partitionNo * Constants.MAX_CHUNK_SIZE;
            byte[] partitionData = new byte[partitionSize];
            int byteCounter = 0;

            System.arraycopy(fileBytes, partitionStart, partitionData, 0, partitionSize);

            chunks.add(new Chunk( encryptedFileId + partitionNo, partitionData));
        }
        return chunks;
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

    public BackedUpFileInfo getBackedUpFileInfo(String fileId){
        return this.filesManager.getBackedUpFileInfo(fileId);
    }

    public void sendMessage(String targetId, Message message) throws IOException {
        for(Map.Entry<String, TCPSendChannel> connectedPeer : this.forwardingTable.entrySet()){
            if(connectedPeer.getKey().equals(targetId) ){
                connectedPeer.getValue().send(message.getBytes());
                System.out.println("MESSAGE SENT " + message.getHeader());
                break;
            }
        }
    }

    public void sendMessageToAddress(Address address, Message message) throws IOException{
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket msgPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, address.getInetAddress(), address.getPort());
        socket.send(msgPacket);
        System.out.println("MESSAGE SENT TO ADDRESS " + address.toString());
    }

    public void sendFloodMessage(Message message) throws IOException{
        for(Entry<String, TCPSendChannel> entry : this.forwardingTable.entrySet()){
            entry.getValue().send(message.getBytes());
        }
    }



    public void startConnection(String peerId, Address addressToAdd){

    }

    public String encryptFileName(String fileName, String clientId) throws NoSuchAlgorithmException {
    	String temp = fileName + clientId;

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(temp.getBytes(StandardCharsets.UTF_8));

		return DatatypeConverter.printHexBinary(hash);
    }

    public boolean deleteChunk(String chunkId){
        return this.filesManager.deleteChunk(chunkId);
    }

    /* RMI methods */

	public int backup(String clientId, String fileName, int replicationDegree) throws RemoteException {
        String fileId = "";
        try {
            fileId = encryptFileName(fileName, clientId);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error encrypting file id");
            return Constants.PEER_ERROR;
        }

        if(!clientTransferChunks.containsKey(fileId)) {
            System.out.println("File " + fileName + " was not previously transferred from client " + clientId);
            return Constants.FILE_CHUNKS_NOT_RECEIVED;
        }

		System.out.println("Starting to backup " + fileName);
		Thread thread = new Thread(new BackupInitiator(this, clientId, fileName, replicationDegree));
		thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
	}

    public int restore(String clientId, String fileName) throws RemoteException {
		System.out.println("Starting to restore file " + fileName + " from client " + clientId);
		Thread thread = new Thread(new RestoreInitiator(this, clientId, fileName));
		thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
	}

    public int delete(String clientId, String fileName) throws RemoteException {
		System.out.println("Starting to delete " + fileName + " from client  " + clientId);
		Thread thread = new Thread(new DeleteInitiator(this, clientId, fileName));
		thread.start();
		return 0;
	}

    // Obtain file chunk from client
	public int transferFileChunk(String clientId, String fileName, int chunkNo, byte[] chunkContent) throws RemoteException {
        String fileId = "";
        try {
            fileId = encryptFileName(fileName, clientId);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error encrypting file id");
            return Constants.FILE_CHUNK_TRANSFER_ERROR;
        }

        Thread thread = new Thread(new ClientChunkTransfer(fileId, new Chunk(fileId + chunkNo, chunkContent), this));
        thread.start();

        return Constants.SUCCESS;
	}
	
	// Send file chunk to client
	public byte[] getFileChunk(String clientId, String fileName, int chunkNo) throws RemoteException {
        String fileId;
        try {
             fileId = encryptFileName(fileName, clientId);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error encrypting file id");
            return null;
        }

        ArrayList<Chunk> fileChunks = clientTransferChunks.get(fileId);
        if(fileChunks == null)
            return null;

        String chunkId = fileId + chunkNo;
        byte[] chunkData = null;
        for(int i = 0; i < fileChunks.size(); i++) {//get chunk and delete it
            Chunk chunk = fileChunks.get(i);
            if(chunk.getChunkId().equals(chunkId)) {
                chunkData = chunk.getData();
                fileChunks.remove(i);
                break;
            }
        }

        if(fileChunks.isEmpty())
            clientTransferChunks.remove(fileId);

        return chunkData;
	}

    public boolean chunkTransferDuplicated(String fileId, Chunk chunk) {
        ArrayList<Chunk> fileChunks = clientTransferChunks.get(fileId);
        if(fileChunks == null)
            return false;

        //Search on file chunks
        for(Chunk c: fileChunks) {
            if(c.getChunkId() == chunk.getChunkId()) {
                return true;
            }
        }
        return false;
    }

    public void addClientTransferChunk(String fileId, Chunk chunk) {
        ArrayList<Chunk> fileChunks = clientTransferChunks.get(fileId);
        if(fileChunks == null) { //no chunks belonging to this file have been added
            ArrayList<Chunk> newFileChunks = new ArrayList<Chunk>();
            newFileChunks.add(chunk);
            clientTransferChunks.putIfAbsent(fileId, newFileChunks);
        }
    }
    public ArrayList<Chunk> getClientTransferFileChunks(String fileId) {
	    return clientTransferChunks.get(fileId);
    }

    public void eliminateClientTransferFileChunks(String fileId) {
        clientTransferChunks.remove(fileId);
    }

	public void saveChunk(Chunk chunk){
	    this.filesManager.saveChunk(chunk);
    }

	public Chunk getChunk(String chunkId) {
	    return this.filesManager.getChunk(chunkId);
	}

	public boolean saveBackedUpFileInfo(BackedUpFileInfo info){
	    return this.filesManager.addBackedUpFileInfo(info);
    }

    public void addClientTransferChunks(String fileName, ArrayList<Chunk> chunks) {
	    this.clientTransferChunks.put(fileName, chunks);
    }

    public void addChunkInfo(ChunkInfo chunkInfo){
	    this.filesManager.addChunkInfo(chunkInfo);
    }
}
