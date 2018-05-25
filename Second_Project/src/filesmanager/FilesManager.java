package filesmanager;

import peer.ChunkInfo;
import utils.Constants;


import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class FilesManager {

    private String ownerId;
    private CopyOnWriteArrayList<String> logs = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<ChunkInfo> chunksInfo = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<BackedUpFileInfo> backedUpFilesInfo = new CopyOnWriteArrayList<>();

    public FilesManager(String ownerId) {
        this.ownerId = ownerId;
        loadDirectories();
        loadFiles();
    }

    private void loadDirectories() {
        File[] sharedDirs = new File[]{new File(Constants.getBackedUpChunksDir(ownerId)), new File(Constants.getChunksInfoFile(ownerId))};
        for (File dir : sharedDirs) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    private void loadFiles() {
        String[] filesInfoPath = new String[]{Constants.getLogsFile(ownerId), Constants.getChunksInfoFile(ownerId), Constants.getBackedUpFilesInfo(ownerId)};
        for (int i = 0; i < filesInfoPath.length; i++) {
            File file = new File(filesInfoPath[i]);
            if (file.exists()) {
                ObjectInputStream ois = null;
                try {
                    FileInputStream fis = new FileInputStream(filesInfoPath[i]);
                    ois = new ObjectInputStream(fis);
                    switch (i) {
                        case 0:
                            @SuppressWarnings("unchecked")
                            CopyOnWriteArrayList<String> readCase = (CopyOnWriteArrayList<String>) ois.readObject();
                            logs.addAll(readCase);
                            break;
                        case 1:
                            @SuppressWarnings("unchecked")
                            CopyOnWriteArrayList<ChunkInfo> readCase1 = (CopyOnWriteArrayList<ChunkInfo>) ois.readObject();
                            chunksInfo.addAll(readCase1);
                            break;
                        case 2:
                            @SuppressWarnings("unchecked")
                            CopyOnWriteArrayList<BackedUpFileInfo> readCase2 = (CopyOnWriteArrayList<BackedUpFileInfo>) ois.readObject();
                            backedUpFilesInfo.addAll(readCase2);
                            break;
                    }
                    ois.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void saveFiles() {
        ObjectOutputStream oos = null;
        String[] filesInfoPath = new String[]{Constants.getLogsFile(ownerId), Constants.getChunksInfoFile(ownerId), Constants.getBackedUpFilesInfo(ownerId)};
        try {
            for (int i = 0; i < filesInfoPath.length; i++) {
                FileOutputStream fos = new FileOutputStream(filesInfoPath[i], true);
                oos = new ObjectOutputStream(fos);
                switch (i) {
                    case 0:
                        oos.writeObject(this.logs);
                        this.logs.clear();
                        break;
                    case 1:
                        oos.writeObject(this.chunksInfo);
                        this.chunksInfo.clear();
                        break;
                    case 2:
                        oos.writeObject(this.backedUpFilesInfo);
                        this.backedUpFilesInfo.clear();
                        break;
                }
            }
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addLog(String log){
        this.logs.add(log);
    }

    public void addChunkInfo(ChunkInfo chunkInfo){
        this.chunksInfo.add(chunkInfo);
    }

    public void addBackedUpFileInfo(BackedUpFileInfo fileInfo){
        this.backedUpFilesInfo.add(fileInfo);
    }

}
