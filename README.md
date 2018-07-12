#FEUP_SDIS
Repository for the course "Sistemas distribuídos" (EIC0036) of the second semester of the third year of MIEIC in FEUP.

In this repository there are the two practical assignments of the course in the lective year of 2018/2019.

This course provides students a more in depth knowledge on web applications especially on how they can scale and how backend systems can deal with a big number of clients using the application. To do this, stuents were challenged to create Java peer-to-peer applications on the first project and to further develop that system on the second project.

All of the following topics were expected to be dealt with and each were handled up to a certain extend:
* Communication models: messages, remote functions invocation
* Identification and localization
* Security
* Synchronization
* Replication and consistency
* Fault tolerance
* Distributed objects
* Distributed file systems

Instructions to run the first project:

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
