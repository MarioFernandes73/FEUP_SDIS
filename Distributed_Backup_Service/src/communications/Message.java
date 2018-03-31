package communications;

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

	public Message() {
	}

	public String getHeader() {
		String header = "";
		if (operation != null && version != null && senderIdSetted && fileId != null) {
			header = operation + " " + version + " " + senderId + " " + fileId + " ";
		} else {
			return "";
		}
		if (!operation.equals("DELETE") && chunkNoSetted) {
			header += chunkNo + " ";
		} else {
			return "";
		}
		if (operation.equals("PUTCHUNK")) {
			if (replicationDegSetted) {
				header += replicationDeg + " ";
			} else {
				return "";
			}
		}
		header += "\r\n\r\n";
		return header;
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

	public void setChunkNo(int chunkNo) {
		this.chunkNoSetted = true;
		this.chunkNo = chunkNo;
	}

	public void setReplicationDeg(int replicationDeg) {
		this.replicationDegSetted = true;
		this.replicationDeg = replicationDeg;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void prepareMessage(String operation, String version, int senderId, String fileId, int chunkNo,
			int replicationDeg, String body) {
		this.setOperation(operation);
		this.setVersion(version);
		this.setSenderId(senderId);
		this.setFileId(fileId);
		this.setChunkNo(chunkNo);
		if (replicationDeg >= 0)
			this.setReplicationDeg(replicationDeg);
		this.setBody(body);
	}
}
