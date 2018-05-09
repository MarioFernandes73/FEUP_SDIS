package client_peer_file_transfer;

public class Partition {
	
	private byte[] data;
	private int partitionNo;
	
	public Partition(byte[] data, int partitionNo) {
		this.data = data;
		this.partitionNo = partitionNo;
	}
}
