package engine.ux.UIElements;

import engine.core.Logger;
import engine.ux.GUI;
import engine.ux.GraphicsWrapper;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class GridContainer extends Container {
	
	int width, height, elementWidth, elementHeight, border;
	
	public GridContainer(String[] args) {
		super(args);
		
		elements = new ArrayList<>();
	}
	
	public void alignElements() {
		elementWidth = getWidth() / width;
		elementHeight = getHeight() / height;
		
		super.alignElements();
	}
	
	void alignElement(UIElement element) {
		element.bounds.x = element.layoutBounds.x * (elementWidth) + border;
		element.bounds.y = element.layoutBounds.y * (elementHeight) + border;
		
		element.bounds.z = element.layoutBounds.z * (elementWidth) - border*2;
		element.bounds.w = element.layoutBounds.w * (elementHeight) - border*2;
	}
	
	//setters
	public void setWidth(String width) {
		this.width = Integer.parseInt(width);
	}
	public void setHeight(String height) {
		this.height = Integer.parseInt(height);
	}
	public void setBorder(String border) {
		this.border = Integer.parseInt(border) / 2;
	}

//	public void draw() {
//		drawGrid(GUI.graphics);
//		super.draw();
//	}
//
//	public void drawGrid(GraphicsWrapper g) {
//		g.setColor(Color.black);
//
//		for (int col = 0; col <= height; col++) {
//			g.drawLine(
//					col * elementWidth,
//					0,
//					col * elementWidth,
//					elementHeight*height);
//		}
//
//		for (int row = 0; row <= width; row++) {
//			g.drawLine(
//					0,
//					row * elementHeight,
//					elementWidth*width,
//					row * elementHeight);
//		}
//	}
}
