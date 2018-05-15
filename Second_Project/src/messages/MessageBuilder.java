package messages;

import messages.commands.MessagePutChunk;

import java.util.ArrayList;

public class MessageBuilder {

    public Message build(ArrayList<String> args){
        if(args.get(0).equals("PUTCHUNK")){
            return new MessagePutChunk();
        }
        return null;
    }

}
