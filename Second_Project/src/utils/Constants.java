package utils;

public class Constants {

    public static final int MAX_PACKET_SIZE = 65000;
    
    public static final int MAX_CHUNK_SIZE = 64000;
    
    public static final int MAX_CHUNK_TRANSFER_TRIES = 3;

    public static final int RESPONSE_AWAITING_TIME = 300;
    
    //Peer RMI operation responses
    public static final int SUCCESS = 0;
    public static final int FILE_CHUNKS_NOT_RECEIVED = -1; //backup
    public static final int FILE_NOT_BACKEDUP = -2; //restore & delete
    public static final int FILE_CHUNK_TRANSFER_ERROR = -3; //backup
    
    public static final String RESTORED_FILES_DIR = "../../RestoredFiles/"; //located in the directory level as "src" folder

    public enum Operation {BACKUP, RESTORE, DELETE, STATE};

    public enum FileType {BACKEDUP, RESTORED};

    private static String getMainDir(String peerId){return "PeerFiles/"+peerId; }

    public static String getBackedUpChunksDir(String peerId){return getMainDir(peerId) + "/Chunks"; }

    public static String getInfoDir(String peerId){return getMainDir(peerId) + "/Info"; }

    public static String getLogsFile(String peerId){return getMainDir(peerId) + "/Logs.ser"; }

    public static String getChunksInfoFile(String peerId){return getMainDir(peerId) + "/ChunksInfo.ser"; }

    public static String getBackedUpFilesInfo(String peerId){return getMainDir(peerId) + "/BackedUpFilesInfo.ser"; }
}
