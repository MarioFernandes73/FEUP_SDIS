package utils;
  
public class Message {
	private String operation;
	private String version;
	private int senderId;
	private String fileId;
	private String chunckNo;
	private int replicationDeg;
	private String body;
	
	public Message() {};
	
	public getFormat(String operation) {
		String operation = "(PUTCHUNK)|(STORED)|(GETCHUNK)|(CHUNK)|(DELETE)|(REMOVED)";
		String version = " [0-9]\\.[0-9]";
		String senderId = " (0)|([0-9]+)";
		String fileId = " [.]{64}";
		String chunckNo = " [0-9]{1,6}";
		String replicationDeg = " [1-9] ";
		String crlf = "\r\n";
		
		switch(operation) {
		case "PUTCHUNK":
			//PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
			break;
		case "STORED":
		case "GETCHUNK":
		case "CHUNK":
		case "REMOVED":
			//operation <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
			break;
		case "DELETE":
			//DELETE <Version> <SenderId> <FileId> <CRLF><CRLF>
			break;
		}
		
	}
}
