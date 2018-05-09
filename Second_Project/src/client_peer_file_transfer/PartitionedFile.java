package client_peer_file_transfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import rmi.RMIInterface;
import utils.Constants;

public class PartitionedFile {
	
	private String fileName; //represents the file path when file type is BACKEDUP
	private File file;
	private Constants.FileType type;
	private RMIInterface endpoint;
	private ArrayList<Partition> partitions = new ArrayList<Partition>();
	
	public PartitionedFile(String fileName, Constants.FileType type, RMIInterface endpoint) throws FileNotFoundException {
		this.fileName = fileName;
		this.type = type;
		this.endpoint = endpoint;
		
		if(type == Constants.FileType.BACKEDUP) {		
			this.file = new File(fileName);
			
			if(file.exists() && file.isFile()) {
				extractFilePartitions();			
			} else {
				throw new FileNotFoundException();
			}
		}
	}
	
	private void extractFilePartitions() {
		
	}
}
