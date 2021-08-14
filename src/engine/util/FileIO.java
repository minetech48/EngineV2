package engine.util;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import engine.core.Logger;

public class FileIO {
	
	//utility
	private static File resourcesDir = new File("resources");
	public static File getResourcesDirectory() {
		return resourcesDir;
	}
	
	public static File getFile(String fileName, boolean createIfNull) {
		if (!createIfNull) return getFile(fileName);
		
		File file = getFile(fileName);
		
		if (file.exists()) return file;
		
		file.getParentFile().mkdirs();
		
		try {
			if (file.isDirectory()) {
				file.mkdir();
			}else{
				file.createNewFile();
			}
		} catch (IOException e) {
			Logger.log(e);
			return null;
		}
		
		return file;
	}
	public static File getFile(String fileName) {
		return new File(FileIO.getResourcesDirectory(), fileName);
	}
	
	public static String toString(File file) throws FileNotFoundException {
		StringBuilder toReturn = new StringBuilder();
		
		Scanner in = new Scanner(file);
		
		while (in.hasNext()) {
			toReturn.append(in.nextLine());
			toReturn.append("\n");
		}
		
		return toReturn.toString();
	}
	
	//data
	private static Map<String, Image> images = new HashMap<>();
	
	//getters
	//UX
	public static Image loadImage(String imageName) {
		if (!images.containsKey(imageName)) {
			try {
				images.put(imageName, ImageIO.read(getFile(imageName)));
			} catch (IOException e) {
				Logger.logException(e);
			}
		}
		
		return images.get(imageName);
	}
}