package engine.core;

public class ContainerEvent extends Event {
	
	protected Object contents;
	
	public ContainerEvent(String eventString, String[] args, Object contents) {
		super(eventString, args);
		
		this.contents = contents;
	}
	
	public Object getContents() {
		return contents;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder(event);
		builder.append(":(");
		
		for (String str : args) {
			builder.append(str);
			builder.append(", ");
		}
		
		builder.append(contents.toString());
		
		builder.append(")");
		
		return builder.toString();
	}
}
