package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import utils.Message;
import utils.MessageInterpreter;
import utils.Utils;

public class MulticastChannel implements Runnable {

	private InetAddress address = null;
	private int port;
	private MulticastSocket socket = null;
	private boolean running = false;

	public MulticastChannel(String address, int port) throws IOException, UnknownHostException {
		this.address = InetAddress.getByName(address);
		this.port = port;
		this.socket = new MulticastSocket(port);
		socket.setTimeToLive(1);
		socket.joinGroup(this.address);
	}

	@Override
	public void run() {
		try {
			running = true;
			
			while (running) {
				byte[] data = this.receive();
				String mensagem = new String(data, StandardCharsets.UTF_8);
				MessageInterpreter message = new MessageInterpreter(mensagem);
				message.run();
				//System.out.println("RECEBI ISTO: " + message.getMessage().getHeader());
			}
			
			socket.leaveGroup(this.address);
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(byte[] data) throws IOException {
		DatagramPacket msgPacket = new DatagramPacket(data, data.length, address, port);
		socket.send(msgPacket);
		System.out.println("ENVIEI COISAS!");
	}

	public byte[] receive() throws IOException, SocketTimeoutException {
		byte[] buffer = new byte[Utils.MAX_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		return packet.getData();
	}
	
	public void setSocketTimeout(int timeout) throws SocketException {
		this.socket.setSoTimeout(timeout);
	}

}
