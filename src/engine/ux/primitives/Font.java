package engine.ux.primitives;

import engine.ux.GUI;

public class Font {
	
	private String fontName;
	
	private float scale;
	private int fontSize;
	
	private java.awt.Font font;
	
	public Font(String name, int size) {
		fontName = name;
		fontSize = size;
	}
	
	public void clearFont() {
		font = null;
	}
	public void resetFont() {
		font = new java.awt.Font(fontName, java.awt.Font.PLAIN, (int) (fontSize*scale));
	}
	
	//setters
	public void setSize(int newSize) {
		fontSize = newSize;
		
		resetFont();
	}
	
	//getters
	public String getName() {
		return fontName;
	}
	
	public int getSize() {
		return getAwtFont().getSize();
	}
	public int getFontSize() {
		return fontSize;
	}
	
	public java.awt.Font getAwtFont() {
		if (scale != GUI.getFontScale()) {
			scale = GUI.getFontScale();
			resetFont();
		}
		if (font == null) resetFont();
		return font;
	}
}
