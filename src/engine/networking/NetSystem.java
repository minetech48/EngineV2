package engine.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import engine.core.Logger;

public class NetSystem {
	
	private static ServerSocket serverSocket;
	private static Client clientSide;
	
	private static int currentClientID;
	private static Map<Integer, Client> clients;
	
	public static void initialize() {}
	
	public static void update() {
		if (clientSide != null) {
			clientSide.update();
		}
		
		if (clients != null) {
			for (Client client : clients.values()) {
				client.update();
			}
		}
	}
	
	public static ServerSocket getServerSocket() {
		return serverSocket;
	}
	public static ServerSocket getServerSocket(int port) {
		try {
			if (serverSocket == null) {
				serverSocket = new ServerSocket(port);
				
				clients = new HashMap<>();
				return serverSocket;
			}else{
				return new ServerSocket(port);
			}
		}catch (IOException e) {
			Logger.log(e);
		}
		return null;
	}
	
	//clientside
	public static Client getClientConnection(String ip, int port) {
		try {
			clientSide = new Client(new Socket(ip, port), true);
			
			return clientSide;
		} catch (IOException e) {
			Logger.log(e);
			
			return null;
		}
	}
	
	//serverside
	public static Client getClientConnection(Socket socket) {
		return new Client(socket, false);
	}
	public static Client getClientConnection(Integer ID) {
		if (clients == null) return null;
		
		return clients.get(ID);
	}
	
	public static int registerClient(Client client) {
		client.clientID = currentClientID++;
		
		clients.put(client.clientID++, client);
		
		return client.clientID;
	}
	
	public static Map<Integer, Client> getClients() {
		return clients;
	}
	
}
