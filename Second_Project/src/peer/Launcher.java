package peer;


import messages.MessageBuilder;

import java.util.ArrayList;

public class Launcher {

    public static void main(String[] args)  {
/*
        String[] temp = new String[]{"PUTCHUNK", "senderId" };
        new MessageBuilder().build(temp);
*/
        try{
            new Peer(args);
        } catch ( Exception e){
            e.printStackTrace();
        }
    }

}
