package org.ruhlendavis.mc.utility;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Miscellaneous Minecraft related methods.
 *
 * By design, this class cannot be instantiated nor can it be sub-classed.
 *
 * @author Feaelin (Iain E. Davis) <iain@ruhlendavis.org>
 */
public final class MinecraftUtilities
{
	/**
	 *  Private constructor prevents instantiation of the class.
	 */
	private MinecraftUtilities() {}
	/**
	 * Given a pluginName string returns that plugin's version string.
	 * @param String Name of the plugin
	 * @return String containing the plugin's version.
	 */
	public static String getPluginVersion(String pluginName)
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
		if (plugin == null)
		{
			return null;
		}

    return plugin.getDescription().getVersion();
	}
}
