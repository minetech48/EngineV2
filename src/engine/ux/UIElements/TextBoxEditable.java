package engine.ux.UIElements;

import engine.core.Logger;
import engine.util.vector.Vec2i;
import engine.ux.GUI;
import engine.ux.GraphicsWrapper;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class TextBoxEditable extends UIElement {
	
	ArrayList<StringBuilder> contents;
	Vec2i cursorPos;
	int tempCursorX, y;
	
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
				if (cursorPos.x == 0) {
					if (cursorPos.y > 0) {
						contents.remove(cursorPos.y);
						
						cursorPos.y--;
						cursorPos.x = contents.get(cursorPos.y).length();
						
						contents.get(cursorPos.y).append(currentLine);
					}
				}
				else {
					currentLine.deleteCharAt(cursorPos.x - 1);
					cursorPos.x--;
					cursorMoved(true);
				}
				break;
			case 127://delete
				if (cursorPos.x == currentLine.length() && cursorPos.y < contents.size()-1) {
					currentLine.append(contents.get(cursorPos.y+1));
					
					contents.remove(cursorPos.y+1);
				}else if (cursorPos.x < currentLine.length())
					currentLine.deleteCharAt(cursorPos.x);
				break;
			case 10://enter
				contents.add(cursorPos.y+1, new StringBuilder(currentLine.substring(cursorPos.x)));
				currentLine.delete(cursorPos.x, currentLine.length());
				
				cursorPos.y++;
				cursorPos.x = 0;
				cursorMoved(false);
				break;
				
			case 24://ctrl + x
				break;
			case 3://ctrl + c
				break;
			case 22://ctrl + v
				try {
					Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
					
					if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						String text = t.getTransferData(DataFlavor.stringFlavor).toString();
						
						String[] split = text.split("\n");
						
						
						for (String str : split) {
							contents.get(cursorPos.y).insert(cursorPos.x, str);
							
							cursorPos.x+= str.length();
							cursorMoved(true);
							
							keyPressed((char) 10, 0);
						}
						keyPressed((char) 8, 0);
						
					}
				} catch (UnsupportedFlavorException | IOException e) {
					Logger.log(e);
				}
				break;
				
			case 26://ctrl + z
				break;
			case 27://ctrl + y
				break;
				
			default:
				if ((modifiers & KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK)
					currentLine.insert(cursorPos.x, c);
				else
					currentLine.insert(cursorPos.x, Character.toLowerCase(c));
				
				cursorPos.x++;
				cursorMoved(true);
		}
		
		System.out.println((int) c);
		
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
		
		cursorMoved(horizontal);
		
		return true;
	}
	
	public void cursorMoved(boolean horizontal) {
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
					cursorPos.x = contents.get(cursorPos.y-1).length();
			}
		}
		
		if (cursorPos.y < 0)
			cursorPos.y = 0;
		if (cursorPos.y > contents.size()-1)
			cursorPos.y = contents.size()-1;
		
		if (!horizontal) {
			if (cursorPos.x >= contents.get(cursorPos.y).length()) {
				tempCursorX = Math.max(tempCursorX, cursorPos.x);
				cursorPos.x = contents.get(cursorPos.y).length();
			}else if (tempCursorX > -1)
				cursorPos.x = tempCursorX;
		}
	}
	
	public void click() {
		if (GUI.getFont("editorFont") != null)
			GUI.graphics.setFont(GUI.getFont("editorFont"));
		else
			GUI.graphics.setFont(GUI.getFont("primary"));
		
		//vertical
		cursorPos.y = ((GUI.window.mousePosition.y - getY()) / GUI.graphics.getTextLineHeight());
		y = cursorPos.y;
		
		cursorMoved(false);
		
		//horizontal
		int width = 0, mousePosX = GUI.window.mousePosition.x - getX(), difference = 0;
		boolean set = false;
		for (int x = 0; x < contents.get(cursorPos.y).length(); x++) {
			difference = width;
			width = GUI.graphics.getTextSize(contents.get(cursorPos.y).substring(0, x+1)).width;
			difference = width - difference;
			
			if (width >= mousePosX) {
				cursorPos.x = x;
				
				if (width < mousePosX + difference/2) {
					cursorPos.x++;
				}
				
				set = true;
				break;
			}
		}
		
		if (!set) {
			cursorPos.x = contents.get(cursorPos.y).length();
		}
		
		cursorMoved(true);
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
		
		if (GUI.getFont("editorFont") != null)
			GUI.graphics.setFont(GUI.getFont("editorFont"));
		else
			GUI.graphics.setFont(GUI.getFont("primary"));
		
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
		//element.cursorPos.y = ((GUI.window.mousePosition.y - element.getY()) / GUI.graphics.getTextLineHeight());
		if (element.cursorPos.x > -1)
			GUI.graphics.fillRect(
					element.getX() + GUI.graphics.getTextSize(element.contents.get(element.cursorPos.y).substring(0, element.cursorPos.x)).width,
					element.getY() + GUI.graphics.getTextLineHeight() * element.cursorPos.y,
					1, GUI.graphics.getTextLineHeight());
	}
}
