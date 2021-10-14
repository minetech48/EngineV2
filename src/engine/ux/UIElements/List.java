package engine.ux.UIElements;

import java.awt.Shape;
import java.util.AbstractList;

import engine.util.vector.Vec2i;
import engine.ux.GUI;

public class List extends Button {
	
	public String listName;
	private String prefix, midfix, suffix;
	
	private int width = 1, activeIndex = -1, scroll, offset;
	
	public List(String[] args) {
		super(args);
	}
	public List(String name) {
		super(name);
	}
	
	public List(String name, int x, int y, int width, int height) {
		super(name, x, y, width, height);
	}
	
	public void draw() {
		drawList(this);
	}
	
	//list logic
	public void click() {
		Vec2i mousePos = getMousePos();
		
		if (mousePos == null)
			return;
		
		activeIndex = mousePos.y*width + mousePos.x;
		
		if (activeIndex >= getList().size())
			activeIndex = -1;
		else {
			super.click();
			active = false;
		}
	}
	
	public void clickAction() {
		if (activeIndex < 0) return;
		
		for (String str : onClickEvents) {
			str = str.replace("|ELEMENT|", getList().get(activeIndex));
			GUI.broadcastEvent(str);
		}
	}
	public void releaseAction() {
		for (String str : onReleaseEvents) {
			str = str.replace("|ELEMENT|", getList().get(activeIndex));
			GUI.broadcastEvent(str);
		}
	}
	public void doubleClick() {
		for (String str : onDoubleClickEvents) {
			str = str.replace("|ELEMENT|", getList().get(activeIndex));
			GUI.broadcastEvent(str);
		}
	}
	
	public void scroll(int amt) {
		scroll -= amt;
		offset = (int) (scroll*getElementHeight()*1.5);
		
		if (scroll >= 0) {
			scroll = 0;
			offset = 0;
		}else
		if (-offset > (getList().size()/width+1)*getElementHeight()-getHeight()) {
			scroll+= amt;
			
			if (getList().size()/width*getElementHeight() < getHeight())
				offset = 0;
			else
				offset = -(getList().size()/width+1)*getElementHeight()+getHeight();
		}
	}
	
	public Vec2i getMousePos() {
		if (GUI.window.mousePosition == null) return null;
		
		Vec2i mousePos = new Vec2i(
				GUI.window.mousePosition.x - getX() - parent.bounds.x,
				GUI.window.mousePosition.y - getY() - parent.bounds.y);
		
		mousePos.x = width > 1 ? mousePos.x*width/getWidth() : 0;
		mousePos.y = (mousePos.y-offset) / getElementHeight();
		
		if (mousePos.x >= width)
			mousePos = null;
		
		return mousePos;
	}
	
	public int getActiveListIndex() {return activeIndex;}
	
	
	public int getElementHeight() {
		return (int) (GUI.getFont("primary").getSize() + GUI.getScale()*2);
	}
	
	public AbstractList<String> getList() {
		return GUI.getList(listName);
	}
	
	public String getReturnValue() {
		if (activeIndex < 0 || getList().size() < activeIndex)
			return "";
		
		return getList().get(activeIndex);
	}
	
	//setters
	public void setListName(String name) {
		listName = name;
	}
	
	public void setText(String text) {
		setPrefix(text);
	}
	public void setPrefix(String text) {
		prefix = text;
	}
	public void setMidfix(String text) {
		midfix = text;
	}
	public void setSuffix(String text) {
		suffix = text;
	}
	
	public void setWidth(String width) {
		this.width = Integer.parseInt(width);
	}
	
	//drawing
	public static void drawList(List element) {
		drawElementBase(element);
		
		AbstractList<String> elementList = element.getList();
		
		if (elementList == null || elementList.size() < 1) {
			GUI.addList(element.listName);
			return;
		}
		Shape clip = GUI.graphics.getGraphics().getClip();
		GUI.graphics.getGraphics().setClip(
				element.bounds.x,
				element.bounds.y, 
				element.bounds.z, 
				element.bounds.w);;
		
		Vec2i mousePos = element.getMousePos();
		Vec2i activePos = new Vec2i(
				element.getActiveListIndex()%element.width,
				element.getActiveListIndex()/element.width);
		
		if (element.getActiveListIndex() < 0) activePos = null;
		if (GUI.hoveredElement != element) mousePos = null;
		
		if (mousePos != null && mousePos.equals(activePos))
			mousePos = null;
		
		int elementWidth = element.getWidth()/element.width;
		int elementHeight = element.getElementHeight();
		
		int startPoint = -element.offset/elementHeight;
		int endPoint = elementList.size()/element.width < element.getHeight()/element.getElementHeight() ? 
				elementList.size()/element.width :
				element.getHeight()/element.getElementHeight();
		
		int i = startPoint*element.width;
		for (int y = startPoint; y <= startPoint+endPoint; y++) {
			for (int x = 0; x < element.width; x++) {
				GUI.graphics.setColor(GUI.getColor("background"));
				fillListElement(element, elementWidth, elementHeight, x, y);
				
				GUI.graphics.setColor(GUI.getColor("border"));
				drawListElement(element, elementWidth, elementHeight, x, y);

				if (++i >= elementList.size()) {
					y++;
					break;
				}
			}
		}
		
		//redrawing special elements
		if (mousePos != null && mousePos.y*element.width+mousePos.x < elementList.size()) {
			GUI.graphics.setColor(GUI.getColor("highlight"));
			
			GUI.graphics.setAlpha(0.2f);
			fillListElement(element, elementWidth, elementHeight, mousePos.x, mousePos.y);
			GUI.graphics.setAlpha(1f);
			
			drawListElement(element, elementWidth, elementHeight, mousePos.x, mousePos.y);
		}
		if (activePos != null) {
			GUI.graphics.setColor(GUI.getColor("active"));
			fillListElement(element, elementWidth, elementHeight, activePos.x, activePos.y);
			
			GUI.graphics.setColor(GUI.getColor("activeBorder"));
			drawListElement(element, elementWidth, elementHeight, activePos.x, activePos.y);
		}
		
		//drawing text
		GUI.graphics.setFont(GUI.getFont("primary"));
		i = startPoint*element.width;
		for (int y = startPoint; y <= startPoint+endPoint; y++) {
			for (int x = 0; x < element.width; x++) {
				if (y*element.width + x >= elementList.size())
					continue;
				
				GUI.graphics.setColor(GUI.getColor("text"));
				
				if (element.prefix != null)
					GUI.graphics.drawStringLeft(element.prefix.replace("|ELEMENT|", elementList.get(i)),
							(int) (x*elementWidth + element.getX() + GUI.getScale()), y*elementHeight + element.getY() + element.offset, elementHeight);
				else
					GUI.graphics.drawStringLeft(elementList.get(i),
							(int) (x*elementWidth + element.getX() + GUI.getScale()), y*elementHeight + element.getY() + element.offset, elementHeight);
				
				if (element.midfix != null)
					GUI.graphics.drawStringCentered(element.midfix.replace("|ELEMENT|", elementList.get(i)),
							(int) (x*elementWidth + element.getX()), y*elementHeight + element.getY(), elementWidth, elementHeight);
				if (element.suffix != null)
					GUI.graphics.drawStringRight(element.suffix.replace("|ELEMENT|", elementList.get(i)),
							(int) (x*elementWidth + element.getX() - GUI.getScale()), y*elementHeight + element.getY() + element.offset, elementWidth, elementHeight);
				
				if (++i >= elementList.size()) {
					y++;
					break;
				}
			}
		}
		
		GUI.graphics.getGraphics().setClip(clip);
	}
	
	public static void drawListElement(List element, int width, int height, int x, int y) {
		GUI.graphics.drawRect(
				x*width + element.getX(), 
				y*height+ element.getY() + element.offset,
				width, 
				height);
	}
	public static void fillListElement(List element, int width, int height, int x, int y) {
		GUI.graphics.fillRect(
				x*width + element.getX(), 
				y*height+ element.getY() + element.offset, 
				width, 
				height);
	}
}
