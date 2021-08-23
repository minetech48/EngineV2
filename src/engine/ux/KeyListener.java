package engine.ux;

public interface KeyListener {
	
	public void keyPressed(String keyText, int keyCode, int modifiers);
	
	public void keyReleased(String keyText, int keyCode, int modifiers);
	
}
