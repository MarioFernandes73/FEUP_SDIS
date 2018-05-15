package peer;

import client.Client;
import filesmanager.BackedUpFileInfo;
import filesmanager.Chunk;
import filesmanager.FilesManager;
import messages.MessagesRecords;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Peer {

    private String id;
    private boolean isBootPeer;
    private String ip;
    private int port;
    private String bootPeerIP;
    private int bootPeerPort;
    private ArrayList<Address> forwardingTable = new ArrayList<Address>();
    private int peerLimit;
    private int networkSize;

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
        for (Address add : forwardingTable)
            table.append(add.getIp()).append(":").append(add.getPort()).append("\n");

        byte[] data = table.toString().getBytes();

        DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(peerIP), Integer.parseInt(peerPort));
        sendSocket.send(sendPacket);

        forwardingTable.add(peerAddress);
        showForwardingTable();
    }

    private void fillForwardingTable(String tableInfo) throws NumberFormatException, UnknownHostException {
        String[] rows = tableInfo.split("\n");
        for (String row : rows) {
            String addressParts[] = row.split(":");
            Address address = new Address(addressParts[0], Integer.parseInt(addressParts[1]));
            forwardingTable.add(address);
            System.out.println("Added " + addressParts[0] + ":" + addressParts[1] + " to the the Forwarding Table.");
        }
    }

    private void showForwardingTable() {
        System.out.println("\nForwarding Table:");
        for (Address address : forwardingTable) {
            System.out.println(address.getIp() + ":" + address.getPort());
        }
    }

    public String getPublicIP() throws IOException {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        return in.readLine();
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

}
