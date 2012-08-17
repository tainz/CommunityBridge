package net.netmanagers.api;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

public class Configer {
	private File confLoc;
	private FileConfiguration config;
	
	public Configer(String confName, PluginDescriptionFile pdf, FileConfiguration confige) {
		confLoc = new File("plugins" + File.separator + pdf.getName() + File.separator + confName);
		config = confige;
	}
	
	public void write(String s, Object obj) {
		config.set(s, obj);
	}
	
	public Object get(String s) {
		return config.get(s);
	}
	
	public File getLoc() {
		return confLoc;
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
}