package org.communitybridge.main;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class BukkitWrapper
{
	public OfflinePlayer getPlayer(UUID uuid)
	{
		return Bukkit.getPlayer(uuid);
	}
}
