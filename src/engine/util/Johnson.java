package engine.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;

import engine.core.Logger;
import engine.util.vector.Vec2i;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.luaj.vm2.ast.Str;

public class Johnson {
	
	static Set<String> classPaths = new HashSet<>();
	static Map<String, String> independentClasses = new HashMap<>();
	static {
		addDeserializablePath("GUI", "engine.ux.UIElements");
		
		//addDeserializableClass("Menu", "engine.ux.UIElements.Menu");
		addDeserializableClass("ManagedGridLayout", "engine.ux.layouts.ManagedGridLayout");
		addDeserializableClass("GridLayout", "engine.ux.layouts.GridLayout");
	}
	
	public static void addDeserializablePath(String name, String path) {
		classPaths.add(path + ".");
	}
	public static void addDeserializableClass(String name, String path) {
		independentClasses.put(name, path);
	}
	
	public static Object deserialize(File file) throws FileNotFoundException {
		DeserializationEvent dEvent = new DeserializationEvent(file);
		
		return dEvent.next();
	}
	
	public static Object deserialize(String string) {
		DeserializationEvent dEvent = new DeserializationEvent(string);
		
		return dEvent.next();
	}
	
	public static ArrayList<Object> deserializeList(File file) throws FileNotFoundException {
		return deserializeList(new DeserializationEvent(file));
	}
	public static ArrayList<Object> deserializeList(String string) {
		return deserializeList(new DeserializationEvent(string));
	}
	
	public static ArrayList<Object> deserializeList(DeserializationEvent dEvent) {
		ArrayList<Object> objectList = new ArrayList<>();
		
		while (dEvent.reader.hasNext()) {
			objectList.add(dEvent.next());
			
			if (objectList.get(objectList.size()-1) == null) {
				return objectList;
			}
		}
		
		return objectList;
	}
	
	static void setField(Object obj, String field, Object value) throws Exception {
		try {
			//System.out.println(currentObjects.peek() + ": <" + fields.peek() + ", " + values.peek()+ ">:");
			Class<?> clss = obj.getClass();
			Class<?> valueClass;
			
//			if (value.getClass().equals(String.class)) {
//				valueClass = String.class;
//				((String) value).intern();
//			}else
//				valueClass = AbstractList.class;
			
			if (value.getClass().equals(LinkedList.class))
				valueClass = AbstractList.class;
			else
				valueClass = value.getClass();
			
			try {
				if (value.getClass().equals(String.class) && ((String) value).equalsIgnoreCase("true"))
					clss
							.getField(field)
							.set(obj, true);
				else if (value.getClass().equals(String.class) && ((String) value).equalsIgnoreCase("false"))
					clss
							.getField(field)
							.set(obj, false);
				else
					clss
					.getField(field)
					.set(obj, value);
			}catch (IllegalAccessException | NoSuchFieldException e) {
				try {
					clss
					.getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1), valueClass)
					.invoke(obj, value);
				}catch(NoSuchMethodException ex) {
					clss
					.getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1), valueClass.getSuperclass())
					.invoke(obj, value);
				}
			}
		}catch (InvocationTargetException e) {
			Logger.logException(e.getCause());
			
		}catch(NoSuchMethodException e) {
			Logger.err("Johnson Error: " + e.getClass().getName());
			Logger.err("\tMethod not found in class:");
			Logger.err("\t\tMethod:\tset" + field.substring(0, 1).toUpperCase() + field.substring(1) + "(" + value.getClass().getName() + ")");
			Logger.err("\t\tClass:\t" + obj.getClass().getSimpleName());
			
			throw new JohnsonException("Method not found");
		}
	}
	
	static Object createObject(StringReader reader) throws Exception{
		String typeName = reader.advanceTo('(');
		
		try {
			Class<?> clss = null;
			
			for (String classPath : classPaths) {
				try {
					clss = Class.forName(classPath + typeName);
					break;
				} catch(ClassNotFoundException ignored) {}
			}
			
			if (clss == null) {
				clss = Class.forName(independentClasses.get(typeName));
			}
			
			return clss
					.getConstructor(String[].class)
					.newInstance(new Object[] {reader.advanceTo(')').split(",")});
			
		}catch (InvocationTargetException e) {
			Logger.logException(e.getCause());
		}catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException e) {
			throw e;
		}
		return null;
	}
}

class DeserializationEvent {
	
	File fileSource;
	String source;
	
	StringReader reader;
	
	Stack<Object> currentObjects = new Stack<>();
	Stack<Object> values = new Stack<>();
	Stack<String> fields = new Stack<>();
	
	public DeserializationEvent(File fileSource) throws FileNotFoundException {
		this(FileIO.toString(fileSource));
		
		this.fileSource = fileSource;
	}
	
	public DeserializationEvent(String source) {
		this.source = source;
		//removing whitespace and comments
		source = source.replaceAll("(\\s)", "");
		
		reader = new StringReader(source);
		//String classPath = classPaths.get(reader.advanceTo(';'));
	}
	
	public Object next() {
		try {
			return start();
		}catch(Exception e) {
			if (e.getClass().equals(JohnsonException.class)) {
				logLocation();
				
				return null;
			}
			
			Logger.err("Johnson Error: " + e.getClass().getName());
			
			
			switch (e.getClass().getSimpleName()) {
				case "StringIndexOutOfBoundsException":
					Logger.err("\tEOF Exception: " + e.getMessage());
					break;
				
				default:
					Logger.log(e);
			}
			
			logLocation();
		}
		
		return null;
	}
	
	private void logLocation() {
		if (fileSource != null)
			Logger.err("\tFrom: " + fileSource.getPath() + ":" +  + getReaderLocation().y);
		else
			Logger.err("\tFrom <File Null>:" + getReaderLocation().y);
	}
	
	private Object start() throws Exception {
		
		StringBuilder builder = null;
		
		int scope = 0;
		
		while (reader.hasNext()) {
			//First switch block
			switch (reader.next()) {
				case '#'://Comments
					reader.advanceTo('#');
					continue;
				case '{':
					scope++;
					currentObjects.add(Johnson.createObject(reader));
					reader.next();
					continue;
				case '\"':
					currentObjects.push(reader.advanceTo('\"'));
					scope++;
				case '}':
					scope--;
					Object obj = currentObjects.pop();
					
					if (currentObjects.size() == 0) {
						return obj;
					}else{
						if (currentObjects.peek().getClass().equals(LinkedList.class)) {
							((LinkedList<Object>) currentObjects.peek()).add(obj);
							continue;
						}else
							Johnson.setField(currentObjects.peek(), fields.pop(), obj);
					}
					continue;
				
				case ']':
					Object list = currentObjects.pop();
					Johnson.setField(currentObjects.peek(), fields.pop(), list);
					
					if (!reader.hasNext())
						return currentObjects.pop();
					continue;
				
				case ',':
					continue;
				
				default:
					reader.previous();
					break;
			}
			
			if (currentObjects.peek().getClass().equals(LinkedList.class)) {
				String[] split = reader.advanceTo(']').split(",");
				LinkedList<Object> list = ((LinkedList<Object>) currentObjects.peek());
				
				for (String s : split)
					list.add(s);
				
				reader.previous();
				continue;
			}
			
			//getting field name
			fields.add(reader.advanceTo(':'));//TODO: "booleanField;" = "booleanField:true"
			
			//second switch block (this one tests for an array value)
			switch (reader.next()) {
				case '[':
					currentObjects.add(new LinkedList<Object>());
					continue;
				case '{':
					reader.previous();
					continue;
				default:
					reader.previous();
					break;
			}
			
			//setting the variable(field)
			if (values.size() < scope)
				values.add(reader.advanceTo(';'));
			Johnson.setField(currentObjects.peek(), fields.pop(), values.pop());
		}
		
		return null;
	}
	
	public Vec2i getReaderLocation() {
		StringReader sweeper = new StringReader(source);
		
		Vec2i charPos = new Vec2i();
		
		for (int i = 0; i < reader.getIndex(); i++) {
			if (Character.isWhitespace(sweeper.next()))
				i--;
			
			charPos.x++;
			if (sweeper.current() == '\n') {
				charPos.x = 0;
				charPos.y++;
			}
		}
		
		return charPos;
	}
}

class JohnsonException extends Exception {
	
	public JohnsonException(String message) {
		super(message);
	}
}