package engine.ux.UIElements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Container extends Menu {
	
	public Container(String[] args) {
		super(args);
		
		elementMap = new HashMap<>();
		elements = new ArrayList<>();
	}
}
