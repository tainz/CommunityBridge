package org.communitybridge.permissionhandlers;

import com.avaje.ebean.EbeanServer;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

//@RunWith(MockitoJUnitRunner.class)
public class PermissionHandlerTest
{
	private Player player = mock(Player.class);
	private final String playerName = "somePlayer";
	private final String groupOne = "groupOne";
	private final String groupTwo = "groupTwo";

	TestablePermissionHandler permissionHandler = new TestablePermissionHandler();

	@Before
	public void setUp()
	{
		permissionHandler = new TestablePermissionHandler();
	}

	@Test
	public void switchGroupCallsRemoveGroup()
	{
		permissionHandler.switchGroup(player, groupOne, groupTwo);
		assertEquals(playerName, permissionHandler.removePlayer);
		assertEquals(groupOne, permissionHandler.removeGroup);
	}

	@Test
	public void switchGroupCallsAddGroup()
	{
		permissionHandler.switchGroup(player, groupOne, groupTwo);
		assertEquals(playerName, permissionHandler.addPlayer);
		assertEquals(groupTwo, permissionHandler.addGroup);
	}

	@Test
	public void validateHandlerDoesNotThrowErrorWithValidPlugin()
	{
		TestablePlugin plugin = new TestablePlugin();
		plugin.setEnabled(true);

		String name = "someplugin";
		try
		{
			permissionHandler.validate(plugin, name);
		}
		catch (Exception exception)
		{
			fail("Caught an exception with valid plugin: " + exception.getMessage());
		}
	}

	@Test
	public void validateHandlerDoesThrowErrorWithNullPlugin()
	{
		Plugin plugin = null;

		String name = "someplugin";
		try
		{
			permissionHandler.validate(plugin, name);
			fail("Failed to throw an exception.");
		}
		catch (IllegalStateException exception)
		{
			assertEquals(name + permissionHandler.NOT_FOUND, exception.getMessage());
		}
		catch (Exception exception)
		{
			fail("Threw incorrect exception: " + exception.getMessage());
		}
	}

	@Test
	public void validateHandlerDoesThrowErrorWithDisabledPlugin()
	{
		TestablePlugin plugin = new TestablePlugin();

		plugin.setEnabled(false);

		String name = "someplugin";
		try
		{
			permissionHandler.validate(plugin, name);
			fail("Failed to throw an exception.");
		}
		catch (IllegalStateException exception)
		{
			assertEquals(name + permissionHandler.NOT_ENABLED, exception.getMessage());
		}
		catch (Exception exception)
		{
			fail("Threw incorrect exception." + exception.getMessage());
		}
	}

	public class TestablePermissionHandler extends PermissionHandler
	{
		public String addPlayer;
		public String addGroup;
		public String removePlayer;
		public String removeGroup;

		@Override
		public boolean addToGroup(Player player, String groupName)
		{
			addPlayer = playerName;
			addGroup = groupName;
			return true;
		}

		@Override
		public boolean removeFromGroup(Player player, String groupName)
		{
			removePlayer = playerName;
			removeGroup = groupName;
			return true;
		}

		@Override
		public String[] getGroups(Player player)
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public String[] getGroupsPure(Player player)
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public String getPrimaryGroup(Player player)
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public boolean isMemberOfGroup(Player player, String groupName)
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public boolean isPrimaryGroup(Player player, String groupName)
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public boolean setPrimaryGroup(Player player, String groupName, String formerGroupName)
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public boolean supportsPrimaryGroups()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}
	}

	public class TestablePlugin implements Plugin
	{
		private boolean enabled = true;

		public void setEnabled(boolean enabled)
		{
			this.enabled = enabled;
		}

		@Override
		public boolean isEnabled()
		{
			return enabled;
		}

		@Override
		public File getDataFolder()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public PluginDescriptionFile getDescription()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public FileConfiguration getConfig()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public InputStream getResource(String string)
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public void saveConfig()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public void saveDefaultConfig()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public void saveResource(String string, boolean bln)
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public void reloadConfig()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public PluginLoader getPluginLoader()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public Server getServer()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public void onDisable()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public void onLoad()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public void onEnable()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public boolean isNaggable()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public void setNaggable(boolean bln)
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public EbeanServer getDatabase()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public ChunkGenerator getDefaultWorldGenerator(String string, String string1)
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public Logger getLogger()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public String getName()
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] strings)
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}

		@Override
		public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
		{
			throw new UnsupportedOperationException("No implementation needed for tests.");
		}
	}
}