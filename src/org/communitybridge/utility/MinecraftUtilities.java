package org.communitybridge.utility;

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
	
	/**
	 * Start a Minecraft asynchronous task using the correct method based on version.
	 * 
	 * @param plugin The plugin object to pass to the task
	 * @param runnable The runnable object to be executed.
	 */
	public static void startTask(Plugin plugin, Runnable runnable)
	{
		if (StringUtilities.compareVersion(Bukkit.getBukkitVersion(), "1.4.6") > 0)
		{
			Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);		
		}
		else
		{
			Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, runnable);
		}
	}

	/**
	 * Start a Minecraft asynchronous repeatable task using the correct method based on version.
	 * 
	 * @param plugin The plugin object to pass to the task
	 * @param every How often to execute the task in game ticks.
	 * @param runnable The runnable object to be executed.
	 */
	public static void startTaskTimer(Plugin plugin, long every, Runnable runnable)
	{
		if (StringUtilities.compareVersion(Bukkit.getBukkitVersion(), "1.4.6") > 0)
		{
			Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, every, every);		
		}
		else
		{
			Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, runnable, every, every);
		}
	}
}
