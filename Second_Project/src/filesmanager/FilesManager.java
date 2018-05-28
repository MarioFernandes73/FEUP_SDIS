package filesmanager;

import peer.ChunkInfo;
import utils.Constants;
import utils.Utils;


import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class FilesManager {

    private String ownerId;
    private static CopyOnWriteArrayList<String> logs = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<ChunkInfo> chunksInfo = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<BackedUpFileInfo> backedUpFilesInfo = new CopyOnWriteArrayList<>();

    public FilesManager(String ownerId) {
        this.ownerId = ownerId;
        loadDirectories();
    }

    private void loadDirectories() {
        File[] sharedDirs = new File[]{new File(Constants.getBackedUpChunksDir(ownerId)), new File(Constants.getInfoDir(ownerId))};
        for (File dir : sharedDirs) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    public void saveFiles() {
        try {

            FileOutputStream fos = new FileOutputStream(Constants.getChunksInfoFile(ownerId), true);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.chunksInfo);
            this.chunksInfo.clear();
            oos.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.getLogsFile(ownerId), true));
            for(String log : logs){
                writer.write(log + "\n");
            }
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

        public void saveChunk(Chunk chunk) {
        byte data[] = chunk.getData();
        try {
            FileOutputStream out = new FileOutputStream(Constants.getBackedUpChunksDir(this.ownerId) + Utils.getDirSeparator() + chunk.getChunkId(),
                    false);
            out.write(data);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteChunk(String fileName) {
        for (File file : new File(Constants.getBackedUpChunksDir(this.ownerId)).listFiles()) {
            if (file.getName().equals(fileName)) {
                file.delete();
                return true;
            }
        }
        return false;
    }

    public static void addLog(String log){
        logs.add(log);
    }

    public void addChunkInfo(ChunkInfo chunkInfo){
        this.chunksInfo.add(chunkInfo);
    }

    public boolean addBackedUpFileInfo(BackedUpFileInfo fileInfo){
        for(BackedUpFileInfo file: this.backedUpFilesInfo){
            if(file.equals(fileInfo)){
                return false;
            }
        }
        this.backedUpFilesInfo.add(fileInfo);
        return true;
    }

    public BackedUpFileInfo getBackedUpFileInfo(String fileId) {
    	for(BackedUpFileInfo fileInfo : this.backedUpFilesInfo){
    		if(fileInfo.getFileId().equals(fileId))
    			return fileInfo;
    	}
    	return null;
    }

    public boolean hasChunk(String chunkId) {
        for (ChunkInfo chunkInfo : this.chunksInfo) {
            if (chunkInfo.getChunkId().equals(chunkId)) {
                return true;
            }
        }
        return false;
    }

    public Chunk getChunk(String chunkId) {
        try {
            for (File file : new File(Constants.getBackedUpChunksDir(this.ownerId)).listFiles()) {
                if (file.getName().equals(chunkId)) {
                    return new Chunk(chunkId, Files.readAllBytes(file.toPath()));
                }
            }

        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void addBackedUpFileInfo(ArrayList<BackedUpFileInfo> filesInfo) {
        this.backedUpFilesInfo.addAll(filesInfo);
    }

    public String getAllBackedUpFilesInfo() {
        StringBuilder filesInfo = new StringBuilder();
        for(int i = 0; i < this.backedUpFilesInfo.size(); i++){
            filesInfo.append(this.backedUpFilesInfo.get(i).toString());
            if(i != this.backedUpFilesInfo.size() - 1){
                filesInfo.append("~");
            }
        }
        return filesInfo.toString();
    }

    public void deleteChunkInfo(String chunkId) {
        int i = 0;
        for(ChunkInfo chunkInfo : this.chunksInfo){
            if(chunkInfo.getChunkId().equals(chunkId)){
                this.chunksInfo.remove(i);
            }
            i++;
        }
    }
}
