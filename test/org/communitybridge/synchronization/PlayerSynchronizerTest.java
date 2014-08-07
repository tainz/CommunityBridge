package org.communitybridge.synchronization;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.entity.Player;
import org.communitybridge.linker.UserPlayerLinker;
import org.communitybridge.main.BukkitWrapper;
import org.communitybridge.main.CommunityBridge;
import org.communitybridge.main.Configuration;
import org.communitybridge.utility.Log;
import org.communitybridge.main.Environment;
import org.communitybridge.main.WebApplication;
import org.communitybridge.permissionhandlers.PermissionHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.MockGateway;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CommunityBridge.class)
public class PlayerSynchronizerTest
{
	private Environment environment = new Environment();
	private BukkitWrapper bukkit = mock(BukkitWrapper.class);
	private CommunityBridge plugin = PowerMockito.mock(CommunityBridge.class);
	private Configuration configuration = mock(Configuration.class);
	private Economy economy = mock(Economy.class);
	private Log log = mock(Log.class);
	private PermissionHandler permissionHandler = mock(PermissionHandler.class);
	private UserPlayerLinker userPlayerLinker = mock(UserPlayerLinker.class);
	private WebApplication webApplication = mock(WebApplication.class);
	private PlayerState result = mock(PlayerState.class);

	private Player player = mock(Player.class);
	private Player[] players = new Player[2];
	private static final String USER_ID = RandomStringUtils.randomAlphabetic(8);
	private static final UUID UUID = new UUID(RandomUtils.nextLong(), RandomUtils.nextLong());
	private File dataFolder = new File("/");

	@Mock private PlayerState previous;
	@Mock private PlayerState current;
	@Mock private File dataFile;
	@Mock private ArrayList<Player> playerLocks;

	@InjectMocks
	private PlayerSynchronizer synchronizer = new PlayerSynchronizer(environment);

	@Before
	public void beforeEach() throws Exception
	{
		MockGateway.MOCK_STANDARD_METHODS = false;
		environment.setBukkit(bukkit);
		environment.setConfiguration(configuration);
		environment.setEconomy(economy);
		environment.setLog(log);
		environment.setPermissionHandler(permissionHandler);
		environment.setPlugin(plugin);
		environment.setUserPlayerLinker(userPlayerLinker);
		environment.setWebApplication(webApplication);

		configuration.groupSynchronizationActive = true;
		configuration.simpleSynchronizationGroupsTreatedAsPrimary = new ArrayList<String>();
		configuration.statisticsEnabled = true;
		configuration.useAchievements = true;

		players[0] = player;
		when(bukkit.getOnlinePlayers()).thenReturn(players);
		PowerMockito.when(plugin.getDataFolder()).thenReturn(dataFolder);
		when(dataFile.exists()).thenReturn(true);
		when(userPlayerLinker.getUserID(player)).thenReturn(USER_ID);
		when(player.getUniqueId()).thenReturn(UUID);
		when(webApplication.synchronizeGroups(any(Player.class), anyString(), any(PlayerState.class), any(PlayerState.class), any(PlayerState.class))).thenReturn(result);
	}

	@Test
	public void synchronizeShouldLogStart()
	{
		synchronizer.synchronize(environment);
		verify(log).finest("Running player synchronization.");
	}

	@Test
	public void synchronizeShouldLogEnd()
	{
		synchronizer.synchronize(environment);
		verify(log).finest("Player synchronization complete.");
	}

	@Test
	public void synchronizeShouldSynchronizeGroups()
	{
		synchronizer.synchronize(environment);
		verify(webApplication).synchronizeGroups(eq(player), eq(USER_ID), any(PlayerState.class), any(PlayerState.class), any(PlayerState.class));
	}

	@Test
	public void synchronizeShouldSynchronizeForMultiplePlayers()
	{
		Player player2 = mock(Player.class);
		players[1] = player2;
		when(userPlayerLinker.getUserID(player2)).thenReturn(USER_ID);
		when(player2.getUniqueId()).thenReturn(UUID);
		synchronizer.synchronize(environment);

		verify(webApplication).synchronizeGroups(eq(player), eq(USER_ID), any(PlayerState.class), any(PlayerState.class), any(PlayerState.class));
		verify(webApplication).synchronizeGroups(eq(player2), eq(USER_ID), any(PlayerState.class), any(PlayerState.class), any(PlayerState.class));
	}

	@Test
	public void synchronizeWhenGroupSynchronizationInactiveShouldNotSynchronizeGroups()
	{
		configuration.groupSynchronizationActive = false;
		synchronizer.synchronize(environment);
		verify(webApplication, never()).synchronizeGroups(any(Player.class), anyString(), any(PlayerState.class), any(PlayerState.class), any(PlayerState.class));
	}

	@Test
	public void synchronizeWhenUserIDIsNullShouldNotSynchronize()
	{
		configuration.groupSynchronizationActive = true;
		when(userPlayerLinker.getUserID(player)).thenReturn(null);
		synchronizer.synchronize(environment);
		verify(webApplication, never()).synchronizeGroups(any(Player.class), isNull(String.class), any(PlayerState.class), any(PlayerState.class), any(PlayerState.class));
	}

	@Test
	public void synchronizeShouldOnlySynchronizePlayerOnce()
	{
		when(playerLocks.contains(player)).thenReturn(true);

		synchronizer.synchronize(environment);

		verify(webApplication, never()).synchronizeGroups(eq(player), anyString(), any(PlayerState.class), any(PlayerState.class), any(PlayerState.class));
	}

	@Test
	public void synchronizeShouldAddPlayerToLock()
	{
		synchronizer.synchronize(environment);
		InOrder inOrder = inOrder(playerLocks, webApplication);
		inOrder.verify(playerLocks).add(player);
		inOrder.verify(webApplication).synchronizeGroups(eq(player), eq(USER_ID), any(PlayerState.class), any(PlayerState.class), any(PlayerState.class));
	}

	@Test
	public void synchronizeShouldRemovePlayerFromLock()
	{
		synchronizer.synchronize(environment);

		InOrder inOrder = inOrder(webApplication, playerLocks);
		inOrder.verify(webApplication).synchronizeGroups(eq(player), eq(USER_ID), any(PlayerState.class), any(PlayerState.class), any(PlayerState.class));
		inOrder.verify(playerLocks).remove(player);
	}


	@Test
	public void synchronizeShouldUpdateStatistics()
	{
		synchronizer.synchronize(environment);
		verify(webApplication).updateStatistics(player, true);
	}

	@Test
	public void synchronizeWhenStatisticsInactiveShouldNotUpdateStatistics()
	{
		configuration.statisticsEnabled = false;
		synchronizer.synchronize(environment);
		verify(webApplication, never()).updateStatistics(player, true);
	}

	@Test
	public void synchronizeShouldRewardAchievements()
	{
		synchronizer.synchronize(environment);
		verify(webApplication).rewardAchievements(player);
	}

	@Test
	public void synchronizeWhenAchievementsInactiveShouldNotRewardAchievements()
	{
		configuration.useAchievements = false;
		synchronizer.synchronize(environment);
		verify(webApplication, never()).rewardAchievements(player);
	}

//	@Test
//	public void synchronizeShouldUpdateMinecraftWalletResult()
//	{
//		double webPrevious = RandomUtils.nextDouble();
//		double webCurrent = webPrevious * 2;
//		double mcPrevious = RandomUtils.nextDouble();
//		double mcCurrent = RandomUtils.nextDouble();
//		double expected = webCurrent - webPrevious;
//
//		when(webApplication.getBalance(USER_ID)).thenReturn(webCurrent);
//		when(economy.getBalance(player)).thenReturn(mcCurrent);
//
//		synchronizer.synchronize(environment);
//
//		verify(economy).depositPlayer(player, expected);
//	}
}