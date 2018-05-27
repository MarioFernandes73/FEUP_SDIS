package client_peer_file_transfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.util.ArrayList;

import rmi.RMIInterface;
import utils.Constants;

public class PartitionedFile {

	private String clientId;
	private String fileName;
	private File file;
	private Constants.FileType type;
	private ArrayList<Partition> partitions = new ArrayList<Partition>();
	
	/**
	 * 
	 * @param fileName for RESTORED file type, represents the file's name; for BACKEDUP file type, represents the file's path
	 * @param type
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public PartitionedFile(String clientId, String fileName, Constants.FileType type) throws FileNotFoundException, IOException {
		this.clientId = clientId;
		this.fileName = fileName;
		this.type = type;
		
		if (type == Constants.FileType.BACKEDUP) {
			this.file = new File(fileName);

			if (file.exists() && file.isFile()) {
				extractFilePartitions();
			} else {
				throw new FileNotFoundException();
			}

			this.fileName = file.getName();
		}
	}
	
	private void extractFilePartitions() throws IOException {
		int partitionSize = Constants.MAX_CHUNK_SIZE;
		int partitionsQuantity = (int) (file.length() / partitionSize) + 1;
		byte[] fileBytes = Files.readAllBytes(file.toPath());
		
		for (int partitionNo = 0; partitionNo < partitionsQuantity; partitionNo++) {

			if (partitionNo == partitionsQuantity - 1) {
				partitionSize = fileBytes.length % Constants.MAX_CHUNK_SIZE;
			}

			// copy file data to partition
			int partitionStart = partitionNo * Constants.MAX_CHUNK_SIZE;
			byte[] partitionData = new byte[partitionSize];
			int byteCounter = 0;
			
			System.arraycopy(fileBytes, partitionStart, partitionData, 0, partitionSize);
			
			partitions.add(new Partition(partitionData, partitionNo));
		}
	}
	
	private void createFileFromPartitions() {
		FileOutputStream out;
		try {
			out = new FileOutputStream(Constants.RESTORED_FILES_DIR + fileName, false);
			
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
	
	public boolean uploadToEndpoint(RMIInterface endpoint) {
		int partitionTransfTries = 0;
		
		for (int i = 0; ((i < partitions.size()) && (partitionTransfTries < Constants.MAX_CHUNK_TRANSFER_TRIES)); i++) {
			Partition partition = partitions.get(i);
			
			try {
				if (endpoint.transferFileChunk(this.clientId, this.fileName, partition.getPartitionNo(), partition.getData()) == Constants.FILE_CHUNK_TRANSFER_ERROR) {
					partitionTransfTries++;
					
					System.out.println("ERROR! File transfer to peer failed on Chunk no. " + partition.getPartitionNo());
					if (partitionTransfTries == Constants.MAX_CHUNK_TRANSFER_TRIES) {
						System.out.println("Max transfer tries have been reached! Could not transfer file to peer");
						return false;
					}
					
					System.out.println("Retrying...");
					i--;
					
				} else {
					partitionTransfTries = 0;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				System.out.println("ERROR! Couldn't invoke chunk transfer function");
				return false;
			}
		}
		
		return true;
	}
	
	public void downloadFromEndpoint(RMIInterface endpoint) {
		boolean reachedFinalPartition = false;
		
		for (int partitionNo = 0; !reachedFinalPartition; partitionNo++) {
            byte[] partitionData = new byte[0];
            try {
                partitionData = endpoint.getFileChunk(this.clientId, this.fileName, partitionNo);
            } catch (RemoteException e) {
                System.out.println("ERROR! Couldn't extract partition no. " + partitionNo + " from file " + this.fileName + " on client " + this.clientId);
            }

            if(partitionData == null)
                break;

            partitions.add(new Partition(partitionData, partitionNo));
		}

        createFileFromPartitions();
	}
}