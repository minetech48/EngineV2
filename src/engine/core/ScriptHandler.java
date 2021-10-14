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
	public static LuaValue execute(File file) {
		Logger.log("Executing Lua Script: " + file.getPath());
		
		return execute(globals.loadfile(file.getPath()));
	}
	public static LuaValue execute(String script) {
		Logger.log("Executing Lua Script: " + script);
		
		return execute(globals.load(script));
	}
	public static LuaValue execute(LuaValue script) {
		return script.call();
	}
}
