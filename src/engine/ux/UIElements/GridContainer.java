package engine.ux.UIElements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GridContainer extends Container {
	
	int width, height, elementWidth, elementHeight, border;
	
	public GridContainer(String[] args) {
		super(args);
		
		elementMap = new HashMap<>();
		elements = new ArrayList<>();
	}
	
	public void align(Menu parent) {
		super.align(parent);
		
		elementWidth = getWidth() / width;
		elementHeight = getHeight() / height;
		
		for (UIElement element : getElements()) {
			allignElement(element);
		}
	}
	
	private void allignElement(UIElement element) {
		element.bounds.x = element.layoutBounds.x * (elementWidth+border);
		element.bounds.y = element.layoutBounds.y * (elementHeight+border);
		
		element.bounds.z = element.layoutBounds.z * (elementWidth-border);
		element.bounds.w = element.layoutBounds.w * (elementHeight-border);
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
}
