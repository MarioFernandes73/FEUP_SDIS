package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.Message;

public class MessageInterpreter implements Runnable {

	private Message message = null;
	private String text;
	
	public MessageInterpreter(String text) {
		this.text = text;
	}
	
	public Message getMessage() {
		return this.message;
	}
	
	public Pattern getOperationFormat(){
		return Pattern.compile("^(PUTCHUNK|STORED|GETCHUNK|CHUNK|DELETE|REMOVED) ((.|\r|\n)*)$");
	}
	
	public Pattern getHeaderFormat(String operation) {
		String version = " (1\\.0)";
		String senderId = " ((0)|([0-9]+))";
		String fileId = " ([A-Za-z0-9]{64})";
		String chunckNo = " ([0-9]{1,6})";
		String replicationDeg = " ([1-9])";
		String crlf = "(\r\n)";
		String format = "";
		String common = "^" + operation + version + senderId + fileId;
		
		switch(operation) {
		case "PUTCHUNK":
			//PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
			format = common + chunckNo + replicationDeg + " " + crlf + crlf + "(.*)$";
			break;
		case "STORED":
		case "GETCHUNK":
		case "REMOVED":
			//operation <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
			format = common + chunckNo + " " + crlf + crlf + "$";
			break;
		case "CHUNK":
			//operation <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
			format = common + chunckNo + " " + crlf + crlf + "(.*)$";
			break;
		case "DELETE":
			//DELETE <Version> <SenderId> <FileId> <CRLF><CRLF>
			format = common + " " + crlf + crlf+ "$";
			break;
		}
		return Pattern.compile(format);
	}
	
	@Override
	public void run() {	
		//Verify operation
		if(!(getOperationFormat()).matcher(text).matches()) {
			return;
		}	
		//Extract operation
		String operation = text.substring(0, text.indexOf(" "));
		String rest = text.substring(text.indexOf(" ") + 1);
		//Verify header
		if(!this.getHeaderFormat(operation).matcher(text).matches()) {
			return;
		}
		message = new Message();
		
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
			message.setChunckNo(rest.substring(0, rest.indexOf(" ")));
			rest = rest.substring(rest.indexOf(" ") + 1);
		}
		//Replication Degree and body
		if(operation.equals("PUTCHUNK")) {
			message.setReplicationDeg(Integer.parseInt(rest.substring(0, rest.indexOf(" "))));
			message.setBody(rest.substring(rest.indexOf(" ") + 5));//+5 to ignore both crlf before body
		}		
	}
}
