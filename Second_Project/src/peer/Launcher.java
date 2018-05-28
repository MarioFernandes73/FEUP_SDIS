package peer;


import client_peer_file_transfer.Partition;
import filesmanager.Chunk;
import rmi.RMIInterface;
import peer.Peer;
import utils.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Launcher {

    public static void main(String[] args)  {

        Peer peer = null;
        try{
            peer = new Peer(args);
            if(args[1].equals(Integer.toString(8001))){
                Thread.sleep(5000);
                File file = new File("C:\\Users\\Mario\\Desktop\\teste.jpg");
                String encryptedFileName = peer.encryptFileName(file.getName(), "Mario");
                ArrayList<Chunk> chunks = peer.splitToChunks(file, encryptedFileName);
                peer.addClientTransferChunks(encryptedFileName, chunks);
                peer.backup("Mario", file.getName(), 1);
                Thread.sleep(5000);
                peer.delete("Mario", file.getName());
                //peer.restore("Mario",file.getName());
                //temp(peer,"Mario","teste.jpg");
            }
        } catch ( Exception e){
            e.printStackTrace();
            return;
        }





       /* try {
            RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(peer, 0);
            System.out.println("Passou");
            // Bind remote object to rmi registry
            Naming.rebind(peer.getAccessPoint(), stub);
            System.out.println("Server ready for RMI communication " + "\n \tAccess point: " + peer.getAccessPoint());

        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("RMI stub creation error");
            return;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Invalid access point");
            return;
        }

        while(true) {

        }*/
    }

    private static void temp(Peer peer, String clientId, String fileName){
        boolean reachedFinalPartition = false;

        ArrayList<Partition> partitions = new ArrayList<>();

        for (int partitionNo = 0; !reachedFinalPartition; partitionNo++) {
            byte[] partitionData = new byte[0];
            try {
                partitionData = peer.getFileChunk(clientId, fileName, partitionNo);
                if(partitionData.length < Constants.MAX_CHUNK_SIZE){
                    reachedFinalPartition = true;
                }
            } catch (RemoteException e) {
                System.out.println("ERROR! Couldn't extract partition no. " + partitionNo + " from file " + fileName + " on client " + clientId);
            }

            if(partitionData == null)
                break;

            partitions.add(new Partition(partitionData, partitionNo));
        }

        createFileFromPartitions(partitions, fileName);
    }

    private static void createFileFromPartitions(ArrayList<Partition> partitions, String fileName) {
        File dir = new File(Constants.getRestoredFilesDir("TESTE"));
        dir.mkdirs();
        FileOutputStream out;
        try {
            out = new FileOutputStream(Constants.getRestoredFilesDir("TESTE") + "/" + fileName, false);

            for (Partition partition : partitions) {
                out.write(partition.getData());
            }
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("ERROR! Could not create file form restored partitions");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR! Could not create file form restored partitions");

        }
    }

}
