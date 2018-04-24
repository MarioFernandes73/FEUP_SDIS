package messages;

import java.net.DatagramPacket;
import java.util.Arrays;

public class PacketHandler implements Runnable {

    private byte[] packetData;

    public PacketHandler(DatagramPacket packet){
        this.packetData = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
    }

    @Override
    public void run() {
        System.out.println("RECEBI CARALHO");
    }

}
