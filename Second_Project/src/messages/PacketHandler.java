package messages;

import peer.Peer;

import java.net.DatagramPacket;
import java.util.Arrays;

public class PacketHandler implements Runnable {

    private Peer peer;
    private byte[] packetData;
    private Message message;

    public PacketHandler(DatagramPacket packet){
        this.packetData = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
    }

    @Override
    public void run() {

    }

}
