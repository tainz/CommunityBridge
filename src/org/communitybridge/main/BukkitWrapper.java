package org.communitybridge.main;

import java.util.UUID;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class BukkitWrapper
{
	public OfflinePlayer getOfflinePlayer(UUID uuid)
	{
		return Bukkit.getOfflinePlayer(uuid);
	}

	@SuppressWarnings("deprecation")
	public OfflinePlayer getOfflinePlayer(String name)
	{
		return Bukkit.getOfflinePlayer(name);
	}

	public Player getPlayer(UUID uuid)
	{
		return Bukkit.getPlayer(uuid);
	}

	public Server getServer()
	{
		return Bukkit.getServer();
	}

	public BanList getBanList(BanList.Type type)
	{
		return Bukkit.getBanList(type);
	}

	public PluginManager getPluginManager()
	{
		return Bukkit.getPluginManager();
	}

	@SuppressWarnings("deprecation")
	public Player[] getOnlinePlayers()
	{
		return Bukkit.getOnlinePlayers();
	}
}
