package messages;

import java.util.ArrayList;

public class MessageBuilder {

    public Message build(ArrayList<String> args){
        if(args.get(0).equals("PUTCHUNK")){
            return new MessagePutchunk();
        }
        return null;
    }

}
