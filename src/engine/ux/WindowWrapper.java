package engine.ux;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import engine.core.EventBus;
import engine.ux.UIElements.Menu;

public class WindowWrapper extends Menu {
	
	protected JFrame frame;
	protected JPanel panel;
	
	protected boolean mouseMoved;
	public Point mousePosition;
	
	private ArrayList<engine.ux.KeyListener> keyListeners = new ArrayList<>();
	private ArrayList<engine.ux.MouseListener> mouseListeners = new ArrayList<>();
	
	public WindowWrapper() {
		frame = new JFrame();
		panel = new JPanel() {
			public void paint(Graphics g) {
				GUI.draw(g);
			}
		};
		
		
		frame.add(panel);
		frame.setFocusTraversalKeysEnabled(false);
		
		setSize(1120, 630);
		
		addListeners();
	}

	
	public void setSize(int width, int height) {
		panel.setPreferredSize(new Dimension(width, height));
		frame.pack();
	}
	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}
	
	public Dimension getSize() {
		return panel.getSize();
	}
	public int getWidth() {
		return panel.getSize().width;
	}
	public int getHeight() {
		return panel.getSize().height;
	}
	
	public void exit() {
		frame.dispose();
		
		EventBus.shutdown();
	}

	public void addKeyListener(engine.ux.KeyListener listener) {
		keyListeners.add(listener);
	}
	
	public void addMouseListener(engine.ux.MouseListener listener) {
		mouseListeners.add(listener);
	}
	
	public Set<String> keysDown = new HashSet<>();
	public void addListeners() {
		frame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			public void keyPressed(KeyEvent e) {
				int modifiers = e.getModifiers()<<16;
				int keyCode = e.getKeyCode();
				
				if (GUI.focusedElement != null) {
					if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
						if (GUI.focusedElement.keyPressed(KeyEvent.getKeyText(keyCode), e.getModifiers())) {
							return;
						}
					}else{
						if (GUI.focusedElement.keyPressed(e.getKeyChar(), e.getModifiers())) {
							return;
						}
					}
				}
				
				if (!keysDown.contains(KeyEvent.getKeyText(e.getKeyCode()))) {
					keysDown.add(KeyEvent.getKeyText(e.getKeyCode()));
					
					for (engine.ux.KeyListener listener : keyListeners) {
						listener.keyPressed(KeyEvent.getKeyText(e.getKeyCode()), e.getKeyCode(), e.getModifiers());
					}
				}
			}
			public void keyReleased(KeyEvent e) {
				String keyText = KeyEvent.getKeyText(e.getKeyCode());
				
				if (keysDown.contains(keyText)) {
					keysDown.remove(keyText);
					
					for (engine.ux.KeyListener listener : keyListeners) {
						listener.keyReleased(KeyEvent.getKeyText(e.getKeyCode()), e.getKeyCode(), e.getModifiers());
					}
				}
			}
		});
		
		panel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				for (engine.ux.MouseListener listener : mouseListeners) {
					listener.mouseReleased(e.getButton(), e.getX(), e.getY());
				}
				
				if (GUI.hoveredElement != null) {
					GUI.hoveredElement.release();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				for (engine.ux.MouseListener listener : mouseListeners) {
					listener.mousePressed(e.getButton(), e.getX(), e.getY());
				}
				
				if (GUI.hoveredElement != null) {
					if (GUI.focusedElement != null)
						GUI.focusedElement.focused = false;
					
					GUI.hoveredElement.click();
					GUI.hoveredElement.click(e.getButton(), e.getX(), e.getY());
					GUI.focusedElement = GUI.hoveredElement;
					
					GUI.focusedElement.focused = true;
				}else{
					if (GUI.focusedElement != null)
						GUI.focusedElement.focused = false;
					GUI.focusedElement = null;
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
		panel.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseMoved = true;
				
				mousePosition = e.getPoint();
				
				for (engine.ux.MouseListener listener : mouseListeners) {
					listener.mouseMoved(e.getX(), e.getY());
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				mouseMoved = true;
				
				mousePosition = e.getPoint();
				
				for (engine.ux.MouseListener listener : mouseListeners) {
					listener.mouseMoved(e.getX(), e.getY());
				}
			}
		});
		
		panel.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (GUI.hoveredElement != null) {
					GUI.hoveredElement.scroll(e.getWheelRotation());
				}
			}
		});
		
		frame.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent event) {
		        exit();
		    }
		});
		
		frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
            	GUI.panelResized(panel.getWidth(), panel.getHeight());
            	
            	//GUI.scale = panel.getWidth()/240;
            }
		});
	}
}
