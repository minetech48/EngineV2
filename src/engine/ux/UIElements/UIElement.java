package engine.ux.UIElements;

import java.awt.Color;
import java.awt.Point;

import engine.ux.GUI;
import engine.util.vector.Vec4i;

public class UIElement {
	
	public static enum Alignment {
		LEFT,
		RIGHT,
		TOP,
		BOTTOM,
		CENTER
	}
	public static enum Constraints {
		PARENT,
		CENTER,
		ELEMENT
	}
	
	public UIElement parent;
	public String name;
	
	public Vec4i layoutBounds, bounds;
	public Alignment alignmentX = Alignment.LEFT, alignmentY = Alignment.TOP;
	protected Constraints constraint = Constraints.PARENT;
	
	public boolean highlighted, focused, active;
	
	protected String nextElement;
	
	public UIElement(String[] args) {
		this(args[0]);
		
		if (args.length > 4) {
			layoutBounds.x = Integer.parseInt(args[1]);
			layoutBounds.y = Integer.parseInt(args[2]);
			layoutBounds.z = Integer.parseInt(args[3]);
			layoutBounds.w = Integer.parseInt(args[4]);
		}else if (args.length > 3) {
			name = null;
			layoutBounds.x = Integer.parseInt(args[0]);
			layoutBounds.y = Integer.parseInt(args[1]);
			layoutBounds.z = Integer.parseInt(args[2]);
			layoutBounds.w = Integer.parseInt(args[3]);
		}
	}
	public UIElement(String name) {
		this(name, 0, 0, 0, 0);
	}
	
	public UIElement(String name, int x, int y, int width, int height) {
		this.name = name;
		layoutBounds = new Vec4i(x, y, width, height);
		bounds = new Vec4i();
		
		init();
	}
	
	protected void init() {};
	
	//events
	public void draw() {
		drawElement(this);
	}
	
	public void onShow() {}
	public void click() {}
	public void click(int button, int x, int y) {}
	public void release() {}
	public void hover() {}
	public void scroll(int amt) {}
	public boolean keyPressed(char c, int modifiers) {
		if (c == 9 && nextElement != null) {
			focused = false;
			
			GUI.focusedElement = GUI.getElement(parent.name + "." + nextElement);
			
			if (GUI.focusedElement != null)
				GUI.focusedElement.focused = true;
			
			return true;
		}
		
		return false;
	}
	
	private int getConstraintX(UIElement parent) {
		switch(constraint) {
		case PARENT:
			return parent.getWidth();
		case CENTER:
			return parent.getWidth()/2;
		case ELEMENT:
			return 0;
		}
		return 0;
	}
	public void align(Menu parent) {
		switch (alignmentX) {
		case LEFT:
			bounds.x = layoutBounds.x;
			bounds.z = layoutBounds.z > 0 ?
					layoutBounds.z :
						getConstraintX(parent) + layoutBounds.z - bounds.x;
			break;
			
		case RIGHT:
			bounds.x = parent.getWidth() - layoutBounds.x;
			bounds.z = layoutBounds.z > 0 ?
					layoutBounds.z :
					parent.getWidth() - layoutBounds.z - bounds.x;
			
			if (layoutBounds.z > 0) {
				bounds.z = layoutBounds.z;
				bounds.x = parent.getWidth() - bounds.z - layoutBounds.x;
			}else{
				bounds.x = parent.getWidth() - getConstraintX(parent) -layoutBounds.z;
				bounds.z = getConstraintX(parent) + layoutBounds.z - layoutBounds.x;
			}
			break;
			
		case CENTER:
			bounds.x = parent.getWidth()/2 + layoutBounds.x;
			bounds.z = layoutBounds.z;
			break;
			
		default:
			//GUI.definitionError();
			System.err.println("GUI definition error: " + name);
			break;
		}
		
		switch (alignmentY) {
		case TOP:
			bounds.y = layoutBounds.y;
			bounds.w = layoutBounds.w > 0 ?
					layoutBounds.w :
					parent.getHeight() + layoutBounds.w - bounds.y;
			break;
		case BOTTOM:
			bounds.y = getHeight() - layoutBounds.y;
			bounds.w = layoutBounds.w > 0 ?
					layoutBounds.w :
					parent.getHeight() - layoutBounds.w - bounds.y;
			
			if (layoutBounds.w > 0) {
				bounds.w = layoutBounds.w;
				bounds.y = parent.getHeight() - bounds.w - layoutBounds.y;
			}else{
				bounds.y = -layoutBounds.w;
				bounds.w = parent.getHeight() + layoutBounds.w - layoutBounds.y;
			}
			break;
		case CENTER:
			bounds.y = parent.getHeight()/2 + layoutBounds.y;
			bounds.w = layoutBounds.w;
			break;
		default:
			//GUI.definitionError();
			System.err.println("GUI definition error: " + name);
		}
	}
	
	//checks
	public UIElement checkHover(Point p) {
		if (engine.util.Math.inBounds(bounds, p))
			return this;
		return null;
	}
	
	public String getReturnValue() {
		return "";
	}
	
	//setters
	public void setName(String name) {
		this.name = name;
	}
	
	public void setBounds(int x, int y, int width, int height) {
		if (bounds == null)
			bounds = new Vec4i(x, y, width, height);
		else {
			bounds.x = x;
			bounds.y = y;
			bounds.z = width;
			bounds.w = height;
		}
	}
	
	public void setNextElement(String elementName) {
		nextElement = elementName;
	}
	
	public void setConstraint(String constraint) {
		this.constraint = Constraints.valueOf(constraint.toUpperCase());
	}
	public void setAlignment(String newAlignment) {
		String[] split = newAlignment.split("-");
		split[0] = split[0].toLowerCase();
		
		if (split.length > 1) {
			split[1] = split[1].toLowerCase();
			
			switch(split[0]) {
			case "top":
				alignmentY = Alignment.TOP;
				break;
			case "bottom":
				alignmentY = Alignment.BOTTOM;
				break;
			case "left":
				alignmentX = Alignment.LEFT;
				break;
			case "right":
				alignmentX = Alignment.RIGHT;
				break;
			case "center":
				alignmentX = Alignment.CENTER;
				break;
			}
			switch(split[1]) {
			case "top":
				alignmentY = Alignment.TOP;
				break;
			case "bottom":
				alignmentY = Alignment.BOTTOM;
				break;
			case "left":
				alignmentX = Alignment.LEFT;
				break;
			case "right":
				alignmentX = Alignment.RIGHT;
				break;
			case "center":
				alignmentY = Alignment.CENTER;
				break;
			}
		}else{
			switch(split[0]) {
			case "top":
				alignmentY = Alignment.TOP;
				alignmentX = Alignment.CENTER;
				break;
			case "bottom":
				alignmentY = Alignment.BOTTOM;
				alignmentX = Alignment.CENTER;
				break;
			case "left":
				alignmentX = Alignment.LEFT;
				alignmentY = Alignment.CENTER;
				break;
			case "right":
				alignmentX = Alignment.RIGHT;
				alignmentY = Alignment.CENTER;
				break;
			case "center":
				alignmentX = Alignment.CENTER;
				alignmentY = Alignment.CENTER;
				break;
			}
		}
		
		
	}
	
	//getters
	public Vec4i getBounds() {
		return bounds;
	}
	
	public String getName() {
		return name;
	}
	
	public int getX() {return bounds.x;}
	public int getY() {return bounds.y;}
	public int getWidth() {return bounds.z;}
	public int getHeight() {return bounds.w;}
	
	//drawing (static)
	public static void drawElement(UIElement element) {
		drawElementBase(element);
		
		//highlight and focus
//		if (element.focused) {
//			GUI.graphics.setColor(GUI.getColor("borderFocused"));
//			rect(element, false);
			
		//}else 
		if (element.highlighted) {
			GUI.graphics.setColor(GUI.getColor("highlight"));
			GUI.graphics.setAlpha(0.2f);
			rect(element, true);
			GUI.graphics.setAlpha(1f);
			
			
			rect(element, false);
		}else{
			return;
		}
		
	}
	
	public static void drawElementBase(UIElement element) {
		Color primary = null;
		Color secondary = null;
		
		//idle
		if (element.active) {
			primary = GUI.getColor("active");
			secondary = GUI.getColor("borderActive");
		}
//		else if (element.focused) { 
//			primary = GUI.getColor("backgroundFocused");
//			secondary = GUI.getColor("borderFocused");
//		}
		
		if (primary == null)
			primary = GUI.getColor("background");
		if (secondary == null)
			secondary = GUI.getColor("border");
		
		//background
		GUI.graphics.setColor(primary);
		rect(element, true);
		
		//border

		GUI.graphics.setColor(secondary);
		GUI.graphics.setSize(GUI.getScale());
		
		rect(element, false);
	}
	
	protected static void rect(UIElement element, boolean fill) {
		if (fill)
			GUI.graphics.fillRect(	element.getX(),
					element.getY(),
					element.getWidth(),
					element.getHeight());
		else
			GUI.graphics.drawRect(	element.getX(),
					element.getY(),
					element.getWidth(),
					element.getHeight());
			
	}
}
