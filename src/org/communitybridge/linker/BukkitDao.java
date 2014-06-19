package org.communitybridge.linker;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class BukkitDao
{
	OfflinePlayer getPlayer(UUID uuid)
	{
		return Bukkit.getPlayer(uuid);
	}
}
