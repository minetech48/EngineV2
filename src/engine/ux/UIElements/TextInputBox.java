package engine.ux.UIElements;

import java.awt.event.KeyEvent;

@Deprecated
public class TextInputBox extends Button {
	
	private String emptyText;
	private StringBuilder currentText;
	
	private boolean clearOnShow = true;
	
	public TextInputBox(String[] args) {
		super(args);
		
		currentText = new StringBuilder();
	}
	
	public void draw() {
		if (currentText.length() > 0)
			text = currentText.toString();
		else
			text = emptyText;
		
		super.draw();
	}
	
	//events
	//click supression
	public void click() {}
	
	public void onShow() {
		if (clearOnShow) {
			currentText = new StringBuilder();
		}
	}
	
	public boolean keyPressed(char c, int modifiers) {
		if (super.keyPressed(c, modifiers)) return true;
		
		if (c == 16 || c == 17) return true;
		
		if (c == 8) {
			if (currentText.length() > 0)
				currentText.deleteCharAt(currentText.length()-1);
			
		}else if (c == 10)
			release();
		else if ((modifiers & KeyEvent.CTRL_MASK) == KeyEvent.CTRL_MASK)
			;//currentText.append("ctrl");
		else if ((modifiers & KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK)
			currentText.append(c);
		else
			currentText.append(Character.toLowerCase(c));
		
		return true;
	}
//	public void intInput(int i) {
//		charInput((char) i, i >> 16);
//	}
	
	public String getReturnValue() {
		return currentText.toString();
	}
	
	public void setText(String text) {
		this.emptyText = text;
	}
	
	public void clearOnShow(String bool) {
		clearOnShow = Boolean.valueOf(bool);
	}
}
