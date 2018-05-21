package client;

import client.Client;

public class Main {
	
	public static void main(String[] args) {
		try {
			Client client = new Client(args);
		} catch (Exception e) {}
	}
}
