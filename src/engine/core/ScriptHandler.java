 package engine.core;

import java.io.File;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class ScriptHandler {
	
	public static Globals globals;
	
	//initialization
	static {
		globals = JsePlatform.standardGlobals();
	}
	
	//methods
	public static void execute(File file) {
		Logger.log("Executing Lua Script: " + file.getPath());
		
		execute(globals.loadfile(file.getPath()));
	}
	public static void execute(String script) {
		Logger.log("Executing Lua Script: " + script);
		
		execute(globals.load(script));
	}
	public static void execute(LuaValue script) {
		script.call();
	}
}
