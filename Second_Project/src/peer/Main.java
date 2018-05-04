package peer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {

    public static void main(String[] args) {

        try{
                DatagramSocket socket = new DatagramSocket();
                String lol = "LSAODJASODINSAODANSDOANSODANSDIOASNDOI";
                InetAddress address = InetAddress.getByName("89.152.150.172");
                DatagramPacket packet = new DatagramPacket(lol.getBytes(), lol.length(), address, 8080);
                socket.send(packet);
        } catch(Exception e){
            return;
        }


    }
}
