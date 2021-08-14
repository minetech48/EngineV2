package engine.ux.UIElements;

import engine.ux.GUI;

public class Textbox extends UIElement {
	
	String text;
	String fontName;
	
	public Textbox(String[] args) {
		super(args);
	}
	public Textbox(String name) {
		super(name);
	}
	
	public Textbox(String name, int x, int y, int width, int height) {
		super(name, x, y, width, height);
	}
	
	public void draw() {
		drawTextbox(this);
	}
	
	//setters
	public void setText(String text) {
		this.text = text;
	}
	public void setFont(String fontName) {
		this.fontName = fontName;
	}
	
	
	//drawing
	public static void drawTextbox(Textbox element) {
		if (element.highlighted) {
			GUI.graphics.setColor(GUI.getColor("highlight"));
			rect(element, false);
		}
		
		if (element.fontName == null)
			GUI.graphics.setFont(GUI.getFont("primary"));
		else
			GUI.graphics.setFont(GUI.getFont(element.fontName));
		
		GUI.graphics.setColor(GUI.getColor("text"));
		
		GUI.graphics.drawString(element.text, (int) (element.getX()+GUI.getScale()), (int) (element.getY()));
	}
}
