package org.communitybridge.synchronization;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.communitybridge.main.Environment;

public class PlayerSynchronizationDispatcher
{
	private static final String SYNCHRONIZATION_EXCEPTION = "Exception occurred during synchronization: ";
	private final Boolean synchronizationLock = true;
	private List<Player> playerLocks = new ArrayList<Player>();
	private PlayerState previous = new PlayerState();
	private PlayerState current = new PlayerState();
	private PlayerState result;
	private MoneySynchronizer moneySynchronizer = new MoneySynchronizer(null);

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
		if (!playerLocks.contains(player))
		{
			String userID = environment.getUserPlayerLinker().getUserID(player);
			if (userID == null)
			{
				return;
			}
			synchronized (synchronizationLock) { playerLocks.add(player);}
			playerStateBaseSynchronization(environment, player, userID);
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

	private void playerStateBaseSynchronization(Environment environment, Player player, String userID)
	{
		try
		{
			PlayerFileFetcher fetcher = new PlayerFileFetcher();
			File playerFile = fetcher.getPlayerFile(environment.getPlugin().getDataFolder(), player, true);

			previous.load(playerFile);
			current.generate(environment, player, userID);
			result = current.copy();

			if (environment.getConfiguration().groupSynchronizationActive)
			{
				result = environment.getWebApplication().synchronizeGroups(player, userID, previous, current, result);
			}
			if (moneySynchronizer.isActive(environment))
			{
				result = moneySynchronizer.synchronize(environment, player, userID, previous, current, result);
			}
			if (environment.getConfiguration().groupSynchronizationActive || moneySynchronizer.isActive(environment))
			{
				playerFile = fetcher.getPlayerFile(environment.getPlugin().getDataFolder(), player, false);
				result.save(player, playerFile, environment.getLog());
			}
		}
		catch (InstantiationException exception)
		{
			environment.getLog().severe(SYNCHRONIZATION_EXCEPTION + exception.getMessage());
		}
		catch (IllegalAccessException exception)
		{
			environment.getLog().severe(SYNCHRONIZATION_EXCEPTION + exception.getMessage());
		}
		catch (MalformedURLException exception)
		{
			environment.getLog().severe(SYNCHRONIZATION_EXCEPTION + exception.getMessage());
		}
		catch (SQLException exception)
		{
			environment.getLog().severe(SYNCHRONIZATION_EXCEPTION + exception.getMessage());
		}
	}
}
