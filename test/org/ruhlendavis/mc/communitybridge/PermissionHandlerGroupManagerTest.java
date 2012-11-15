/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ruhlendavis.mc.communitybridge;

import java.util.ArrayList;
import java.util.List;
import net.netmanagers.community.Main;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author Feaelin
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class PermissionHandlerGroupManagerTest
{
	private final String goodPlayerName = "goodPlayer";
	private final	String badPlayerName = "badPlayer";
	private final	String goodGroup = "goodGroup";
	private final String worldName = "world";
	private Server server;
	private Player goodPlayer;
	private World world;
 	private	List<World> worlds;
	private	GroupManager gmPluginAsGroupManager;
	private	WorldsHolder worldHolder;
	private	AnjoPermissionsHandler handler;
	private PermissionHandler permissionHandler;
	
	public PermissionHandlerGroupManagerTest()
	{
	}
	
	@BeforeClass
	public static void setUpClass()
	{
	}
	
	@AfterClass
	public static void tearDownClass()
	{
	}
	
	@Before
	public void setUp()
	{
		PowerMockito.mockStatic(Bukkit.class);
		server = mock(Server.class);
		goodPlayer = mock(Player.class);
		world = mock(World.class);
 		worlds =  new ArrayList<World>();
		worlds.add(world);		
		gmPluginAsGroupManager = mock(GroupManager.class);
		worldHolder = mock(WorldsHolder.class);
		handler = mock(AnjoPermissionsHandler.class);

		when(Bukkit.getServer()).thenReturn(server);
		when(server.getPlayerExact(goodPlayerName)).thenReturn(goodPlayer);
		when(server.getPlayerExact(badPlayerName)).thenReturn(null);
		when(goodPlayer.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn(worldName);
		when(server.getWorlds()).thenReturn(worlds);
		when(gmPluginAsGroupManager.getWorldsHolder()).thenReturn(worldHolder);
		when(worldHolder.getWorldPermissions(worldName)).thenReturn(handler);
		
		Main.permissions_system = "GroupManager";
		permissionHandler = Main.permissionHandler = new PermissionHandlerGroupManager(gmPluginAsGroupManager);
	}
	
	@After
	public void tearDown()
	{
	}

	@Test
	public void testGetPrimaryGroup()
	{
		when(handler.getGroup(goodPlayerName)).thenReturn(goodGroup);

		Assert.assertEquals("getPrimaryGroup() should return null with an invalid player",
						          null, permissionHandler.getPrimaryGroup(badPlayerName));
		Assert.assertEquals("getPrimaryGroup() should return correct group with an valid player",
						          goodGroup, permissionHandler.getPrimaryGroup(goodPlayerName));

	}
	/**
	 * Test of isMemberOfGroup method, of class PermissionHandlerGroupManager.
	 */
	@Test
	public void testIsMemberOfGroup()
	{
		String badGroup = "badGroup";
		String noexistGroup = "thisgroupdoesnotexist";		
		
		when(handler.inGroup(goodPlayerName, goodGroup)).thenReturn(true);
		when(handler.inGroup(badPlayerName, goodGroup)).thenReturn(false);
		when(handler.inGroup(goodPlayerName, badGroup)).thenReturn(false);
		when(handler.inGroup(badPlayerName, badGroup)).thenReturn(false);
		
		Assert.assertTrue("isMemberOfGroup should return true with GroupManager, correct"
						        + " player and correct group",
										  permissionHandler.isMemberOfGroup(goodPlayerName, goodGroup));
		Assert.assertFalse("isMemberOfGroup should return false with GroupManager, incorrect"
						         + " player and correct group",
											 permissionHandler.isMemberOfGroup(badPlayerName, goodGroup));
		Assert.assertFalse("isMemberOfGroup should return false with GroupManager, correct"
						         + " player and incorrect group",
											 permissionHandler.isMemberOfGroup(goodPlayerName, noexistGroup));
		Assert.assertFalse("isMemberOfGroup should return false with GroupManager, incorrect"
						         + " player and incorrect group",
											 permissionHandler.isMemberOfGroup(badPlayerName, noexistGroup));
	}
}
