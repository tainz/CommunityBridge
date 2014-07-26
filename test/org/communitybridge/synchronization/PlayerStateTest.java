package org.communitybridge.synchronization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
import static org.mockito.Mockito.*;

public class PlayerStateTest
{
	private static final String PLAYER_NAME = RandomStringUtils.randomAlphabetic(9);
	private static final String PRIMARY_GROUP_NAME = RandomStringUtils.randomAlphabetic(7);
	private static final String USER_ID = RandomStringUtils.randomNumeric(3);
	private static final String PRIMARY_GROUP_ID = RandomStringUtils.randomNumeric(2);
	private static final List<String> GROUP_NAMES = new ArrayList<String>(Arrays.asList(new String[] {"group1", "group2", "group3"}));
	private static final List<String> GROUP_IDS = new ArrayList<String>(Arrays.asList(new String[] {"01", "02", "03"}));

	private static final UUID UUID = new UUID(RandomUtils.nextLong(), RandomUtils.nextLong());

	private Environment environment = new Environment();
	private PermissionHandler permissionHandler = mock(PermissionHandler.class);
	private Configuration configuration = mock(Configuration.class);
  private CommunityBridge plugin = mock(CommunityBridge.class);
	private Player player = mock(Player.class);
	private WebApplication webApplication = mock(WebApplication.class);

	private YamlConfiguration playerData = mock(YamlConfiguration.class);

	private PlayerState state;

	@Before
	public void beforeEach() {
		environment.setConfiguration(configuration);
		environment.setPermissionHandler(permissionHandler);
		environment.setPlugin(plugin);
		environment.setWebApplication(webApplication);
		configuration.simpleSynchronizationGroupsTreatedAsPrimary = new ArrayList<String>();
		when(player.getUniqueId()).thenReturn(UUID);
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(webApplication.getUserPrimaryGroupID(USER_ID)).thenReturn(PRIMARY_GROUP_ID);
		when(webApplication.getUserSecondaryGroupIDs(USER_ID)).thenReturn(GROUP_IDS);
		when(permissionHandler.supportsPrimaryGroups()).thenReturn(true);
		when(permissionHandler.getPrimaryGroup(player)).thenReturn(PRIMARY_GROUP_NAME);
		when(permissionHandler.getGroups(player)).thenReturn(GROUP_NAMES);
		state = new PlayerState(environment, player, USER_ID, playerData);
	}

	@Test
	public void generateSetsPrimaryGroupId()
	{
		state.generate();

		assertEquals(PRIMARY_GROUP_ID, state.getWebappPrimaryGroupID());
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
	public void generateSetsPrimaryGroupName()
	{
		state.generate();
		assertEquals(PRIMARY_GROUP_NAME, state.getPermissionsSystemPrimaryGroupName());

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
	public void copyCopiesNewFile()
	{
		state.generate();
		PlayerState copy = state.copy();

		assertEquals(state.isIsNewFile(), copy.isIsNewFile());
	}

	@Test
	public void saveSavesData() throws IOException
	{
		doNothing().when(playerData).set(anyString(), anyString());
		doNothing().when(playerData).save(any(File.class));
		state.generate();
		state.save();
		verify(playerData).set("last-known-name", PLAYER_NAME);
		verify(playerData).set("webapp.primary-group-id", PRIMARY_GROUP_ID);
		verify(playerData).set("webapp.group-ids", GROUP_IDS);
		verify(playerData).set("permissions-system.primary-group-name", PRIMARY_GROUP_NAME);
		verify(playerData).set("permissions-system.group-names", GROUP_NAMES);
		verify(playerData).save(any(File.class));
	}
}