1. cd into the src folder: project_folder_directory/src   ("project_folder_directory" depends on the machine)

2. Compile Main.java file used as server:
	- javac server/Main.java  (all imports used by this file will consequently be compiled too)

3. Compile TestApp.java file used as client:
	- javac cli/TestApp.java  (all imports used by this file will consequently be compiled too)
	
4. Start RMI registry on the server machine
	- rmiregistry &
	
4. Run the server
	- java server/Main <protocol_version> <server_id> <RMI_access_point> <MC_ip> <MC_port> <MDB_ip> <MDB_port> <MDR_ip> <MDR_port>
	the only supported protocol version is 1.0, and the server id and rmi access point vary according to each peer.
	<MC_ip> <MC_port> <MDB_ip> <MDB_port> <MDR_ip> <MDR_port> should be the same for all peers.
	example peer 1: java server/Main 1.0 1 //localhost/peer1 224.0.0.0 8000 224.0.0.0 8001 224.0.0.0 8002
	example peer 2: java server/Main 1.0 2 //localhost/peer2 224.0.0.0 8000 224.0.0.0 8001 224.0.0.0 8002
	
5. Run the client
	- java cli/TestApp <peer_RMI_access_point> <sub_protocol> <opnd_1> <opnd_2>
	the rmi access point requires the previous knowledge of the peer ip address if they are running in seperates machines, in other case, we can use localhost.
	example connecting to peer 1 default port running on the same machine: java cli/TestApp //localhost/peer1 BACKUP example.pdf 3
	example connecting to peer 2 default port running on different machine with ip 192.32.16.47: java cli/TestApp //192.32.16.47/peer2 BACKUP example.pdf 3