package peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;

public class Main {

    public static void main(String[] args) throws IOException {
        String localIP = InetAddress.getLocalHost().getHostAddress();
        String publicIP = getPublicIP();

        System.out.println("Local IP address: " + localIP);
        System.out.println("Public IP address: " + publicIP);

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

    private static String getPublicIP() throws IOException {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        return in.readLine();
    }
}
