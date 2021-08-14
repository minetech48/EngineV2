package engine.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import engine.core.Logger;

public class Client {
	
	private Socket socket;
	
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	private Queue<Object> outputQueue;
	
	private ClientInputListener listener;
	
	private boolean running = true;
	public int clientID;
	
	public Client() {}
	protected Client(Socket socket, boolean clientSide) {
		this.socket = socket;
		
		try {
			if (clientSide) {
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
			}else{
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
			}
		} catch (IOException e) {
			Logger.logException(e);
			return;
		}
		
		
		//input listening
		new Thread() {
			public void run() {
				try {
					while (running) {
						input(in.readObject());
					}
				}catch(Exception e) {
					disconnect(e);
				}
			}
		}.start();
		
		//output thread
		new Thread() {
			public void run() {
				try {
					outputQueue = new ConcurrentLinkedQueue<>();
					
					while (running) {
						synchronized (out) {
							out.wait();
						}
						
						while(!outputQueue.isEmpty()) {
							out.writeObject(outputQueue.poll());
						}
					}
				}catch(Exception e) {
					disconnect(e);
				}
			}
		}.start();
	}
	public void update() {
		synchronized (out) {
			out.notify();
		}
	}
	
	
	public void input(Object recieved) {
		listener.input(recieved);
	}
	
	public void out(Object toSend) {
		outputQueue.add(toSend);
	}
	
	
	public void disconnect(Exception e) {
		if (running) {
			Logger.log(e);
			disconnect();
		}
	}
	public void disconnect() {
		running = false;
	}
	
	
	public Client setInputListener(ClientInputListener listener) {
		this.listener = listener;
		
		return this;
	}
	
	public Socket getSocket() {
		return socket;
	}
}
