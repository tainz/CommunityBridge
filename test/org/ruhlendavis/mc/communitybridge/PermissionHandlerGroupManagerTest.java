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
public class PermissionHandlerGroupManagerTest
{
	
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
	}
	
	@After
	public void tearDown()
	{
	}

	/**
	 * Test of isMemberOfGroup method, of class PermissionHandlerGroupManager.
	 */
	@Test
	@PrepareForTest(Bukkit.class)
	public void testIsMemberOfGroup()
	{
		String goodPlayerName = "goodPlayer";
		String badPlayerName = "badPlayer";
		String goodGroup = "goodGroup";
		String badGroup = "badGroup";
		String noexistGroup = "thisgroupdoesnotexist";		
		
		PowerMockito.mockStatic(Bukkit.class);
		
		Server server = mock(Server.class);
		Player goodPlayer = mock(Player.class);
		World world = mock(World.class);
 		List<World> worlds =  new ArrayList();
		worlds.add(world);		
		GroupManager gmPluginAsGroupManager = mock(GroupManager.class);
		WorldsHolder worldHolder = mock(WorldsHolder.class);
		AnjoPermissionsHandler handler = mock(AnjoPermissionsHandler.class);
		
		when(Bukkit.getServer()).thenReturn(server);
		when(server.getPlayerExact(goodPlayerName)).thenReturn(goodPlayer);
		when(server.getPlayerExact(badPlayerName)).thenReturn(null);
		when(goodPlayer.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(server.getWorlds()).thenReturn(worlds);
		when(gmPluginAsGroupManager.getWorldsHolder()).thenReturn(worldHolder);
		when(worldHolder.getWorldPermissions("world")).thenReturn(handler);		

		when(handler.inGroup(goodPlayerName, goodGroup)).thenReturn(true);
		when(handler.inGroup(badPlayerName, goodGroup)).thenReturn(false);
		when(handler.inGroup(goodPlayerName, badGroup)).thenReturn(false);
		when(handler.inGroup(badPlayerName, badGroup)).thenReturn(false);
		
		Main.permissions_system = "GroupManager";
		PermissionHandler ph = Main.permissionHandler = new PermissionHandlerGroupManager(gmPluginAsGroupManager);

		Assert.assertTrue("isMemberOfGroup should return true with GroupManager, correct"
						        + " player and correct group",
										  ph.isMemberOfGroup(goodPlayerName, goodGroup));
		Assert.assertFalse("isMemberOfGroup should return false with GroupManager, incorrect"
						         + " player and correct group",
											 ph.isMemberOfGroup(badPlayerName, goodGroup));
		Assert.assertFalse("isMemberOfGroup should return false with GroupManager, correct"
						         + " player and incorrect group",
											 ph.isMemberOfGroup(goodPlayerName, noexistGroup));
		Assert.assertFalse("isMemberOfGroup should return false with GroupManager, incorrect"
						         + " player and incorrect group",
											 ph.isMemberOfGroup(badPlayerName, noexistGroup));
	}
}
