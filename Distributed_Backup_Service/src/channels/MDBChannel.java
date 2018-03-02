package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MDBChannel extends Thread{
	
	private InetAddress address = null;
	private int port;
	private MulticastSocket socket;
	
	public MDBChannel(String address, int port) throws IOException, UnknownHostException {
		this.address = InetAddress.getByName(address);
		this.port = port;
		this.socket = new MulticastSocket(port);
		socket.joinGroup(this.address);
	}
	
	 public void run() {
	        System.out.println("Hello from a thread!");
	    }
	 
	 
	 public void send(String msg) throws IOException {
		 
	                DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),
	                        msg.getBytes().length, address, port);
	     
	                System.out.println("Server sent packet with msg: " + msg);                        
	                socket.send(msgPacket);
	                
	 }

	 public String receive() throws IOException {

	                byte[] rbuf = new byte[1024];
	    			// Receive the information and print it.
	    			DatagramPacket msgPacket = new DatagramPacket(rbuf, rbuf.length);
	    			socket.receive(msgPacket);

	    			String msg = new String(rbuf, 0, rbuf.length);
	    			System.out.println("The server has responded with: " + msg);
	    			return msg;
	 }           
	               
}
