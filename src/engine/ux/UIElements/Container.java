package engine.ux.UIElements;

import engine.core.Logger;
import engine.ux.GUI;

import java.awt.*;
import java.util.*;

public class Container extends UIElement {
	
	Menu parentMenu;
	public boolean drawBackground, drawBorder;
	
	protected Map<String, UIElement> elementMap;
	protected ArrayList<UIElement> elements;
	
	public Container(String[] args) {
		super(args);
		
		elementMap = new HashMap<>();
		elements = new ArrayList<>();
	}
	
	public Container() {this("");}
	public Container(String name) {
		super(name);
		
		elementMap = new HashMap<>();
		elements = new ArrayList<>();
	}
	
	public void setParentMenu(Menu parentMenu) {
		this.parentMenu = parentMenu;
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
		
		alignElements();
	}
	
	public void align() {
		alignElements();
	}
	
	public void alignElements() {
		parent.alignElement(this);
		
		for (UIElement element : getElements()) {
			alignElement(element);
			element.align();
		}
	}
	
	public void setElements(AbstractList<UIElement> elements) {
		for (UIElement element : elements) {
			addElement(element);
		}
	}
	
	public UIElement addElement(UIElement element) {
		if (element == null) return null;
		
		elementMap.put(element.getName(), element);
		elements.add(element);
		
		if (element.getName() == null)
			element.setName(element.getClass().getSimpleName() + (elements.size()-1));
		
		element.parent = this;
		
		alignElement(element);
		
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
		for (int i = elements.size()-1; i >= 0; i--) {
			element = elements.get(i);
			
			hovered = element.checkHover(p);
			
			if (hovered != null)
				break;
		}
		p.x+= getX();
		p.y+= getY();
		
		if (hovered != null)
			return hovered;
		
		if (!drawBackground)
			return null;
		return this;
	}
	
	
	public static void drawContainer(UIElement element) {
		if (((Container) element).drawBackground) {
			GUI.graphics.setColor(GUI.getColor("menuBackground"));
			
			GUI.graphics.fillRect(element.getX(),
					element.getY(),
					element.getWidth(),
					element.getHeight());
		}
		
		if (((Container) element).drawBorder) {
			GUI.graphics.setColor(GUI.getColor("border"));
			GUI.graphics.setSize(GUI.getScale());
			
			GUI.graphics.drawRect(element.getX(),
					element.getY(),
					element.getWidth(),
					element.getHeight());
		}
	}
	
	private int getConstraintX(UIElement element) {
		switch(element.constraint) {
			case PARENT:
				return getWidth();
			case CENTER:
				return getWidth()/2;
			case ELEMENT:
				return 0;
		}
		return 0;
	}
	
	void alignElement(UIElement element) {
		switch (element.alignmentX) {
			case LEFT:
				element.bounds.x = element.layoutBounds.x;
				element.bounds.z = element.layoutBounds.z > 0 ?
						element.layoutBounds.z :
						getConstraintX(element) + element.layoutBounds.z - element.bounds.x;
				break;
			
			case RIGHT:
				element.bounds.x = element.parent.getWidth() - element.layoutBounds.x;
				element.bounds.z = element.layoutBounds.z > 0 ?
						element.layoutBounds.z :
						element.parent.getWidth() - element.layoutBounds.z - element.bounds.x;
				
				if (element.layoutBounds.z > 0) {
					element.bounds.z = element.layoutBounds.z;
					element.bounds.x = element.parent.getWidth() - element.bounds.z - element.layoutBounds.x;
				}else{
					element.bounds.x = element.parent.getWidth() - getConstraintX(element) -element.layoutBounds.z;
					element.bounds.z = getConstraintX(element) + element.layoutBounds.z - element.layoutBounds.x;
				}
				break;
			
			case CENTER:
				element.bounds.x = element.parent.getWidth()/2 + element.layoutBounds.x;
				element.bounds.z = element.layoutBounds.z;
				break;
			
			default:
				//GUI.definitionError();
				Logger.err("GUI definition error: " + name);
				break;
		}
		
		switch (element.alignmentY) {
			case TOP:
				element.bounds.y = element.layoutBounds.y;
				element.bounds.w = element.layoutBounds.w > 0 ?
						element.layoutBounds.w :
						element.parent.getHeight() + element.layoutBounds.w - element.bounds.y;
				break;
			case BOTTOM:
				element.bounds.y = getHeight() - element.layoutBounds.y;
				element.bounds.w = element.layoutBounds.w > 0 ?
						element.layoutBounds.w :
						element.parent.getHeight() - element.layoutBounds.w - element.bounds.y;
				
				if (element.layoutBounds.w > 0) {
					element.bounds.w = element.layoutBounds.w;
					element.bounds.y = element.parent.getHeight() - element.bounds.w - element.layoutBounds.y;
				}else{
					element.bounds.y = -element.layoutBounds.w;
					element.bounds.w = element.parent.getHeight() + element.layoutBounds.w - element.layoutBounds.y;
				}
				break;
			case CENTER:
				element.bounds.y = element.parent.getHeight()/2 + element.layoutBounds.y;
				element.bounds.w = element.layoutBounds.w;
				break;
			default:
				//GUI.definitionError();
				Logger.err("GUI definition error: " + name);
		}
	}
}
