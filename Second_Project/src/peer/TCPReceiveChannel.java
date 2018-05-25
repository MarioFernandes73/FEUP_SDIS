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

    public byte[] receive() throws IOException {
        byte[] buffer = new byte[Constants.MAX_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
    }

}
