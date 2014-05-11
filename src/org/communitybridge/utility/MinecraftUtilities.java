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

	@SuppressWarnings("deprecation")
	public static void startTask(Plugin plugin, Runnable runnable)
	{
		if (StringUtilities.compareVersion(MinecraftUtilities.getBukkitVersion(), "1.4.6") > 0)
		{
			Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
		}
		else
		{
			Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, runnable);
		}
	}

	@SuppressWarnings("deprecation")
	public static void startTaskTimer(Plugin plugin, long every, Runnable runnable)
	{
		if (StringUtilities.compareVersion(MinecraftUtilities.getBukkitVersion(), "1.4.6") > 0)
		{
			Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, every, every);
		}
		else
		{
			Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, runnable, every, every);
		}
	}
}
