package messages;

import peer.Peer;

import java.nio.charset.Charset;

public class PacketHandler implements Runnable {

    private Peer owner;
    private byte[] data;

    public PacketHandler(Peer owner, byte[] data){
        this.owner = owner;
        this.data = data;
    }

    @Override
    public void run() {

        String text = new String(data, Charset.forName("ISO_8859_1"));
        String crlf = " \r\n\r\n";
        int crlfIndex = text.indexOf(crlf);
        if(!text.contains(" ") || crlfIndex < 1) {
            return;
        }
        String header = text.substring(0,crlfIndex);


        int bodyStartPos = (header + crlf).getBytes().length;
        int bodyLength = this.data.length - bodyStartPos;
        byte[] body = new byte[bodyLength];
        System.arraycopy(this.data, bodyStartPos, body, 0, bodyLength);

        Message msg = MessageBuilder.build(header.split(" "));
        msg.setData(body);
        msg.handleMessage(this.owner);
    }
}
