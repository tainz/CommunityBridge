/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ruhlendavis.mc.communitybridge;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import java.util.ArrayList;
import java.util.List;
import net.netmanagers.community.Main;
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
public class PermissionHandlerBPermissionsTest
{
	
	public PermissionHandlerBPermissionsTest()
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
	 * Test of isMemberOfGroup method, of class PermissionHandlerBPermissions.
	 */
	@Test
	@PrepareForTest({Bukkit.class, ApiLayer.class})
	public void testIsMemberOfGroup()
	{
		String goodPlayerName = "goodPlayer";
		String goodGroup = "goodGroup";
		String badPlayerName = "badPlayer";
		String badGroup = "badGroup";
		String noexistGroup = "thisgroupdoesnotexist";
		String worldName = "world";
		String [] goodPlayerGroups = { goodGroup };
		String [] badPlayerGroups = { badGroup };
		
		PowerMockito.mockStatic(Bukkit.class);
		Server server = mock(Server.class);
		when(Bukkit.getServer()).thenReturn(server);
		
		Player goodPlayer = mock(Player.class);
		when(server.getPlayerExact(goodPlayerName)).thenReturn(goodPlayer);
		when(server.getPlayerExact(badPlayerName)).thenReturn(null);

		World world = mock(World.class);
 		List<World> worlds =  new ArrayList();
		worlds.add(world);				
		when(goodPlayer.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(server.getWorlds()).thenReturn(worlds);
		
		PowerMockito.mockStatic(ApiLayer.class);
		when(ApiLayer.getGroups(worldName, CalculableType.USER, goodPlayerName)).thenReturn(goodPlayerGroups);
		when(ApiLayer.getGroups(worldName, CalculableType.USER, badPlayerName)).thenReturn(badPlayerGroups);
		
		Main.permissions_system = "bPerms";
		PermissionHandler ph = Main.permissionHandler = new PermissionHandlerBPermissions(true);

		Assert.assertTrue("isMemberOfGroup should return true with bPerms, correct"
						        + " player and correct group",
										  ph.isMemberOfGroup(goodPlayerName, goodGroup));
		Assert.assertFalse("isMemberOfGroup should return false with bPerms, incorrect"
						         + " player and correct group",
											 ph.isMemberOfGroup(badPlayerName, goodGroup));
		Assert.assertFalse("isMemberOfGroup should return false with bPerms, correct"
						         + " player and incorrect group",
											 ph.isMemberOfGroup(goodPlayerName, noexistGroup));
		Assert.assertFalse("isMemberOfGroup should return false with bPerms, incorrect"
						         + " player and incorrect group",
											 ph.isMemberOfGroup(badPlayerName, noexistGroup));
	}
}
