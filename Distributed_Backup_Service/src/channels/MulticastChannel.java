package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import utils.Utils;

public class MulticastChannel extends Thread {

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
		System.out.println("Hello from a thread!");
		try {

			running = true;
			
			while (running) {
				byte[] data = this.receive();
			}
			
			socket.leaveGroup(this.address);
			socket.close();

		} catch (IOException e) {
			//colocar uma classe qq a tratar das excecoes
			System.out.println("error");
		}
	}

	public void send(byte[] data) throws IOException {
		DatagramPacket msgPacket = new DatagramPacket(data, data.length, address, port);
		socket.send(msgPacket);
	}

	public byte[] receive() throws IOException {
		byte[] buffer = new byte[Utils.MAX_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		return packet.getData();
	}

}
