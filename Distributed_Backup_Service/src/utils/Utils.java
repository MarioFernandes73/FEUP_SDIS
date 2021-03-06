package utils;

import java.io.File;
import java.util.Arrays;

public class Utils {
	public static String defaultIP = "localhost";
	public static enum operations {BACKUP, RESTORE, DELETE, RECLAIM, BACKUPENH, RESTOREENH, DELETEENH, RECLAIMENH, STATE};
	public static int MAX_PACKET_SIZE = 65000;
	public static int MAX_CHUNK_SIZE = 64000;
	public static long MAX_DISK_SPACE = 100000000;
	public static int MAX_TRIES = 5;
	public static int MAX_RANDOM_DELAY = 400;
	public static int FIXED_WAITING_TIME = 700;
	
	public static String DEFAULT_VERSION = "1.0";
	
	public static String getSeparator() {
		if(System.getProperty("os.name").split(" ")[0].equals("Windows")) {
			return "\\";
		}
		
		return "/";
	}
	
	public static int calcChunksQuantity(File file) {
		return (int) (file.length() / Utils.MAX_CHUNK_SIZE) + 1;
	}
}
