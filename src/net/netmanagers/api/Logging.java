package net.netmanagers.api;

import java.util.logging.Logger;
import org.bukkit.plugin.PluginDescriptionFile;

public class Logging {
	private Logger logge;
	private PluginDescriptionFile pdf;
	
	public Logging(Logger logg, PluginDescriptionFile pd) {
		logge = logg;
		pdf = pd;
	}
	
	public void info(String text) {
		logge.info("[" + pdf.getName() + "] " + text);
	}
	
	public void warning(String text) {
		logge.warning("[" + pdf.getName() + "] " + text);
	}
	
	public void severe(String text) {
		logge.severe("[" + pdf.getName() + "] " + text);
	}
}