package messages;

public class MessageBuilder {

    public Message build(String[] args){
        if(args[0] == "PUTCHUNK"){
            return new MessagePutchunk();
        }
        return null;
    }

}
