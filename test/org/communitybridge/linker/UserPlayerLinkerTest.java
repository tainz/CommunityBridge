package org.communitybridge.linker;

import org.communitybridge.main.BukkitWrapper;
import java.util.UUID;
import org.apache.commons.lang.RandomStringUtils;
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
	private BukkitWrapper bukkit = mock(BukkitWrapper.class);

	@InjectMocks
	UserPlayerLinker userPlayerLinker = new UserPlayerLinker(environment, 1);

	@Before
	public void setup()
	{
			environment.setConfiguration(configuration);
	}

	@Test
	public void getUserIDByUUIDNeverReturnsNull()
	{
		when(userIDDao.getUserID(uuid.toString())).thenReturn(UUID_USER_ID);
		assertNotNull(userPlayerLinker.getUserID(uuid.toString()));
	}

	@Test
	public void getUserIDByUUIDWithUUIDReturnsUserID()
	{
		configuration.linkingMethod = "both";
		when(userIDDao.getUserID(uuid.toString())).thenReturn(UUID_USER_ID);
		assertEquals(UUID_USER_ID, userPlayerLinker.getUserID(uuid.toString()));
	}

	@Test
	public void getUserIDByUUIDWithoutUUIDReturnsBlank()
	{
		configuration.linkingMethod = "uuid";
		when(userIDDao.getUserID(uuid.toString())).thenReturn("");
		assertEquals("", userPlayerLinker.getUserID(uuid.toString()));
	}

	@Test
	public void getUserIDByPlayerNeverReturnsNull()
	{
		configuration.linkingMethod = "both";
		when(player.getUniqueId()).thenReturn(uuid);
		when(userIDDao.getUserID(anyString())).thenReturn(NAME_USER_ID);
		assertNotNull(userPlayerLinker.getUserID(player));
	}

	@Test
	public void getUserIDbyPlayerWithoutUUIDWithPlayernameReturnsPlayernameUserID()
	{
		configuration.linkingMethod = "both";
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(player.getUniqueId()).thenReturn(uuid);
		when(userIDDao.getUserID(uuid.toString())).thenReturn("");
		when(userIDDao.getUserID(PLAYER_NAME)).thenReturn(NAME_USER_ID);
		assertEquals(NAME_USER_ID, userPlayerLinker.getUserID(player));
	}

	@Test
	public void getUserIDByPlayerWithUUIDandWithPlayernameReturnsUUIDUserID()
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

	@Test
	public void getUserIDAddsUUIDEntryToCache()
	{
		configuration.linkingMethod = "uuid";
		when(player.getUniqueId()).thenReturn(uuid);
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(userIDDao.getUserID(PLAYER_NAME)).thenReturn(NAME_USER_ID);
		when(userIDDao.getUserID(uuid.toString())).thenReturn(UUID_USER_ID);
		userPlayerLinker.getUserID(player);
		assertEquals(UUID_USER_ID, userPlayerLinker.getUserIDCache().get(uuid.toString()));
	}

	@Test
	public void getUserIDAddsNameEntryToCache()
	{
		configuration.linkingMethod = "name";
		when(player.getUniqueId()).thenReturn(uuid);
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(userIDDao.getUserID(PLAYER_NAME)).thenReturn(NAME_USER_ID);
		when(userIDDao.getUserID(uuid.toString())).thenReturn(UUID_USER_ID);
		userPlayerLinker.getUserID(player);
		assertEquals(NAME_USER_ID, userPlayerLinker.getUserIDCache().get(PLAYER_NAME));
	}

	@Test
	public void removeUserIDFromCacheRemovesUUIDEntry()
	{
		configuration.linkingMethod = "uuid";
		when(player.getUniqueId()).thenReturn(uuid);
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(userIDDao.getUserID(PLAYER_NAME)).thenReturn(NAME_USER_ID);
		when(userIDDao.getUserID(uuid.toString())).thenReturn(UUID_USER_ID);
		userPlayerLinker.getUserID(player);
		userPlayerLinker.removeUserIDFromCache(uuid.toString(), PLAYER_NAME);
		assertNull(userPlayerLinker.getUserIDCache().get(uuid.toString()));
	}

	@Test
	public void removeUserIDFromCacheRemovesNameEntry()
	{
		configuration.linkingMethod = "name";
		when(player.getUniqueId()).thenReturn(uuid);
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(userIDDao.getUserID(PLAYER_NAME)).thenReturn(NAME_USER_ID);
		when(userIDDao.getUserID(uuid.toString())).thenReturn(UUID_USER_ID);
		userPlayerLinker.getUserID(player);
		userPlayerLinker.removeUserIDFromCache(uuid.toString(), PLAYER_NAME);
		assertNull(userPlayerLinker.getUserIDCache().get(PLAYER_NAME));
	}

	@Test
	public void secondLookupOnTinyCacheForcesFlush()
	{
		String someOtherName = RandomStringUtils.randomNumeric(6);
		String someOtherID = RandomStringUtils.randomNumeric(2);
		configuration.linkingMethod = "name";
		when(player.getUniqueId()).thenReturn(uuid);
		when(player.getName()).thenReturn(PLAYER_NAME, someOtherName);
		when(userIDDao.getUserID(PLAYER_NAME)).thenReturn(NAME_USER_ID);
		when(userIDDao.getUserID(someOtherName)).thenReturn(someOtherID);
		when(userIDDao.getUserID(uuid.toString())).thenReturn(UUID_USER_ID);
		userPlayerLinker.getUserID(player);
		userPlayerLinker.getUserID(player);
		assertEquals(1, userPlayerLinker.getUserIDCache().size());
		assertEquals(someOtherID, userPlayerLinker.getUserIDCache().get(someOtherName));
		assertFalse(userPlayerLinker.getUserIDCache().containsKey(PLAYER_NAME));
	}

	@Test
	public void getUUIDNeverReturnsNull()
	{
		configuration.linkingMethod = "both";
		when(userIDDao.getUUID(anyString())).thenReturn(uuid.toString());
		assertNotNull(userPlayerLinker.getUUID(""));
	}

	@Test
	public void getUUIDSuccessfullyRetrievesCachedEntry()
	{
		configuration.linkingMethod = "uuid";
		when(player.getUniqueId()).thenReturn(uuid);
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(userIDDao.getUserID(PLAYER_NAME)).thenReturn(NAME_USER_ID);
		when(userIDDao.getUserID(uuid.toString())).thenReturn(UUID_USER_ID);
		userPlayerLinker.getUserID(player);
		assertEquals(uuid.toString(), userPlayerLinker.getUUID(UUID_USER_ID));
	}

	@Test
	public void getUUIDRetrievesUUIDFromDatabase()
	{
		configuration.linkingMethod = "uuid";
		when(player.getUniqueId()).thenReturn(uuid);
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(userIDDao.getUUID(UUID_USER_ID)).thenReturn(uuid.toString());
		assertEquals(uuid.toString(), userPlayerLinker.getUUID(UUID_USER_ID));
	}

	@Test
	public void getPlayerNameNeverReturnsNull()
	{
			configuration.linkingMethod = "both";
			when(userIDDao.getUUID(anyString())).thenReturn(uuid.toString());
			when(bukkit.getPlayer(uuid)).thenReturn(player);
			when(player.getName()).thenReturn(PLAYER_NAME);
			assertNotNull(userPlayerLinker.getPlayerName(UUID_USER_ID));
	}

	@Test
	public void getPlayerNameInNameModeReturnsPreviouslyCachedName()
	{
		configuration.linkingMethod = "name";
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(userIDDao.getUserID(PLAYER_NAME)).thenReturn(NAME_USER_ID);
		when(player.getUniqueId()).thenReturn(uuid);
		userPlayerLinker.getUserID(player);
		assertEquals(PLAYER_NAME, userPlayerLinker.getPlayerName(NAME_USER_ID));
	}

	@Test
	public void getPlayerNameInUUIDModeReturnsNameByPreviouslyCachedUUID()
	{
		configuration.linkingMethod = "uuid";
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(player.getUniqueId()).thenReturn(uuid);
		when(userIDDao.getUserID(uuid.toString())).thenReturn(UUID_USER_ID);
		when(bukkit.getPlayer(uuid)).thenReturn(player);
		userPlayerLinker.getUserID(player);
		assertEquals(PLAYER_NAME, userPlayerLinker.getPlayerName(UUID_USER_ID));
	}

	@Test
	public void getPlayerNameInBothModeReturnsNameByPreviouslyCachedUUID()
	{
		configuration.linkingMethod = "both";
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(player.getUniqueId()).thenReturn(uuid);
		when(userIDDao.getUserID(uuid.toString())).thenReturn(UUID_USER_ID);
		when(bukkit.getPlayer(uuid)).thenReturn(player);
		userPlayerLinker.getUserID(player);
		assertEquals(PLAYER_NAME, userPlayerLinker.getPlayerName(UUID_USER_ID));
	}

	@Test
	public void getPlayerNameInBothModeReturnsPreviouslyCachedName()
	{
		configuration.linkingMethod = "both";
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(userIDDao.getUserID(PLAYER_NAME)).thenReturn(NAME_USER_ID);
		when(userIDDao.getUserID(uuid.toString())).thenReturn("");
		when(player.getUniqueId()).thenReturn(uuid);
		userPlayerLinker.getUserID(player);
		assertEquals(PLAYER_NAME, userPlayerLinker.getPlayerName(NAME_USER_ID));
	}

	@Test
	public void getPlayerNameInBothModeReturnsNormalName()
	{
		configuration.linkingMethod = "both";
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(player.getUniqueId()).thenReturn(uuid);
		when(userIDDao.getUserID(uuid.toString())).thenReturn(UUID_USER_ID);
		when(userIDDao.getUUID(UUID_USER_ID)).thenReturn(uuid.toString());
		when(bukkit.getPlayer(uuid)).thenReturn(player);
		assertEquals(PLAYER_NAME, userPlayerLinker.getPlayerName(UUID_USER_ID));
	}
}