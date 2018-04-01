package communications;

import java.nio.charset.Charset;

import communications.Message;

public class MessageInterpreter {

	private byte[] data;
	
	public MessageInterpreter(byte[] data) {
		this.data = data;
	}
	
	private String getOperationFormat(){
		return "^(PUTCHUNK|STORED|GETCHUNK|CHUNK|DELETE|REMOVED)$";
	}
	
	private String getHeaderFormat(String operation) {
		String version = " (1\\.0)";
		String senderId = " ((0)|([0-9]+))";
		String fileId = " ([A-Za-z0-9]{64})";
		String chunckNo = " ([0-9]{1,6})";
		String replicationDeg = " ([1-9])";
		String crlf = "(\r\n)";
		String body = "((?s).*)";
		String format = "";
		String common = "^" + operation + version + senderId + fileId;
		
		switch(operation) {
			case "PUTCHUNK":
				//PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
				format = common + chunckNo + replicationDeg + " " + crlf + crlf + body + "$";
				break;
			case "STORED":
			case "GETCHUNK":
			case "REMOVED":
				//operation <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
				format = common + chunckNo + "$";
				break;
			case "CHUNK":
				//operation <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>
				format = common + chunckNo + " " + crlf + crlf + body + "$";
				break;
			case "DELETE":
				//DELETE <Version> <SenderId> <FileId> <CRLF><CRLF>
				format = common + "$";
				break;
		}
		return format;
	}
	
	public Message parseText() {
		String text = new String(data, Charset.forName("ISO_8859_1"));
		if(text.indexOf(" ") == -1 || text.indexOf(" \r\n\r\n") < 1) {
			return null;
		}
		//Extract operation
		String operation = text.substring(0, text.indexOf(" "));
		if(!operation.matches(getOperationFormat())) {
			return null;
		}

		if(operation.equals("PUTCHUNK") || operation.equals("CHUNK")) {
			if(!text.matches(getHeaderFormat(operation))) {
				return null;
			}
		} else {
			if(!text.substring(0,text.indexOf(" \r\n\r\n")).matches(getHeaderFormat(operation))) {
				return null;
			}
		}
		
		//Verify header

		
		String rest = text.substring(text.indexOf(" ") + 1);
		Message message = new Message();
		
		//Extract message parameters
		//Operation
		message.setOperation(operation);
		//Version
		message.setVersion(rest.substring(0, rest.indexOf(" ")));
		rest = rest.substring(rest.indexOf(" ") + 1);
		//Sender id
		message.setSenderId(Integer.parseInt(rest.substring(0, rest.indexOf(" "))));
		rest = rest.substring(rest.indexOf(" ") + 1);
		//File id
		message.setFileId(rest.substring(0, rest.indexOf(" ")));
		rest = rest.substring(rest.indexOf(" ") + 1);
		//ChunkNo
		if(!operation.equals("DELETE")) {
			message.setChunkNo(Integer.parseInt(rest.substring(0, rest.indexOf(" "))));
			rest = rest.substring(rest.indexOf(" ") + 1);
		}
		//Replication Degree and body
		if(operation.equals("PUTCHUNK")) {
			message.setReplicationDeg(Integer.parseInt(rest.substring(0, rest.indexOf(" "))));
		}
		if(operation.equals("PUTCHUNK") || operation.equals("CHUNK")) {
			
			byte[] temp = new byte[this.data.length - message.getHeader().length()];
			System.arraycopy(this.data, message.getHeader().length(), temp, 0, this.data.length - message.getHeader().length());
			message.setBody(temp);
			//message.setBody(rest.substring(rest.indexOf(" ") + 5));//+5 to ignore both crlf before body
		}
		return message;
	}
}
