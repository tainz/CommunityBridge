package org.communitybridge.synchronization;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.communitybridge.main.Environment;

public class PlayerSynchronizer extends Synchronizer
{
	private final Boolean synchronizationLock = true;
	private List<Player> playerLocks = new ArrayList<Player>();

	public PlayerSynchronizer(Environment environment)
	{
		super(environment);
	}

	public void synchronize()
	{
		environment.getLog().finest("Running player synchronization.");
		for (Player player : environment.getBukkit().getOnlinePlayers())
		{
			synchronizePlayer(player, true);
		}
		environment.getLog().finest("Player synchronization complete.");
	}

	private void synchronizePlayer(Player player, boolean online)
	{
		if (!playerLocks.contains(player))
		{
			synchronized (synchronizationLock) { playerLocks.add(player);}
			if (environment.getConfiguration().groupSynchronizationActive)
			{
				environment.getWebApplication().synchronizeGroups(player);
			}
			if (environment.getConfiguration().statisticsEnabled)
			{
				environment.getWebApplication().updateStatistics(player, online);
			}
			if (environment.getConfiguration().useAchievements)
			{
				environment.getWebApplication().rewardAchievements(player);
			}
			synchronized (synchronizationLock) { playerLocks.remove(player); }
		}
	}
}
