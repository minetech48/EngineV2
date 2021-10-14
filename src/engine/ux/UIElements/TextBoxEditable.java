package engine.ux.UIElements;

import engine.util.vector.Vec2i;
import engine.ux.GUI;
import engine.ux.GraphicsWrapper;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class TextBoxEditable extends UIElement {
	
	ArrayList<StringBuilder> contents;
	Vec2i cursorPos;
	int tempCursorX;
	
	public TextBoxEditable(String[] args) {
		super(args);
		
		contents = new ArrayList<>();
		cursorPos = new Vec2i();
	}
	
	
	public void setText(String text) {
		contents.clear();
		
		String[] split = text.split("\n");
		
		for (String str : split) {
			contents.add(new StringBuilder(str));
		}
	}
	
	public boolean keyPressed(char c, int modifiers) {
		if (super.keyPressed(c, modifiers)) return true;
		
//		if (c == 16 || c == 17) return true;
		
		StringBuilder currentLine = contents.get(cursorPos.y);
		
		switch (c) {
			case 16:
			case 17:
				return true;
			case 8://backspace
				if (currentLine.length() > 0) {
					if (cursorPos.x == 0) {
						//TODO: move current line up, deleting newline char
	//					if (cursorPos.y > 0)
	//						currentLine.deleteCharAt(cursorPos.x - 1);
					}
					else {
						currentLine.deleteCharAt(cursorPos.x - 1);
						cursorPos.x--;
						cursorMoved(true);
					}
				}
				break;
			case 127://delete
				currentLine.deleteCharAt(cursorPos.x);
				break;
			case 10://enter
				//TODO: move line down
				contents.add(cursorPos.y+1, new StringBuilder(""));
				cursorPos.y++;
				cursorMoved(false);
				break;
			default:
				if ((modifiers & KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK)
					currentLine.insert(cursorPos.x, c);
				else
					currentLine.insert(cursorPos.x, Character.toLowerCase(c));
				
				cursorPos.x++;
				cursorMoved(true);
		}
		
//		System.out.println((int) c);
		
		return true;
	}
	
	public boolean keyPressed(String keyText, int modifiers) {
		boolean horizontal = false;
		
		switch (keyText) {
			case "Left":
				cursorPos.x--;
				horizontal = true;
				break;
			case "Right":
				horizontal = true;
				cursorPos.x++;
				break;
			case "Up":
				cursorPos.y--;
				break;
			case "Down":
				cursorPos.y++;
				break;
		}
		
		if (horizontal) {
			tempCursorX = -1;
			
			if (cursorPos.x < 0) {
				cursorPos.y--;
				
				if (cursorPos.y > -1)
					cursorPos.x = contents.get(cursorPos.y).length();
				else
					cursorPos.x = 0;
			}else if (cursorPos.x > contents.get(cursorPos.y).length()) {
				cursorPos.y++;
				
				if (cursorPos.y < contents.size())
					cursorPos.x = 0;
				else
					cursorPos.x--;
			}
		}
		
		cursorMoved(horizontal);
		
		return true;
	}
	
	public void cursorMoved(boolean horizontal) {
		if (cursorPos.y < 0)
			cursorPos.y++;
		if (cursorPos.y > contents.size()-1)
			cursorPos.y--;
		
		if (!horizontal) {
			if (cursorPos.x > contents.get(cursorPos.y).length()) {
				tempCursorX = cursorPos.x;
				cursorPos.x = contents.get(cursorPos.y).length();
			}else if (tempCursorX > -1)
				cursorPos.x = tempCursorX;
		}
	}
	
	public void click() {
		cursorPos.x = 0;
		cursorPos.y = 0;
	}
	
	public void draw() {drawTextBoxEditable(this);}
	
	public static void drawTextBoxEditable(TextBoxEditable element) {
		if (element.highlighted || element.focused) {
			GUI.graphics.setColor(GUI.getColor("highlight"));
			rect(element, false);
		}
		GUI.graphics.setColor(GUI.getColor("background"));
		if (element.focused)
			rect(element, true);
		else
			element.cursorPos.x = -1;
		
//		if (element.fontName == null)
//			GUI.graphics.setFont(GUI.getFont("primary"));
//		else
//			GUI.graphics.setFont(GUI.getFont(element.fontName));
		
		GUI.graphics.setColor(GUI.getColor("text"));
		
//		GUI.graphics.drawString(element.text, (int) (element.getX()+GUI.getScale()), (int) (element.getY()));
		
		//drawing text
		int i = 0;
		for (StringBuilder builder : element.contents) {
			GUI.graphics.drawString(builder.toString(),
					(int) (element.getX()+GUI.getScale()), (int) (element.getY() +
							GUI.graphics.getTextLineHeight() * i++));
		}
		
		//drawing cursor
		if (element.cursorPos.x > -1)
			GUI.graphics.fillRect(
					element.getX() + GUI.graphics.getTextSize(element.contents.get(element.cursorPos.y).substring(0, element.cursorPos.x)).width,
					element.getY() + GUI.graphics.getTextLineHeight() * element.cursorPos.y,
					1, GUI.graphics.getTextLineHeight());
	}
}
