package utils;
  
public class Message {
	private String operation;
	private String version;
	private int senderId;
	private String fileId;
	private String chunckNo;
	private int replicationDeg;
	private String body;
	
	public Message() {}

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
	
	public String getChunckNo() {
		return chunckNo;
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
		this.senderId = senderId;
	}
	
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	public void setChunckNo(String chunckNo) {
		this.chunckNo = chunckNo;
	}
	
	public void setReplicationDeg(int replicationDeg) {
		this.replicationDeg = replicationDeg;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
}
