package peer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {

    public static void main(String[] args) {

        try{
            DatagramSocket socket = new DatagramSocket();
            byte[] buf = new byte[256];
            InetAddress address = InetAddress.getByName("94.60.106.95");
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 8080);
            socket.send(packet);
        } catch(Exception e){

        }


    }
}
