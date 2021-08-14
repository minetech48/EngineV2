package engine.util;

public class StringReader {
	
	String string;
	private int idx = -1, max;
	
	public StringReader() {
		this("");
	}
	public StringReader(String string) {//TODO: Char Set of chars to ignore (ignored chars are allowed if in quotes)
		this.string = string;
		max = string.length()-1;
	}
	
	public void reset() {
		idx = -1;
	}
	public void reset(String str) {
		string = str;
		max = string.length()-1;
		
		reset();
	}
	
	//settings
	public void remove(String regex) {
		string = string.replaceAll(regex, "");
		max = string.length()-1;
	}
	
	//checks
	public boolean hasNext() {
		return idx < max;
	}
	
	//methods
	
	public char next() {
		++idx;
		return current();
	}
	public char previous() {
		--idx;
		return current();
	}
	public char current() {
		return string.charAt(idx);
	}
	
	public void goTo(int i) {
		idx = i-1;
	}
	
	public String advanceTo(char end) {
		char currentChar;
		
		StringBuilder builder = new StringBuilder();
		
		while (hasNext() && (currentChar = next()) != end) {
			builder.append(currentChar);
		}
		
		return builder.toString();
	}
	
	public String getString() {
		return string;
	}
	
	public String toString() {
		return "{StringReader(" + string + ", " + idx + ")}";
	}

}
