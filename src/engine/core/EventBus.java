package engine.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventBus {
	
	protected static Map<String, ArrayList<Method>> eventMethodMap = new HashMap<>();
	protected static ArrayList<Method> updateMethodList = new ArrayList<>();
	
	public static final String[] ARGS_NONE = {};
	
	private static final int QUEUE_SIZE = 64;
	private static BlockingQueue<Event> events = new ArrayBlockingQueue<Event>(QUEUE_SIZE);
	
	private static ScheduledExecutorService executor;
	
	private static int updateDelay = 20;
	private static long tick;
	
	protected static boolean running = true;
	public static boolean isRunning() {return running;}
	
	//broadcating
	public static void broadcast(String str) {
		if (str.startsWith("$lua")) {
			ScriptHandler.execute(str.substring(4));
	}else{
			broadcast(new Event(str));
		}
	}
	public static void broadcast(String[] strs) {
		String[] args = new String[strs.length-1];
		
		for (int i = 1; i < strs.length; i++) {
			args[i-1] = strs[i];
		}
		
		broadcast(new Event(strs[0], args));
	}
	public static void broadcast(String str, String... args) {
		broadcast(new Event(str, args));
	}
	public static void broadcast(String str, Object obj, String... args) {
		broadcast(new ContainerEvent(str, args, obj));
	}
	public static void broadcast(Event event) {
		try {
			events.put(event);
		} catch (InterruptedException e) {
			Logger.logException(e);
		}
	}
	
	public static void loop() {
		Event event;
		
		tick++;
		while (!events.isEmpty()) {
			event = events.poll();
			
			Logger.logEvent(event);
			
			try {
				broadcastNow(event);
			}catch(InvocationTargetException e) {
				Logger.logException(e.getCause());
			}catch(Exception e) {Logger.logException(e);}
		}
		
		try {
			for (Method updater : updateMethodList) {
				updater.invoke(null);
			}
		}catch(InvocationTargetException e) {
			Logger.logException(e.getCause());
		}catch(Exception e) {Logger.logException(e);}
	}
	
	public static void broadcastNow(Event event) throws Exception {
		if (!eventMethodMap.containsKey(event.getEvent()))
			return;
		
		for (Method handler : eventMethodMap.get(event.getEvent())) {
			handler.invoke(null, event);
		}
	}
	
	//init
	public static void start() {
		executor = Executors.newScheduledThreadPool(1);
		
		executor.scheduleAtFixedRate(new Runnable() {public void run() {loop();}}, 0, updateDelay, TimeUnit.MILLISECONDS);
	}
	public static void setUpdateDelay(int delay) {
		updateDelay = delay;
	}
	
	public static void initializeHandler(Class<?> handlerClass) {
		try {
			handlerClass.getDeclaredMethod("initialize").invoke(null);
		}catch(InvocationTargetException e) {
			Logger.logException(e.getCause());
			return;
		}catch (Exception e) {return;}
		
		String eventName;
		
		for (Method method : handlerClass.getDeclaredMethods()) {
			if (method.getName().startsWith("event")) {
				eventName = method.getName().substring(5).intern();
				
				if (!eventMethodMap.containsKey(eventName))
					eventMethodMap.put(eventName, new ArrayList<>());
				
				eventMethodMap.get(eventName).add(method);
				
				Logger.log(new Event("Event Handler Method Registered",  new String[] {handlerClass.getTypeName(), eventName}));
			}
		}
		
		try {
			updateMethodList.add(handlerClass.getDeclaredMethod("update"));
		} catch (NoSuchMethodException | SecurityException e) {}
		
	}
	
	public static void shutdown() {
		executor.shutdown();
		broadcast("Shutdown");
		
		while (!events.isEmpty()) {
			loop();
		}
		
		System.exit(0);
	}
	
	//getters
	public static long getTick() {
		return tick;
	}
	
	public static long secondsToTicks(float seconds) {
		return (long) (seconds* 20);
	}
}
