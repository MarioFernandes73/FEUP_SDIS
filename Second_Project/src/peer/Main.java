package peer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {

    public static void main(String[] args) {
        String localIP = InetAddress.getLocalHost().getHostAddress();
        String publicIP = getPublicIP();

        System.out.println("Local IP address: " + localIP);
        System.out.println("Public IP address: " + publicIP);

        try{
            DatagramSocket socket = new DatagramSocket();
            byte[] buf = new byte[256];
            InetAddress address = InetAddress.getByName("94.60.106.95");
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 8080);
            socket.send(packet);
        } catch(Exception e){

        }
    }

    private String getPublicIP() {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        return in.readLine();
    }
}
