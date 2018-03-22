package utils;
  
public class Message {
	private String operation;
	private String version;
	private boolean senderIdSetted = false;
	private int senderId;
	private String fileId;
	private boolean chunkNoSetted = false;
	private int chunkNo;
	private boolean replicationDegSetted = false;
	private int replicationDeg;
	private String body;
	
	public Message() {}

	public String getHeader() {
		String header = "";
		if(operation != null && version != null && senderIdSetted && fileId != null) {
			header = operation + " " + version + " " + senderId + " " + fileId + " ";
		} else {
			return "";
		}
		if(!operation.equals("DELETE") && chunkNoSetted) {
			header += chunkNo + " ";
		} else {
			return "";
		}
		if(operation.equals("PUTCHUNK") && replicationDegSetted) {
			header += replicationDeg + " ";
		} else {
			return "";
		}
		return header + "\r\n\r\n";
	}
	
	public String getOperation() {
		return operation;
	}
	
	public String getVersion() {
		return version;
	}
	
	public int getSenderId() {
		return senderId;
	}
	
	public String getFileId() {
		return fileId;
	}
	
	public int getChunkNo() {
		return chunkNo;
	}
	
	public int getReplicationDeg() {
		return replicationDeg;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setSenderId(int senderId) {
		this.senderIdSetted = true;
		this.senderId = senderId;
	}
	
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	public void setChunkNo(int chunckNo) {
		this.chunkNoSetted = true;
		this.chunkNo = chunckNo;
	}
	
	public void setReplicationDeg(int replicationDeg) {
		this.replicationDegSetted = true;
		this.replicationDeg = replicationDeg;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
}
