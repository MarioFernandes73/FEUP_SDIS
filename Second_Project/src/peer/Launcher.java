package peer;


public class Launcher {

    public static void main(String[] args)  {

        try{
            Peer peer = new Peer(args);
        } catch ( Exception e){
            e.printStackTrace();
        }

    }

}
