package engine.updater;

import engine.util.FileIO;

public class Updater {
	
	
	public static boolean checkInstall() {
		if (FileIO.getResourcesDirectory().exists())
			return true;

		return false;
	}
	
	public static void install(String programName, String version) {
		install(programName, version, "server.fuqua.org");
	}
	public static void install(String programName, String version, String ip) {
		
	}
	
	public static void update(String programName, String currentVersion) {
		update(programName, currentVersion, "server.fuqua.org");
	}
	public static void update(String programName, String currentVersion, String ip) {
		
	}
}
