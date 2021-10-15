package engine.ux;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.luaj.vm2.LuaValue;

import engine.core.Event;
import engine.core.EventBus;
import engine.core.Logger;
import engine.core.ScriptHandler;
import engine.util.FileIO;
import engine.util.Johnson;
import engine.util.StringReader;
import engine.ux.UIElements.Menu;
import engine.ux.UIElements.UIElement;
import engine.ux.primitives.Font;

public class GUI {
	
	public static WindowWrapper window;
	public static GraphicsWrapper graphics;
	
	private static BufferedImage currentFrame;
	
	private static Map<String, Menu> menus;
	private static ArrayList<String> activeMenus;
	
	public static UIElement hoveredElement;
	public static UIElement focusedElement;
	
	private static ArrayList<LuaValue> updateScripts;
	private static Map<String, LuaValue> eventScripts;
	
	protected static float scale = 2, fontScale = 1;
	
	
	//System Initialization
	public static void initialize() {
		menus = new HashMap<>();
		activeMenus = new ArrayList<>();
		
		updateScripts = new ArrayList<>();
		eventScripts = new HashMap<>();
		
		ScriptHandler.execute(FileIO.getFile("gui/scripts/GUIMain.lua", true));
		
		window = new WindowWrapper();
		window.setVisible(true);
		graphics = new GraphicsWrapper();
	}
	
	//events
	public static void eventGUIShow(Event event) {
		activeMenus.add(event.getArgument(0));
		
		if (!menus.containsKey(event.getArgument(0))) {
			loadMenu(event.getArgument(0));
		}else{
			menus.get(event.getArgument(0)).onShow();
		}
	}
	public static void eventGUIHide(Event event) {
		activeMenus.remove(event.getArgument(0));
	}
	
	public static void eventGUIReload(Event event) {
		menus.clear();
		
		for (String str : activeMenus) {
			loadMenu(str);
		}
		
		EventBus.broadcast("GUIReloaded");
	}
	public static void eventGUIReset(Event event) {
		menus.clear();
		activeMenus.clear();
		
		stringLists.clear();
		colorMap.clear();
		requested.clear();
		
		EventBus.broadcast("GUICleared");
		
		ScriptHandler.execute(FileIO.getFile("gui/scripts/GUIMain.lua", true));
	}
	
	public static void eventGUIClear(Event event) {
		menus = new HashMap<>();
		activeMenus = new ArrayList<>();
	}
	
	public static void eventGUILoadData(Event event) {
		if (event.getArguments().length == 0) {
			EventBus.broadcast("GUILoadData", "gui/themes/ColorsDefault.txt");
			EventBus.broadcast("GUILoadData", "gui/themes/ThemeDefault.txt");
			EventBus.broadcast("GUILoadData", "gui/lang/en.txt");
				//EventBus.broadcast("GUILoadData", "gui/lang/"+ getString("language") +".txt");
			
			return;
		}
		
		File file = FileIO.getFile(event.getArgument(0));
		
		if (file != null && file.exists())
			loadDataFile(file);
	}
	
	public static void eventShutdown(Event event) {
		activeMenus.clear();
		menus.clear();
	}
	
	public static void update() {
		for (LuaValue function : updateScripts) {
			function.call(LuaValue.valueOf(EventBus.getTick()));
		}
		
		updateFrame();
		
		if (window.mouseMoved) {
			mouseMoved(window.mousePosition);
			
			window.mouseMoved = false;
		}
	}
	
	public static void panelResized(int width, int height) {
		for (Menu menu : menus.values()) {
			if (menu == null) continue;
			
			menu.allignElements();
		}
	}
	
	public static void broadcastEvent(String str) {
		str = reconstructString(str);
		
		if (eventScripts.containsKey(str))
			eventScripts.get(str).call();
		else
			EventBus.broadcast(str);
	}
	
	public static void click() {
		if (GUI.hoveredElement != null) {
			if (GUI.focusedElement != null)
				GUI.focusedElement.focused = false;
			
			GUI.hoveredElement.click();
			GUI.focusedElement = GUI.hoveredElement;
			
			GUI.focusedElement.focused = true;
		}else{
			if (GUI.focusedElement != null)
				GUI.focusedElement.focused = false;
			GUI.focusedElement = null;
		}
	}
	
	//drawing
	public static void draw(Graphics g) {
		g.drawImage(currentFrame, 0, 0, null);
	}
	
	public static void updateFrame() {
		if (EventBus.getTick() % 2 == 1) return;
		currentFrame = new BufferedImage(window.frame.getWidth(), window.frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
		graphics.graphics = currentFrame.createGraphics();
		
		for (String menuName : (ArrayList<String>) activeMenus.clone()) {
			if (!menus.containsKey(menuName)) continue;
			
			menus.get(menuName).draw();
		}
		
		window.frame.repaint();
	}
	
	//events
	public static void loadMenu(String menuName) {
		try {
			menus.put(menuName, (Menu)
					Johnson.deserialize(FileIO.toString(FileIO.getFile("gui/menus/" + menuName + ".john"))));
		} catch (FileNotFoundException e) {
			return;
		}
		
		menus.get(menuName).allignElements();
	}
	
	public static void mouseMoved(Point mousePos) {
		UIElement hovered;
		
		for (int i = 0; i < activeMenus.size(); ++i) {
			if (!menus.containsKey(activeMenus.get(i)))
				continue;
			
			hovered = menus.get(activeMenus.get(i)).checkHover(mousePos);
			
			if (hovered != null) {
				
				if (hoveredElement == hovered) continue;
				if (hoveredElement != null) {
					hoveredElement.highlighted = false;
					
					hoveredElement.release();
				}
				
				hoveredElement = hovered;
				
				hoveredElement.highlighted = true;
			}
		}
	}
	
	//methods
	public static UIElement getElement(String elementName) {
		if (elementName.isEmpty()) return null;
		String[] split = elementName.split("\\.");
		if (split.length < 2) return null;
		
		Menu toReturn = menus.get(split[0]);
		if (toReturn == null) return null;
		
		return toReturn.getElement(split[1]);
	}
	
	//data
	private static Map<String, Color> colors = new HashMap<>();
	private static Map<String, String> colorMap = new HashMap<>();
	private static Map<String, Font> fontMap = new HashMap<>();
	private static Map<String, String> strings = new HashMap<>();
	private static Map<String, StringHandler> externalStringHandlers = new HashMap<>();
	public static Map<String, AbstractList<String>> stringLists = new HashMap<>();
	
	public static Set<Object> requested = new HashSet<>();
	//
	
	//getters
	public static Color getColor(String name) {
		if (colors.containsKey(name))
			return colors.get(name);
		if (colorMap.containsKey(name))
			return getColor(colorMap.get(name));
		
		try {
			Object obj = Color.class.getField(name).get(null);
			if (obj != null) setColor(name, (Color) obj);
			return colors.get(name);
		}catch(Exception e) {}
		
		return null;
	}
	
	public static Font getFont(String name) {
		if (name.startsWith("font"))
			name = name.substring(4);
		
		if (fontMap.containsKey(name)) {
			return fontMap.get(name);
		}
		return null;
	}
	
	public static float getScale() {
		return scale;
	}
	public static float getFontScale() {
		return fontScale;
	}
	
	public static AbstractList<String> getList(String name) {
		return stringLists.get(name);
	}
	
	private static String getString(String key) {
		if (strings.containsKey(key)) {
			return strings.get(key);
		}else{
			UIElement element = getElement(key);
			
			if (element != null)
				return element.getReturnValue();
		}
		
		//external checking
		String[] split = key.split("\\.");
		if (split.length < 2) return key;
		
		if (externalStringHandlers.containsKey(split[0])) {
			return externalStringHandlers.get(split[0]).getString(key.substring(split[0].length()+1));
		}
		
		return key;
	}
	
	
	public static String reconstructString(String key) {
		return reconstructString(key, true);
	}
	public static String reconstructString(String key, boolean includeNull) {
		if (key == null) return "";
		
		{
			String tester = getString(key);
			if (!tester.equals(key)) {
				return tester;
			}
		}
		
		StringBuilder builder = new StringBuilder("");
		
		
		StringReader reader = new StringReader(key);
		
		char c;
		while (reader.hasNext()) {
			c = reader.next();
			
			if (c == '\\') {
				if (reader.hasNext())
					if (reader.next() == '$') {
						StringBuilder recursor = new StringBuilder();
						String text, str;
						int scope = 0;
						do  {
							if (reader.current() == '$')
								scope++;
							else {
								scope--;
								if (scope == 0) break;
								reader.previous();
							}
							
							text = reader.advanceTo('\\');
							str = reconstructString(getString(text), includeNull);
							
							if (includeNull || !text.equals(str))
								recursor.append(str);//recursive special strings (\$specialString\)
							
						}while (reader.hasNext() && (reader.next() == '$' || scope > 0));
						reader.previous();
						
						//if (includeNull || !text.equals(recursor.toString()))
						builder.append(getString(recursor.toString()));
					}else{
						builder.append(reader.previous());
					}
			}else
				builder.append(c);
		}
		
		return builder.toString();
	}
	
	public static String stripDeco(String str) {
		return str.replaceAll("(\\\\c:.*\\\\|\\\\f:.*\\\\)", "").replaceAll("\\\\\\$(.*)\\\\", "$1");
	}
	
	//fonts
	
	//keybinds
	public static String getKeyText(Integer key) {
		if (key == -1)
			return null;
		String str;
		
		str = KeyEvent.getKeyModifiersText(key>>16);
		
		StringBuilder builder = new StringBuilder(str);
		String keyString = KeyEvent.getKeyText(key&65535);
		
		if (builder.length() == 0 || !str.contains(keyString)) {
			if (builder.length() != 0)
				builder.append("+");
			
			if (keyString.startsWith("Unknown keyCode: "))
				builder.append(keyString.substring("Unknown keyCode: ".length()));
			else
				builder.append(keyString);
		}
		
		return builder.toString();
	}
	
	//initializers / parsers
	private static void parseColors(StringReader reader) {
		reader.remove("\\s");
		reader.previous();
		
		while (reader.hasNext()) {
			colors.put(reader.advanceTo(':'), new Color(
					Integer.parseInt(reader.advanceTo(',')),
					Integer.parseInt(reader.advanceTo(',')),
					Integer.parseInt(reader.advanceTo(';'))));
		}
	}
	private static void parseTheme(StringReader reader, String themeName) {
		reader.remove("\\s");
		reader.previous();
		
		themeName = themeName.substring(0, themeName.length()-4);
		
		String propName, propType;
		
		while (reader.hasNext()) {
			propType = reader.advanceTo(':');
			propName = reader.advanceTo(':');
			
			String fontName;
			
			switch (propType) {
			case "font":
				fontName = reader.advanceTo(',').replace('_', ' ');
				fontMap.put(propName, new engine.ux.primitives.Font(
						fontName,
						Integer.parseInt(reader.advanceTo(';'))));
				break;
			
			case "color":
				if (reader.next() == '$')
					colorMap.put(propName, reader.advanceTo(';'));
				else {
					reader.previous();
					
					colors.put(propName, new Color(
							Integer.parseInt(reader.advanceTo(',')),
							Integer.parseInt(reader.advanceTo(',')),
							Integer.parseInt(reader.advanceTo(';'))));
				}
				break;
			}
			
		}
	}
	
	public static void parseList(String listName, StringReader reader) {
		reader.advanceTo('\n');
		
		while (reader.hasNext()) {
			addListElement(listName, reader.advanceTo('\n'));
		}
	}
	
	public static void parseStrings(StringReader reader) {
		reader.remove("(\t|\n)");//kill all the tabs and line spacing
		reader.previous();
		StringBuilder builder;
		
		String key;
		while (reader.hasNext()) {
			builder = new StringBuilder();
			key = reader.advanceTo('=');
			reader.advanceTo('\"');//first quote
			
			while (true) {
				builder.append(reader.advanceTo('\"'));
				
				if (reader.previous() == '\\')
					builder.append(reader.next());
				else
					break;
			}
			
			reader.next();
			setString(key.substring(0, key.length()-1), builder.toString());
		}
	}
	
	public static void loadDataFile(String fileName) {
		loadDataFile(FileIO.getFile(fileName));
	}
	public static void loadDataFile(File file) {
		if (file == null || !file.exists()) return;
		
		StringReader reader;
		try {
			reader = new StringReader(FileIO.toString(file));
		} catch (FileNotFoundException e) {
			Logger.logException(e);
			return;
		}
		
		switch (reader.advanceTo('\n')) {
		case "Colors":
			parseColors(reader);
			break;
		case "Theme":
			parseTheme(reader, file.getName());
			break;
		case "Lang":
		case "Localization":
		case "Strings":
			parseStrings(reader);
			break;
		case "GUIList":
			parseList(file.getName().substring(0, file.getName().lastIndexOf('.')), reader);
			break;
		}
	}
	
	//setters
	public static void setColor(String name, Color color) {
		colors.put(name, color);
	}
	
	public static void addList(String name) {
		if (stringLists.containsKey(name)) return;
		
		stringLists.put(name, new ArrayList<>());
		EventBus.broadcast("GUILoadData", "gui/lists/" + name + ".txt");
	}
	public static void addListElement(String listName, String element) {
		if (stringLists.get(listName).contains(element))
			return;
		stringLists.get(listName).add(element);
	}
	public static void clearList(String name) {
		if (!stringLists.containsKey(name)) return;
		
		stringLists.remove(name);
		addList(name);
		
//		stringLists.get(name).clear();
	}
	
	public static void setString(String key, String value) {
		strings.put(key, value);
	}
	public static void addStringHandler(String key, StringHandler handler) {
		externalStringHandlers.put(key, handler);
	}
	
	public static void addUpdateScript(LuaValue function) {
		updateScripts.add(function);
	}
}
