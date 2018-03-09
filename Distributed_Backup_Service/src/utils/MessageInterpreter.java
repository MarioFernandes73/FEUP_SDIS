package utils;

public class MessageInterpreter {

	private String message;
	
	public MessageInterpreter(String message) {
		this.message = message;
	}
	
	public void run() {	
		//Verify message header
		String operation = "(PUTCHUNK)|(STORED)|(GETCHUNK)|(CHUNK)|(DELETE)|(REMOVED)";
		String version = " [0-9]\.[0-9]";
		String senderId = " (0)|([0-9]+)";
		String fileId = " [.]{64}";
		String chunckNo = " [0-9]{1,6}";
		String replicationDeg = " [1-9] ";
		String crlf = "\r\n";
		String headerFormat = "^" + operation + version + senderId + fileId + chunckNo + replicationDeg + crlf + crlf;
		if(!message.matches(headerFormat)) {
			//incorrect message format
		}
		
		//Extract message parameters
		//Operation
		operation = message.substring(0, message.indexOf(" "));
		String rest = message.substring(message.indexOf(" ") + 1);
		//Version
		version = rest.substring(0, message.indexOf(" "));
		rest = rest.substring(message.indexOf(" ") + 1);
		//Sender id
		senderId = rest.substring(0, message.indexOf(" "));
		rest = rest.substring(message.indexOf(" ") + 1);
		//File id
		fileId = rest.substring(0, message.indexOf(" "));
		rest = rest.substring(message.indexOf(" ") + 1);
		//Chunck number
		chunckNo = rest.substring(0, message.indexOf(" "));
		rest = rest.substring(message.indexOf(" ") + 1);
		//Replication degree
		replicationDeg = rest.substring(0, message.indexOf(" "));
		rest = rest.substring(message.indexOf(" ") + 1);
		
		//Extract message body
		
	}
}
