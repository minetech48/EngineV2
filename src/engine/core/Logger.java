package engine.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import engine.util.FileIO;

public class Logger {
	
	public static boolean eventLogging = true;
	
	static File logFile;
	static FileWriter writer;
	static {
		try {
			logFile = FileIO.getFile("logs/" + Long.toString(System.currentTimeMillis()) + ".txt", true);
			writer = new FileWriter(logFile);
		} catch (IOException e) {
			log(e);
		}
	}
	
	public static void log(Throwable t) {
		logException(t);
	}
	public static void logException(Throwable t) {
		System.err.println(t);
		logToFile(t.toString());
		
		for (StackTraceElement el : t.getStackTrace()) {
			System.err.println(el);
			logToFile(el.toString());
		}
	}
//	public static void logException(LuaError error) {
//	}
	
	public static void log(Object obj) {
		log(obj.toString());
	}
	public static void log(String str) {
		logToFile(str);
		
		System.out.println(str);
	}
	
	public static void logToFile(String str) {
		if (writer != null) {
			try {
				writer.append(str);
				writer.append('\n');
				
				writer.flush();
			}catch(Exception e) {e.printStackTrace();}
		}
	}
	
	public static void log(String logChannel, Throwable t) {
		System.err.println("Error: " + logChannel + ":");
		log(t);
	}
	public static void log(String logChannel, String str) {
		System.out.println(logChannel + ": " + str);
	}
	
	public static void log(Event event) {
		logEvent(event);
	}
	public static void logEvent(Event event) {
		if (!eventLogging) return;
		
		log("Event: " + event.toString());
	}
}
