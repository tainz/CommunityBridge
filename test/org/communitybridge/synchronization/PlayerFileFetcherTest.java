package org.communitybridge.synchronization;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

public class PlayerFileFetcherTest
{
	private PlayerFileFetcher fetcher = new PlayerFileFetcher();
	private Player player = mock(Player.class);
	private UUID UUID = new UUID(RandomUtils.nextLong(), RandomUtils.nextLong());
	private String PLAYER_NAME = RandomStringUtils.randomAlphabetic(10);
	@Mock	private File folder;
	private File mockPlayerFile = mock(File.class);
	private File file = new File("/");

	private PlayerFileFetcher fetcher2 = spy(new PlayerFileFetcher());
	@Before
	public void beforeEach()
	{
		when(player.getUniqueId()).thenReturn(UUID);
		when(player.getName()).thenReturn(PLAYER_NAME);
	}

	@Test
	public void getPlayerShouldNeverReturnNull()
	{
		Assert.assertNotNull(fetcher.getPlayerFile(file, player, false));
		Assert.assertNotNull(fetcher.getPlayerFile(file, player, true));
	}

	@Test
	public void getPlayerShouldUsePlayersFolder() throws IOException
	{
		File playerFile = fetcher.getPlayerFile(file, player, false);
		Assert.assertEquals("Players", playerFile.getParent().substring(1));
	}

	@Test
	public void getPlayerShouldUseUUID()
	{
		File playerFile = fetcher.getPlayerFile(file, player, false);
		Assert.assertEquals(player.getUniqueId().toString() + ".yml", playerFile.getName());
	}

	@Test
	public void getPlayerWhenTypicalDoesNotExistAndAllowedShouldReturnOldPlayerFile()
	{
		doReturn(folder).when(fetcher2).makeFile(any(File.class), anyString());
		doReturn(mockPlayerFile).when(fetcher2).makeFile(eq(folder), anyString());
		when(mockPlayerFile.exists()).thenReturn(false);
		File playerFile = fetcher2.getPlayerFile(file, player, true);
		Assert.assertEquals(player.getName() + ".yml", playerFile.getName());
	}

	@Test
	public void getPlayerWhenTypicalDoesNotExistAndNotAllowedShouldReturnTypicalPlayerFile()
	{
		doReturn(folder).when(fetcher2).makeFile(any(File.class), anyString());
		doReturn(mockPlayerFile).when(fetcher2).makeFile(eq(folder), anyString());
		when(mockPlayerFile.exists()).thenReturn(false);
		File playerFile = fetcher2.getPlayerFile(file, player, false);
		Assert.assertSame(mockPlayerFile, playerFile);
	}

	@Test
	public void getPlayerWhenTypicalDoesExistAndAllowedShouldReturnTypicalPlayerFile()
	{
		doReturn(folder).when(fetcher2).makeFile(any(File.class), anyString());
		doReturn(mockPlayerFile).when(fetcher2).makeFile(eq(folder), anyString());
		when(mockPlayerFile.exists()).thenReturn(true);
		File playerFile = fetcher2.getPlayerFile(file, player, true);
		Assert.assertSame(mockPlayerFile, playerFile);
	}

	@Test
	public void getPlayerWhenTypicalDoesExistAndNotAllowedShouldReturnTypicalPlayerFile()
	{
		doReturn(folder).when(fetcher2).makeFile(any(File.class), anyString());
		doReturn(mockPlayerFile).when(fetcher2).makeFile(eq(folder), anyString());
		when(mockPlayerFile.exists()).thenReturn(true);
		File playerFile = fetcher2.getPlayerFile(file, player, false);
		Assert.assertSame(mockPlayerFile, playerFile);
	}
}

