package engine.ux.UIElements;

import java.awt.Point;
import java.util.*;

import engine.ux.GUI;

public class Menu extends UIElement {
	
	protected Map<String, UIElement> elementMap;
	protected ArrayList<UIElement> elements;
	
	public Menu(String[] args) {
		super(args);
		
		elementMap = new HashMap<>();
		elements = new ArrayList<>();
	}
	
	public Menu() {this("");}
	public Menu(String name) {
		super(name);
		
		elementMap = new HashMap<>();
		elements = new ArrayList<>();
	}
	
	public void onShow() {
		for (UIElement element : elements) {
			element.onShow();
		}
	}
	
	public void draw() {
		drawContainer(this);
		
		GUI.graphics.translate(getX(), getY());
		
		for (UIElement element : elements) {
			element.draw();
		}
		
		GUI.graphics.translate(-getX(), -getY());
		
	}
	
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		
		allignElements();
	}
	
	public void allignElements() {
		align(GUI.window);
		for (UIElement element : getElements()) {
			allignElement(element);
		}
	}
	private void allignElement(UIElement element) {
		element.align(this);
	}
	
	public void setElements(AbstractList<UIElement> elements) {
		for (UIElement element : elements) {
			addElement(element);
		}
	}
	public UIElement addElement(UIElement element) {
		elementMap.put(element.getName(), element);
		
		elements.add(element);
		
		if (element.getName() == null)
			element.setName(element.getClass().getSimpleName() + (elements.size()-1));
		
		element.parent = this;
		
		allignElement(element);
		
		return element;
	}
	
	
	public UIElement getElement(String elementName) {
		return elementMap.get(elementName);
	}
	public ArrayList<UIElement> getElements() {
		return elements;
	}
	
	public UIElement checkHover(Point p) {
		UIElement hovered = null;
		
		if (super.checkHover(p) == null)
			return null;
		
		p.x-= getX();
		p.y-= getY();
		
		UIElement element;
		for (int i = elements.size()-1; i > 0; i--) {
			element = elements.get(i);
			
			hovered = element.checkHover(p);
			
			if (hovered != null)
				break;
		}
		p.x+= getX();
		p.y+= getY();
		
		if (hovered != null)
			return hovered;
		
		return this;
	}
	

	public static void drawContainer(UIElement element) {
		GUI.graphics.setColor(GUI.getColor("menuBackground"));
		
		GUI.graphics.fillRect(	element.getX(),
								element.getY(),
								element.getWidth(),
								element.getHeight());
		
		GUI.graphics.setColor(GUI.getColor("border"));
		GUI.graphics.setSize(GUI.getScale());
		
		GUI.graphics.drawRect(	element.getX(),
								element.getY(),
								element.getWidth(),
								element.getHeight());
	}
}
