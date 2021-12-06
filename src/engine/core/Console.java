package engine.core;

import java.util.Scanner;

public class Console {
	
	private static boolean running = true;
	
	public static void initialize() {
		
		Scanner in = new Scanner(System.in);
		
		
		Runnable consoleThread = new Runnable() {
			@Override
			public void run() {
				while (running) {
					input(in.nextLine());
				}
			}
		};
		
		new Thread(consoleThread).start();
	}
	
	private static void input(String str) {
		EventBus.broadcast(str);
	}
	
	public static void eventShutdown(Event event) {
		running = false;
	}
}
