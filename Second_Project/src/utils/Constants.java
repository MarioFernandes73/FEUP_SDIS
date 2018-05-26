package utils;

import messages.Message;
import messages.commands.MessagePutChunk;
import messages.peerscomunications.*;
import messages.responses.*;

import java.util.HashMap;

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
    public static final int DEFAULT_CONNECTION_LIMIT = 2;

    public enum Operation {BACKUP, RESTORE, DELETE, STATE}

    public enum MessageType {ACCEPT_CONNECTION,
        REJECT_PEER,
        ACCEPT_PEER,
        ADD_PEER,
        CONNECT,
        PUT_CHUNK,
        ALIVE,
        STORED,
        REQUEST_CONNECTION,
        CHANGE_CONNECTION_LIMIT,
        SEND_FILE_INFO,
        RECEIVE_FILE_INFO,
        SEND_DELETE_CHUNK,
        RECEIVED_DELETE_CHUNK
    }

    public static final HashMap<String, Class<?>> messageHashMap;

    static {
        messageHashMap = new HashMap<>();
        messageHashMap.put("ACCEPT_CONNECTION", MessageAcceptConnection.class);
        messageHashMap.put("MESSAGE_REJECT_PEER", MessageRejectPeer.class);
        messageHashMap.put("MESSAGE_ACCEPT_PEER", MessageAcceptPeer.class);
        messageHashMap.put("MESSAGE_ADD_PEER", MessageAddPeer.class);
        messageHashMap.put("CONNECT", MessageConnect.class);
        messageHashMap.put("PUT_CHUNK", MessagePutChunk.class);
        messageHashMap.put("ALIVE", MessageAlive.class);
        messageHashMap.put("STORED", MessageStored.class);
        messageHashMap.put("REQUEST_CONNECTION", MessageStored.class);
        messageHashMap.put("CHANGE_CONNECTION_LIMIT", MessageStored.class);
    }

    public enum FileType {BACKEDUP, RESTORED}

    ;

    private static String getMainDir(String peerId) {
        return "PeerFiles/" + peerId;
    }

    public static String getBackedUpChunksDir(String peerId) {
        return getMainDir(peerId) + "/Chunks";
    }

    public static String getInfoDir(String peerId) {
        return getMainDir(peerId) + "/Info";
    }

    public static String getLogsFile(String peerId) {
        return getMainDir(peerId) + "/Logs.ser";
    }

    public static String getChunksInfoFile(String peerId) {
        return getMainDir(peerId) + "/ChunksInfo.ser";
    }

    public static String getBackedUpFilesInfo(String peerId) {
        return getMainDir(peerId) + "/BackedUpFilesInfo.ser";
    }
}
