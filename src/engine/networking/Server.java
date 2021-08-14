package engine.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import engine.core.Logger;

public class Server implements Runnable {
	
	private int port;
	private boolean running;
	
	ServerSocket socket;
	
	private Set<Client> unRegisteredClients, registeredClients;
	
	private Map<String, Client> clients;
	
	
	public Server(int port) {
		this.port = port;
		
		unRegisteredClients = new HashSet<>();
		registeredClients = new HashSet<>();
		
		clients = new HashMap<>();
	}
	
	public void run() {
		try {
			socket = new ServerSocket(port);
			
			
			while (running) {
				unRegisteredClients.add(new Client(socket.accept(), false));
			}
		} catch (IOException e) {
			Logger.logException(e);
			
			shutdown();
		}
	}
	
	public void shutdown() {
		running = false;
		
		socket = null;
	}
	
	//functions
	public void clientConnected(Client client, String clientName) {
		registeredClients.add(client);
		clients.put(clientName, client);
	}
	
	//getters
	public int getPort() {
		return port;
	}
	
	public Client getClient(String clientName) {
		return clients.get(clientName);
	}
}