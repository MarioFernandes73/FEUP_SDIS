package peer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Address {

    private InetAddress inetAddress;
    private String ip;
    private int port;

    public Address(String ip, int port) throws UnknownHostException {
        this.ip = ip;
        this.port = port;
        this.inetAddress = InetAddress.getByName(ip);
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }
    
    @Override
    public String toString()
    {
    	String ret = "";    	
    	ret += ip + " " + Integer.toString(port);

    	return ret;
    }
}
