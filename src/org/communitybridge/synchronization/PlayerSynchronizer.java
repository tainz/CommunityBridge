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

	public void synchronizePlayer(Player player, boolean online)
	{
		if (!playerLocks.contains(player))
		{
			synchronized (synchronizationLock) { playerLocks.add(player);}
			String userID = environment.getUserPlayerLinker().getUserID(player);
			if (userID == null)
			{
				return;
			}
			PlayerState previous = new PlayerState(environment, player, userID);
			PlayerState current = new PlayerState(environment, player, userID);
			previous.load();
			current.generate();
			PlayerState result = current.copy();

			if (environment.getConfiguration().groupSynchronizationActive)
			{
				result = environment.getWebApplication().synchronizeGroups(player, userID, previous, current, result);
			}
			if (environment.getConfiguration().walletEnabled)
			{
				result = synchronizeWallet(player, userID, previous, current, result);
			}
			if (environment.getConfiguration().groupSynchronizationActive || environment.getConfiguration().walletEnabled)
			{
				result.save();
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

	private PlayerState synchronizeWallet(Player player, String userID, PlayerState previous, PlayerState current, PlayerState result)
	{
		return result;
	}
}
