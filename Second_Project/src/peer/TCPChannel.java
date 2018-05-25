package peer;

import messages.PacketHandler;
import utils.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;

public class TCPChannel implements Runnable {

    private Peer owner;
    private Address address;
    private DatagramSocket socket;
    private Date lastTimeAlive;
    private boolean running;

    public TCPChannel(Peer owner, Address address) throws SocketException {
        this.owner = owner;
        this.address = address;
        this.socket = new DatagramSocket();
    }

    @Override
    public void run() {
        try {
            running = true;
            while (running) {
                new PacketHandler(this.owner,this.receive()).run();
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] data) throws IOException {
        DatagramPacket msgPacket = new DatagramPacket(data, data.length, address.getInetAddress(), address.getPort());
        socket.send(msgPacket);
    }

    public byte[] receive() throws IOException {
        byte[] buffer = new byte[Constants.MAX_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
    }

    public Date getLastTimeAlive() {
        return lastTimeAlive;
    }

    public void updateLastTimeAlive() {
        this.lastTimeAlive = new Date();
    }
}
