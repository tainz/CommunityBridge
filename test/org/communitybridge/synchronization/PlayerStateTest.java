package org.communitybridge.synchronization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.communitybridge.main.CommunityBridge;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.Environment;
import org.communitybridge.main.WebApplication;
import org.communitybridge.permissionhandlers.PermissionHandler;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(YamlConfiguration.class)
public class PlayerStateTest
{
	private static final String PLAYER_NAME = RandomStringUtils.randomAlphabetic(9);
	private static final String PRIMARY_GROUP_NAME = RandomStringUtils.randomAlphabetic(7);
	private static final String USER_ID = RandomStringUtils.randomNumeric(3);
	private static final String PRIMARY_GROUP_ID = RandomStringUtils.randomNumeric(2);
	private static final List<String> GROUP_NAMES = new ArrayList<String>(Arrays.asList(new String[] {RandomStringUtils.randomAlphabetic(7), RandomStringUtils.randomAlphabetic(7), RandomStringUtils.randomAlphabetic(7)}));
	private static final List<String> GROUP_IDS = new ArrayList<String>(Arrays.asList(new String[] {RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2)}));

	private static final UUID UUID = new UUID(RandomUtils.nextLong(), RandomUtils.nextLong());

	private Environment environment = new Environment();
	private Economy economy = mock(Economy.class);
	private PermissionHandler permissionHandler = mock(PermissionHandler.class);
	private Configuration configuration = mock(Configuration.class);
  private CommunityBridge plugin = mock(CommunityBridge.class);
	private Player player = mock(Player.class);
	private WebApplication webApplication = mock(WebApplication.class);

	private YamlConfiguration playerData = mock(YamlConfiguration.class);
	private File playerFile = mock(File.class);
	private File oldPlayerFile = mock(File.class);

	private PlayerState state;

	@Before
	public void beforeEach() {
		environment.setConfiguration(configuration);
		environment.setEconomy(economy);
		environment.setPermissionHandler(permissionHandler);
		environment.setPlugin(plugin);
		environment.setWebApplication(webApplication);
		configuration.simpleSynchronizationGroupsTreatedAsPrimary = new ArrayList<String>();
		configuration.simpleSynchronizationGroupsTreatedAsPrimary.add(PRIMARY_GROUP_NAME);
		configuration.groupSynchronizationActive = true;
		configuration.webappPrimaryGroupEnabled = true;
		configuration.webappSecondaryGroupEnabled = true;
		when(player.getUniqueId()).thenReturn(UUID);
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(webApplication.getUserPrimaryGroupID(USER_ID)).thenReturn(PRIMARY_GROUP_ID);
		when(webApplication.getUserSecondaryGroupIDs(USER_ID)).thenReturn(GROUP_IDS);
		when(permissionHandler.supportsPrimaryGroups()).thenReturn(true);
		when(permissionHandler.getPrimaryGroup(player)).thenReturn(PRIMARY_GROUP_NAME);
		when(permissionHandler.getGroups(player)).thenReturn(GROUP_NAMES);
		state = new PlayerState(environment, player, USER_ID, playerData, playerFile, oldPlayerFile);
	}

	@Test
	public void generateSetsPrimaryGroupId()
	{
		state.generate();

		assertEquals(PRIMARY_GROUP_ID, state.getWebappPrimaryGroupID());
	}

	@Test
	public void generateWhenGroupSynchronizationInactiveDoesNotSetPrimaryGroupId()
	{
		configuration.groupSynchronizationActive = false;
		state.generate();

		assertEquals("", state.getWebappPrimaryGroupID());
	}

	@Test
	public void generateWhenPrimaryGroupInactiveDoesNotSetPrimaryGroupId()
	{
		configuration.groupSynchronizationActive = true;
		configuration.webappPrimaryGroupEnabled = false;
		state.generate();

		assertEquals("", state.getWebappPrimaryGroupID());
	}

	@Test
	public void generateSetsGroupIds()
	{
		state.generate();
		for (String id : GROUP_IDS)
		{
			assertTrue(id + "missing", state.getWebappGroupIDs().contains(id));
		}
	}

	@Test
	public void generateWhenGroupSynchronizationInactiveDoesNotSetGroupIds()
	{
		configuration.groupSynchronizationActive = false;
		state.generate();

		assertTrue(state.getWebappGroupIDs().isEmpty());
	}

	@Test
	public void generateWhenSecondaryGroupInactiveDoesNotSetGroupIds()
	{
		configuration.groupSynchronizationActive = true;
		configuration.webappSecondaryGroupEnabled = false;
		state.generate();

		assertTrue(state.getWebappGroupIDs().isEmpty());
	}

	@Test
	public void generateSetsPrimaryGroupName()
	{
		state.generate();
		assertEquals(PRIMARY_GROUP_NAME, state.getPermissionsSystemPrimaryGroupName());
	}

	@Test
	public void generateWhenPrimaryGroupNotSupportedSetsPrimaryGroupName()
	{
		when(permissionHandler.supportsPrimaryGroups()).thenReturn(false);
		GROUP_NAMES.add(PRIMARY_GROUP_NAME);
		state.generate();
		assertEquals(PRIMARY_GROUP_NAME, state.getPermissionsSystemPrimaryGroupName());
	}

	@Test
	public void generateWhenPrimaryGroupNotSupportedSetsBlankOnNotFound()
	{
		when(permissionHandler.supportsPrimaryGroups()).thenReturn(false);
		state.generate();
		assertEquals("", state.getPermissionsSystemPrimaryGroupName());
	}

	@Test
	public void generateWhenGroupSynchronizationInactiveDoesNotSetPrimaryGroupName()
	{
		configuration.groupSynchronizationActive = false;
		state.generate();

		assertEquals("", state.getPermissionsSystemPrimaryGroupName());
	}

	@Test
	public void generateWhenPrimaryGroupInactiveDoesNotSetPrimaryGroupName()
	{
		configuration.groupSynchronizationActive = true;
		configuration.webappPrimaryGroupEnabled = false;
		state.generate();

		assertEquals("", state.getPermissionsSystemPrimaryGroupName());
	}

	@Test
	public void generateSetsGroupNames()
	{
		state.generate();

		for (String group : GROUP_NAMES)
		{
			assertTrue(group + "missing", state.getPermissionsSystemGroupNames().contains(group));
		}
	}

	@Test
	public void generateWhenGroupSynchronizationInactiveDoesNotSetGroupNames()
	{
		configuration.groupSynchronizationActive = false;
		state.generate();

		assertTrue(state.getPermissionsSystemGroupNames().isEmpty());
	}

	@Test
	public void generateWhenSecondaryGroupInactiveDoesNotSetGroupNames()
	{
		configuration.groupSynchronizationActive = true;
		configuration.webappSecondaryGroupEnabled = false;
		state.generate();

		assertTrue(state.getPermissionsSystemGroupNames().isEmpty());
	}

	@Test
	public void generateSetsMinecraftWallet()
	{
		double wallet = RandomUtils.nextDouble() + 1;
		configuration.economyEnabled = true;
		configuration.walletEnabled = true;

		when(economy.getBalance(player)).thenReturn(wallet);

		state.generate();

		assertEquals(wallet, state.getMinecraftWallet(), 0);
	}

	@Test
	public void generateSetsWebApplicationWallet()
	{
		double wallet = RandomUtils.nextDouble() + 1;
		configuration.economyEnabled = true;
		configuration.walletEnabled = true;
		when(webApplication.getBalance(USER_ID)).thenReturn(wallet);
		state.generate();

		assertEquals(wallet, state.getWebApplicationWallet(), 0);
	}

	@Test
	public void copyNeverReturnsNull()
	{
		state.generate();
		PlayerState copy = state.copy();
		assertNotNull(copy);
	}

	@Test
	public void copyCopiesPrimaryGroupId()
	{
		state.generate();
		PlayerState copy = state.copy();
		assertEquals(state.getWebappPrimaryGroupID(), copy.getWebappPrimaryGroupID());
	}

	@Test
	public void copyCopiesGroupIds()
	{
		state.generate();
		PlayerState copy = state.copy();
		assertEquals(state.getWebappGroupIDs(), copy.getWebappGroupIDs());
	}

	@Test
	public void copyCopiesPrimaryGroupName()
	{
		state.generate();
		PlayerState copy = state.copy();

		assertEquals(state.getPermissionsSystemPrimaryGroupName(), copy.getPermissionsSystemPrimaryGroupName());
	}

	@Test
	public void copyCopiesGroupNames()
	{
		state.generate();
		PlayerState copy = state.copy();

		assertEquals(state.getPermissionsSystemGroupNames(), copy.getPermissionsSystemGroupNames());
	}

	@Test
	public void copyCopiesMinecraftMoney()
	{
		double money = RandomUtils.nextDouble() + 1;
		configuration.economyEnabled = true;

		when(economy.getBalance(player)).thenReturn(money);
		state.generate();
		PlayerState copy = state.copy();

		assertEquals(state.getMinecraftWallet(), copy.getMinecraftWallet(), 0);
	}

	@Test
	public void copyCopiesNewFile()
	{
		state.generate();
		PlayerState copy = state.copy();

		assertEquals(state.isIsNewFile(), copy.isIsNewFile());
	}

	@Test
	public void saveSavesData() throws IOException
	{
		double money = RandomUtils.nextDouble() + 1;
		configuration.economyEnabled = true;
		configuration.walletEnabled = true;

		when(economy.getBalance(player)).thenReturn(money);
		doNothing().when(playerData).set(anyString(), anyString());
		doNothing().when(playerData).save(any(File.class));

		state.generate();
		state.save();

		verify(playerData).set("last-known-name", PLAYER_NAME);
		verify(playerData).set("minecraft-money", money);
		verify(playerData).set("permissions-system.primary-group-name", PRIMARY_GROUP_NAME);
		verify(playerData).set("permissions-system.group-names", GROUP_NAMES);
		verify(playerData).set("webapp.primary-group-id", PRIMARY_GROUP_ID);
		verify(playerData).set("webapp.group-ids", GROUP_IDS);
		verify(playerData).save(any(File.class));
	}

	@Test
	public void loadHandlesNewFile() throws IOException
	{
		when(playerFile.exists()).thenReturn(false);
		when(oldPlayerFile.exists()).thenReturn(false);
		state.load();
		assertEquals(true, state.isIsNewFile());
		assertEquals("", state.getWebappPrimaryGroupID());
		assertTrue("Group name list should be empty", state.getPermissionsSystemGroupNames().isEmpty());
		assertTrue("Group id list should be empty", state.getWebappGroupIDs().isEmpty());
		assertEquals("", state.getPermissionsSystemPrimaryGroupName());
	}

	@Test
	public void loadLoadsFromOldPlayerFile() throws IOException
	{
		when(playerFile.exists()).thenReturn(false);
		when(oldPlayerFile.exists()).thenReturn(true);
		PowerMockito.mockStatic(YamlConfiguration.class);
		when(YamlConfiguration.loadConfiguration(oldPlayerFile)).thenReturn(playerData);
		state.load();
		PowerMockito.verifyStatic();
		YamlConfiguration.loadConfiguration(oldPlayerFile);
	}

	@Test
	public void loadLoadsFromNewPlayerFile() throws IOException
	{
		when(playerFile.exists()).thenReturn(true);
		when(oldPlayerFile.exists()).thenReturn(true);
		PowerMockito.mockStatic(YamlConfiguration.class);
		when(YamlConfiguration.loadConfiguration(playerFile)).thenReturn(playerData);
		state.load();
		PowerMockito.verifyStatic();
		YamlConfiguration.loadConfiguration(playerFile);
	}

	@Test
	public void loadLoadsData() throws IOException
	{
		double money = RandomUtils.nextDouble() + 1;

		when(playerFile.exists()).thenReturn(true);
		when(oldPlayerFile.exists()).thenReturn(true);
		PowerMockito.mockStatic(YamlConfiguration.class);
		when(YamlConfiguration.loadConfiguration(playerFile)).thenReturn(playerData);
		when(playerData.getDouble("minecraft-money", 0)).thenReturn(money);
		when(playerData.getStringList("permissions-system.group-names")).thenReturn(GROUP_NAMES);
		when(playerData.getString("permissions-system.primary-group-name", "")).thenReturn(PRIMARY_GROUP_NAME);
		when(playerData.getStringList("webapp.group-ids")).thenReturn(GROUP_IDS);
		when(playerData.getString("webapp.primary-group-id", "")).thenReturn(PRIMARY_GROUP_ID);
		state.load();

		assertEquals(false, state.isIsNewFile());
		assertEquals(money, state.getMinecraftWallet(), 0);
		assertEquals(PRIMARY_GROUP_ID, state.getWebappPrimaryGroupID());
		assertEquals(PRIMARY_GROUP_NAME, state.getPermissionsSystemPrimaryGroupName());

		for (String group : GROUP_NAMES)
		{
			assertTrue(group + " missing.", state.getPermissionsSystemGroupNames().contains(group));
		}

		for (String id : GROUP_IDS)
		{
			assertTrue(id + " missing.", state.getWebappGroupIDs().contains(id));
		}
	}
}