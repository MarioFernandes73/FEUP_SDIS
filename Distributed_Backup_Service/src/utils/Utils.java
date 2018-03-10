package utils;

import java.io.File;
import java.util.ArrayList;

public class Utils {
	public static String defaultIP = "localhost";
	public static enum operations {BACKUP, RESTORE, DELETE, RECLAIM, BACKUPENH, RESTOREENH, DELETEENH, RECLAIMENH, STATE};
	public static int MAX_PACKET_SIZE = 65000;
	public static int MAX_CHUNK_SIZE = 64000;
	public static int MAX_DISK_SPACE = 100000000;
}
