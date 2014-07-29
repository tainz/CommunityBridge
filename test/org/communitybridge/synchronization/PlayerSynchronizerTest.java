package org.communitybridge.synchronization;

import java.util.ArrayList;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.entity.Player;
import org.communitybridge.linker.UserPlayerLinker;
import org.communitybridge.main.BukkitWrapper;
import org.communitybridge.main.Configuration;
import org.communitybridge.utility.Log;
import org.communitybridge.main.Environment;
import org.communitybridge.main.WebApplication;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PlayerSynchronizerTest
{
	private Environment environment = new Environment();
	private BukkitWrapper bukkit = mock(BukkitWrapper.class);
	private Configuration configuration = mock(Configuration.class);
	private Log log = mock(Log.class);
	private UserPlayerLinker userPlayerLinker = mock(UserPlayerLinker.class);
	private WebApplication webApplication = mock(WebApplication.class);

	private Player player = mock(Player.class);
	private Player[] players = new Player[2];
	private static final String USER_ID = RandomStringUtils.randomAlphabetic(8);

	@Mock
	private ArrayList<Player> playerLocks;

	@InjectMocks
	private PlayerSynchronizer synchronizer = new PlayerSynchronizer(environment);

	@Before
	public void beforeEach()
	{
		environment.setBukkit(bukkit);
		environment.setConfiguration(configuration);
		environment.setLog(log);
		environment.setUserPlayerLinker(userPlayerLinker);
		environment.setWebApplication(webApplication);

		configuration.groupSynchronizationActive = true;
		configuration.statisticsEnabled = true;
		configuration.useAchievements = true;

		players[0] = player;
		when(bukkit.getOnlinePlayers()).thenReturn(players);
		when(userPlayerLinker.getUserID(player)).thenReturn(USER_ID);
	}

	@Test
	public void synchronizeShouldLogStart()
	{
		synchronizer.synchronize();
		verify(log).finest("Running player synchronization.");
	}

	@Test
	public void synchronizeShouldLogEnd()
	{
		synchronizer.synchronize();
		verify(log).finest("Player synchronization complete.");
	}

	@Test
	public void synchronizeShouldSynchronizeGroups()
	{
		synchronizer.synchronize();
		verify(webApplication).synchronizeGroups(player, USER_ID);
	}

	@Test
	public void synchronizeShouldSynchronizeForMultiplePlayers()
	{
		Player player2 = mock(Player.class);
		players[1] = player2;
		when(userPlayerLinker.getUserID(player2)).thenReturn(USER_ID);

		synchronizer.synchronize();

		verify(webApplication).synchronizeGroups(player, USER_ID);
		verify(webApplication).synchronizeGroups(player2, USER_ID);
	}

	@Test
	public void synchronizeWhenGroupSynchronizationInactiveShouldNotSynchronizeGroups()
	{
		configuration.groupSynchronizationActive = false;
		synchronizer.synchronize();
		verify(webApplication, never()).synchronizeGroups(player, USER_ID);
	}

	@Test
	public void synchronizeWhenUserIDIsNullShouldNotSynchronize()
	{
		configuration.groupSynchronizationActive = true;
		when(userPlayerLinker.getUserID(player)).thenReturn(null);
		synchronizer.synchronize();
		verify(webApplication, never()).synchronizeGroups(player, null);
	}

	@Test
	public void synchronizeShouldOnlySynchronizePlayerOnce()
	{
		when(playerLocks.contains(player)).thenReturn(true);

		synchronizer.synchronize();

		verify(webApplication, never()).synchronizeGroups(player, USER_ID);
	}

	@Test
	public void synchronizeShouldAddPlayerToLock()
	{
		synchronizer.synchronize();
		InOrder inOrder = inOrder(playerLocks, webApplication);
		inOrder.verify(playerLocks).add(player);
		inOrder.verify(webApplication).synchronizeGroups(player, USER_ID);
	}

	@Test
	public void synchronizeShouldRemovePlayerFromLock()
	{
		synchronizer.synchronize();

		InOrder inOrder = inOrder(webApplication, playerLocks);
		inOrder.verify(playerLocks).add(player);
		inOrder.verify(webApplication).synchronizeGroups(player, USER_ID);
		inOrder.verify(playerLocks).remove(player);
	}


	@Test
	public void synchronizeShouldUpdateStatistics()
	{
		synchronizer.synchronize();
		verify(webApplication).updateStatistics(player, true);
	}

	@Test
	public void synchronizeWhenStatisticsInactiveShouldNotUpdateStatistics()
	{
		configuration.statisticsEnabled = false;
		synchronizer.synchronize();
		verify(webApplication, never()).updateStatistics(player, true);
	}

	@Test
	public void synchronizeShouldRewardAchievements()
	{
		synchronizer.synchronize();
		verify(webApplication).rewardAchievements(player);
	}

	@Test
	public void synchronizeWhenAchievementsInactiveShouldNotRewardAchievements()
	{
		configuration.useAchievements = false;
		synchronizer.synchronize();
		verify(webApplication, never()).rewardAchievements(player);
	}
}