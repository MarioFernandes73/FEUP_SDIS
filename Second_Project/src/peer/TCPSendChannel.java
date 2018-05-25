package peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Date;

public class TCPSendChannel {

    private Peer owner;
    private Address address;
    private DatagramSocket socket;
    private Date lastTimeAlive;

    public TCPSendChannel(Peer owner, Address address) throws SocketException {
        this.owner = owner;
        this.address = address;
        this.socket = new DatagramSocket();
    }

    public void send(byte[] data) throws IOException {
        DatagramPacket msgPacket = new DatagramPacket(data, data.length, address.getInetAddress(), address.getPort());
        socket.send(msgPacket);
    }

    public Date getLastTimeAlive() {
        return lastTimeAlive;
    }

    public void updateLastTimeAlive() {
        this.lastTimeAlive = new Date();
    }
}
