package peer;

import messages.PacketHandler;
import utils.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class TCPReceiveChannel implements Runnable {

    private Peer owner;
    private DatagramSocket socket;
    private boolean running;

    public TCPReceiveChannel(Peer owner) throws SocketException {
        this.owner = owner;
        this.socket = new DatagramSocket(this.owner.getPort());
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

    private byte[] receive() throws IOException {
        byte[] buffer = new byte[Constants.MAX_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        System.out.println("MESSAGE RECEIVED!!!");
        return Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
    }

}
