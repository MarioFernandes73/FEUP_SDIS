package communications;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import peer.Peer;
import utils.Utils;

public class MulticastChannel implements Runnable {

	private Peer owner;
	private InetAddress address = null;
	private int port;
	private MulticastSocket socket = null;
	private boolean running = false;

	public MulticastChannel(Peer owner, String address, int port) throws IOException, UnknownHostException {
		this.address = InetAddress.getByName(address);
		this.port = port;
		this.owner = owner;
		this.socket = new MulticastSocket(port);
		socket.setTimeToLive(1);
		socket.joinGroup(this.address);
	}

	@Override
	public void run() {
		try {
			running = true;
			while (running) {
				new MessageFeedback(this.owner, this.receive()).run();
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
	}

	public byte[] receive() throws IOException {
		byte[] buffer = new byte[Utils.MAX_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		return packet.getData();
	}

	public void setSocketTimeout(int timeout) throws SocketException {
		this.socket.setSoTimeout(timeout);
	}

}
