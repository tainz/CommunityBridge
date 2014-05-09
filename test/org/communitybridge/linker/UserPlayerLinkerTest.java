package org.communitybridge.linker;

import java.util.UUID;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.Environment;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserPlayerLinkerTest
{
	private static final String NAME_USER_ID = RandomStringUtils.randomNumeric(2);
	private static final String PLAYER_NAME = RandomStringUtils.randomAlphabetic(8);
	private static final String UUID_USER_ID = RandomStringUtils.randomNumeric(2);

	private	Player player = mock(Player.class);
	private	UUID uuid = new UUID(System.currentTimeMillis(), System.currentTimeMillis());
	private UserIDDao userIDDao = mock(UserIDDao.class);
	private Environment environment = new Environment();
	private Configuration configuration = mock(Configuration.class);

	@InjectMocks
	UserPlayerLinker userPlayerLinker = new UserPlayerLinker(environment);

	@Before
	public void setup()
	{
		environment.setConfiguration(configuration);
	}

	@Test
	public void getUserIDNeverReturnsNull()
	{
		configuration.linkingMethod = "both";
		when(player.getUniqueId()).thenReturn(uuid);
		when(userIDDao.getUserID(anyString())).thenReturn(NAME_USER_ID);
		assertNotNull(userPlayerLinker.getUserID(player));
	}

	@Test
	public void getUserIDWithoutUUIDWithPlayernameReturnsPlayernameUserID()
	{
		configuration.linkingMethod = "both";
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(player.getUniqueId()).thenReturn(uuid);
		when(userIDDao.getUserID(uuid.toString())).thenReturn("");
		when(userIDDao.getUserID(PLAYER_NAME)).thenReturn(NAME_USER_ID);
		assertEquals(NAME_USER_ID, userPlayerLinker.getUserID(player));
	}

	@Test
	public void getUserIDWithUUIDandWithPlayernameReturnsUUIDUserID()
	{
		configuration.linkingMethod = "both";
		when(player.getUniqueId()).thenReturn(uuid);
		when(userIDDao.getUserID(PLAYER_NAME)).thenReturn(NAME_USER_ID);
		when(userIDDao.getUserID(uuid.toString())).thenReturn(UUID_USER_ID);
		assertEquals(UUID_USER_ID, userPlayerLinker.getUserID(player));
	}

	@Test
	public void getUserIDWithUUIDandWithPlayernameUUIDOffReturnsPlayernameUserID()
	{
		configuration.linkingMethod = "name";
		when(player.getUniqueId()).thenReturn(uuid);
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(userIDDao.getUserID(PLAYER_NAME)).thenReturn(NAME_USER_ID);
		when(userIDDao.getUserID(uuid.toString())).thenReturn(UUID_USER_ID);
		assertEquals(NAME_USER_ID, userPlayerLinker.getUserID(player));
	}

	@Test
	public void getUserIDWithoutUUIDandWithPlayernameUUIDOnReturnsBlank()
	{
		configuration.linkingMethod = "uuid";
		when(player.getUniqueId()).thenReturn(uuid);
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(userIDDao.getUserID(PLAYER_NAME)).thenReturn(NAME_USER_ID);
		when(userIDDao.getUserID(uuid.toString())).thenReturn("");
		assertEquals("", userPlayerLinker.getUserID(player));
	}
}