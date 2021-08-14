package engine.ux.UIElements;

import java.util.AbstractList;

import engine.core.EventBus;
import engine.ux.GUI;

public class Button extends Textbox {
	
	protected String[] onClickEvents, onReleaseEvents, onDoubleClickEvents;
	private long clickTime;
	
	public boolean isSwitch;
	
	public String text, textActive;
	
	public Button(String[] args) {
		super(args);
	}
	public Button(String name) {
		super(name);
	}
	public Button(String name, int x, int y, int width, int height) {
		super(name, x, y, width, height);
		System.out.println(name);
	}
	
	protected void init() {
		onClickEvents = new String[0];
		onReleaseEvents = new String[0];
		onDoubleClickEvents = new String[0];
	}
	
	public void draw() {
		super.draw();
		
		drawButton(this);
	}
	
	
	public void click() {
		if (isSwitch && active) {
			active = false;
			releaseAction();
			return;
		}
		
		active = true;
		
		if (onDoubleClickEvents.length > 0 && EventBus.getTick() - clickTime < EventBus.secondsToTicks(0.7f)) {
			doubleClick();
			return;
		}
		
		clickAction();
		clickTime = EventBus.getTick();
	}
	public void clickAction() {
		for (String str : onClickEvents) {
			GUI.broadcastEvent(str);
		}
	}
	
	public void release() {
		if (isSwitch) return;
		
		active = false;
		
		releaseAction();
		
	}
	public void releaseAction() {
		for (String str : onReleaseEvents) {
			GUI.broadcastEvent(str);
		}
	}
	
	public void doubleClick() {
		for (String str : onDoubleClickEvents) {
			GUI.broadcastEvent(str);
		}
	}
	
	public boolean keyPressed(char key, int modifiers) {
		if (super.keyPressed(key, modifiers))
			return true;
		
		if (key == '\n')
			clickAction();
		
		return false;
	}
	
	//setters
	public void setOnClick(String event) {
		onClickEvents = new String[] {event};
	}
	public void setOnClick(AbstractList<String> eventList) {
		String[] strList = new String[eventList.size()];
		
		for (int i = strList.length-1; i >= 0; i--) {
			strList[i] = eventList.get(i);
		}
		
		onClickEvents = strList;
	}
	
	public void setOnDoubleClick(AbstractList<String> eventList) {
		String[] strList = new String[eventList.size()];
		
		for (int i = strList.length-1; i >= 0; i--) {
			strList[i] = eventList.get(i);
		}
		
		onDoubleClickEvents = strList;
	}
	public void setOnDoubleClick(String event) {
		onDoubleClickEvents = new String[] {event};
	}
	
	public void setOnRelease(String event) {
		onReleaseEvents = new String[] {event};
	}
	public void setOnRelease(AbstractList<String> eventList) {
		String[] strList = new String[eventList.size()];
		
		for (int i = strList.length-1; i >= 0; i--) {
			strList[i] = eventList.get(i);
		}
		
		onReleaseEvents = strList;
	}
	
//	public void setText(String text) {
//		this.text = text;
//	}
//	public void setTextActive(String text) {
//		this.activeText = text;
//	}
	
	public void setToggle(String bool) {
		isSwitch = Boolean.parseBoolean(bool);
	}
	
	//drawing
	public static void drawButton(Button element) {
		drawElement(element);
		
		GUI.graphics.setColor(GUI.getColor("text"));
		
		if (element.fontName == null)
			GUI.graphics.setFont(GUI.getFont("primary"));
		else
			GUI.graphics.setFont(GUI.getFont(element.fontName));
		
		GUI.graphics.drawStringCentered(element.text, element.getX(), element.getY(), element.getWidth(), element.getHeight());
	}
}