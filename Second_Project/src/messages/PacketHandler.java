package messages;

import peer.Peer;

import java.net.DatagramPacket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

public class PacketHandler implements Runnable {

    private Peer peer;
    private byte[] data;

    public PacketHandler(DatagramPacket packet){
        this.data = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
    }

    @Override
    public void run() {
        parseText().receivedOperation(peer);
    }


    private String getHeaderFormat(String operation) {
        String version = " ((1\\.0)|(2\\.0))";
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
            case "CHUNKDELETED":
                //operation <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
                format = common + chunckNo + "$";
                break;
            case "CHUNK":
                //operation <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>
                format = common + chunckNo + " " + crlf + crlf + body + "$";
                break;
            case "DELETE":
            case "INITDELETE":
                //DELETE <Version> <SenderId> <FileId> <CRLF><CRLF>
                format = common + "$";
                break;
        }
        return format;
    }

    private String getOperationFormat(){
        return "^(PUTCHUNK|STORED|GETCHUNK|CHUNK|DELETE|REMOVED|CHUNKDELETED|INITDELETE)$";
    }

    private Message parseText() {
        String text = new String(data, Charset.forName("ISO_8859_1"));
        if(!text.contains(" ") || text.indexOf(" \r\n\r\n") < 1) {
            return null;
        }
        //Extract operation
        String operation = text.substring(0, text.indexOf(" "));
        if(!operation.matches(getOperationFormat())) {
            return null;
        }
        System.out.println("OPERATION   " + operation);

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

        //Extract message parameters
        ArrayList<String> params = new ArrayList<>();
        //Operation
        params.add(operation);
        //Version
        params.add(rest.substring(0, rest.indexOf(" ")));
        rest = rest.substring(rest.indexOf(" ") + 1);
        //Sender id
        params.add(rest.substring(0, rest.indexOf(" ")));
        rest = rest.substring(rest.indexOf(" ") + 1);
        //File id
        params.add(rest.substring(0, rest.indexOf(" ")));
        rest = rest.substring(rest.indexOf(" ") + 1);
        //ChunkNo
        if(!operation.equals("DELETE") && !operation.equals("INITDELETE")) {
            params.add(rest.substring(0, rest.indexOf(" ")));
            rest = rest.substring(rest.indexOf(" ") + 1);
        }
        //Replication Degree and body
        if(operation.equals("PUTCHUNK")) {
            params.add(rest.substring(0, rest.indexOf(" ")));
        }

        Message message = new MessageBuilder().build(params);

        if(operation.equals("PUTCHUNK") || operation.equals("CHUNK")) {
            byte[] body = new byte[this.data.length - message.getHeader().length()];
            System.arraycopy(this.data, message.getHeader().length(), body, 0, this.data.length - message.getHeader().length());
            message.setBody(body);
        }
        return message;
    }

}
