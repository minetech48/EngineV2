package engine.core;

import java.io.Serializable;

public class Event implements Serializable {
	
	protected String event;
	protected String[] args;
	
	public Event(String eventString) {
		String[] split = eventString.split("\\\\\\\\");
		
		if (split.length == 1) {
			event = eventString;
			args = EventBus.ARGS_NONE;
		}else{
			event = split[0];
			args = new String[split.length-1];
			
			for (int i = args.length-1; i >= 0; i--) {
				args[i] = split[i+1];
			}
		}
	}
	public Event(String eventString, String[] args) {
		event = eventString;
		this.args = args;
	}
	
	
	public String getEvent() {
		return event;
	}
	public String getArgument(int i) {
		return args[i];
	}
	public String[] getArguments() {
		return args;
	}
	
	public Object getContents() {
		return null;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder(event);
		builder.append(":(");
		
		for (String str : args) {
			builder.append(str);
			builder.append(", ");
		}
		builder.delete(builder.length()-2, builder.length());
		
		if (args.length > 0)
			builder.append(")");
		
		return builder.toString();
	}
}
