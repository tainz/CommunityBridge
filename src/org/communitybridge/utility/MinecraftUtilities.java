package org.communitybridge.utility;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public final class MinecraftUtilities
{
	private MinecraftUtilities() {}

	public static String getPluginVersion(String pluginName)
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
		if (plugin == null)
		{
			return null;
		}

    return plugin.getDescription().getVersion();
	}

	public static String getBukkitVersion()
	{
    return Bukkit.getBukkitVersion();
	}

	public static void startTask(Plugin plugin, Runnable runnable)
	{
		Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
	}

	public static void startTaskTimer(Plugin plugin, long every, Runnable runnable)
	{
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, every, every);
	}
}
