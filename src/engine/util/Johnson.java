package engine.util;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import engine.core.Logger;

public class Johnson {
	
	static Map<String, String> classPaths = new HashMap<>();
	static Map<String, String> independantClasses = new HashMap<>();
	static Map<String, String> superclassNames = new HashMap<>();
	static {
		addDeserializablePath("GUI", "engine.ux.UIElements");
		setSuperclass("GUI", "UIElement");
		
		//addDeserializableClass("Menu", "engine.ux.UIElements.Menu");
		addDeserializableClass("ManagedGridLayout", "engine.ux.layouts.ManagedGridLayout");
		addDeserializableClass("GridLayout", "engine.ux.layouts.GridLayout");
	}
	
	public static void addDeserializablePath(String name, String path) {
		classPaths.put(name, path + ".");
	}
	public static void addDeserializableClass(String name, String path) {
		independantClasses.put(name, path);
	}
	public static void setSuperclass(String pathName, String className) {
		superclassNames.put(classPaths.get(pathName), className);
		
	}
	
	public static Object deserialize(String string) {
		//removing whitespace and comments
		string = string.replaceAll("(\n|\t|\\s)", "");
		
		StringReader reader = new StringReader(string);
		String classPath = classPaths.get(reader.advanceTo(';'));
		
		Stack<Object> currentObjects = new Stack<>();
		currentObjects.ensureCapacity(4);
		Stack<Object> values = new Stack<>();
		Stack<String> fields = new Stack<>();
		values.ensureCapacity(4);
		fields.ensureCapacity(4);
		
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
				currentObjects.add(createObject(classPath, reader));
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
						setField(currentObjects.peek(), fields.pop(), obj);
				}
				continue;
			
			case ']':
				Object list = currentObjects.pop();
				setField(currentObjects.peek(), fields.pop(), list);
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
			fields.add(reader.advanceTo(':'));
			
			//second switch block (this one tests for a array value
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
			setField(currentObjects.peek(), fields.pop(), values.pop());
		}
		
		return null;
	}
	
	private static void setField(Object obj, String field, Object value) {
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
		}catch (IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException e) {
			Logger.logException(e);
		}
	}
	
	private static Object createObject(String classPath, StringReader reader) {
		String typeName = reader.advanceTo('(');
		
		try {
			Class<?> clss;
			
			try {
				clss = Class.forName(classPath + typeName);
			} catch( ClassNotFoundException e ) {
				clss = Class.forName(independantClasses.get(typeName));
			}
			
			return clss
					.getConstructor(String[].class)
					.newInstance(new Object[] {reader.advanceTo(')').split(",")});
			
		}catch (InvocationTargetException e) {
			Logger.logException(e.getCause());
		}catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException e) {
			Logger.logException(e);
		}
		return null;
	}
}
