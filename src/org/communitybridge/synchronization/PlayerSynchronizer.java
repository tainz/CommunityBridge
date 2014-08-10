package org.communitybridge.synchronization;

import org.bukkit.entity.Player;
import org.communitybridge.main.Environment;

public interface PlayerSynchronizer
{
	PlayerState synchronize(Environment environment, Player player, String userID, PlayerState previous, PlayerState current, PlayerState result);

	boolean isActive(Environment environment);
}
