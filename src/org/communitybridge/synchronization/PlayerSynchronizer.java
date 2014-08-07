package org.communitybridge.synchronization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.communitybridge.main.Environment;

public class PlayerSynchronizer extends Synchronizer
{
	private final Boolean synchronizationLock = true;
	private List<Player> playerLocks = new ArrayList<Player>();
	private PlayerState previous = new PlayerState();
	private PlayerState current = new PlayerState();
	private PlayerState result;

	public PlayerSynchronizer(Environment environment)
	{
		super(environment);
	}

	public void synchronize(Environment environment)
	{
		environment.getLog().finest("Running player synchronization.");
		for (Player player : environment.getBukkit().getOnlinePlayers())
		{
			synchronizePlayer(environment, player, true);
		}
		environment.getLog().finest("Player synchronization complete.");
	}

	public void synchronizePlayer(Environment environment, Player player, boolean online)
	{
		PlayerFileFetcher fetcher = new PlayerFileFetcher();

		if (!playerLocks.contains(player))
		{
			synchronized (synchronizationLock) { playerLocks.add(player);}
			String userID = environment.getUserPlayerLinker().getUserID(player);
			if (userID == null)
			{
				return;
			}

			File playerFile = fetcher.getPlayerFile(environment.getPlugin().getDataFolder(), player, true);

			previous.load(playerFile);
			current.generate(environment, player, userID);
			result = current.copy();

			if (environment.getConfiguration().groupSynchronizationActive)
			{
				result = environment.getWebApplication().synchronizeGroups(player, userID, previous, current, result);
			}
			if (environment.getConfiguration().walletEnabled)
			{
				result = synchronizeWallet(player, userID, previous, result);
			}
			if (environment.getConfiguration().groupSynchronizationActive || environment.getConfiguration().walletEnabled)
			{
				playerFile = fetcher.getPlayerFile(environment.getPlugin().getDataFolder(), player, false);
				result.save(player, playerFile, environment.getLog());
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

	private PlayerState synchronizeWallet(Player player, String userID, PlayerState previous, PlayerState result)
	{
//		double change = result.getMinecraftWallet() - previous.getMinecraftWallet();
//		if (change != 0)
//		{
//			result.setWebApplicationWallet(result.getWebApplicationWallet() + change);
//			// update webapplication
//		}
//
		double change = result.getWebApplicationWallet() - previous.getWebApplicationWallet();
		if (change != 0)
		{
//			result.setMinecraftWallet(result.getMinecraftWallet() + change);
			environment.getEconomy().depositPlayer(player, change);
		}
		return result;
	}
}
