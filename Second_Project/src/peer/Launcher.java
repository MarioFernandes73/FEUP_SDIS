package peer;


import messages.MessageBuilder;

import java.util.ArrayList;

public class Launcher {

    public static void main(String[] args)  {

        ArrayList<String> temp = new ArrayList<>();
        temp.add("PUTCHUNK");
        temp.add("senderId");
        new MessageBuilder().build(temp);
/*
        try{
            Peer peer = new Peer(args);
        } catch ( Exception e){
            e.printStackTrace();
        }
*/
    }

}
