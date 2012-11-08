/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ruhlendavis.mc.communitybridge;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import net.netmanagers.community.Main;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import net.netmanagers.community.Main;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.mockito.Mockito.mock;

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
	public void testIsMemberOfGroup()
	{
		String goodPlayerName = "goodPlayer";
		String badPlayerName = "badPlayer";
		String goodGroup = "goodGroup";
		String badGroup = "badGroup";
		String noexistGroup = "thisgroupdoesnotexist";
		
		PowerMockito.mockStatic(JavaPlugin.class);

		JavaPlugin plugin = mock(JavaPlugin.class);
		Server server = mock(Server.class);
		PluginManager pluginManager = mock(PluginManager.class);
		JavaPlugin gmPluginAsJavaPlugin = mock(JavaPlugin.class);
		GroupManager gmPluginAsGroupManager = mock(GroupManager.class);//(GroupManager)gmPluginAsJavaPlugin;
		WorldsHolder worldHolder = mock(WorldsHolder.class);
		AnjoPermissionsHandler goodPlayerHandler = mock(AnjoPermissionsHandler.class);
		AnjoPermissionsHandler badPlayerHandler = mock(AnjoPermissionsHandler.class);
		
		when(plugin.getServer()).thenReturn(server);
		when(server.getPluginManager()).thenReturn(pluginManager);
		when(pluginManager.getPlugin("GroupManager")).thenReturn(gmPluginAsJavaPlugin);
		when(gmPluginAsJavaPlugin.isEnabled()).thenReturn(true);
		when(gmPluginAsGroupManager.getWorldsHolder()).thenReturn(worldHolder);
		when(worldHolder.getWorldPermissionsByPlayerName(goodPlayerName)).thenReturn(goodPlayerHandler);
		when(worldHolder.getWorldPermissionsByPlayerName(badPlayerName)).thenReturn(badPlayerHandler);
		
		when(goodPlayerHandler.inGroup(goodPlayerName, goodGroup)).thenReturn(true);
		when(goodPlayerHandler.inGroup(badPlayerName, goodGroup)).thenReturn(false);
		when(goodPlayerHandler.inGroup(goodPlayerName, badGroup)).thenReturn(false);
		when(goodPlayerHandler.inGroup(badPlayerName, badGroup)).thenReturn(false);
		
		Main.permissions_system = "GroupManager";
		PermissionHandler ph = Main.permissionHandler = new PermissionHandlerGroupManager(plugin);

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
