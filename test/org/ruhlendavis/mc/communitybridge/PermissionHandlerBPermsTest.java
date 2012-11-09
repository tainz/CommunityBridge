/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ruhlendavis.mc.communitybridge;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
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
@PrepareForTest(ApiLayer.class)
public class PermissionHandlerBPermsTest
{
	
	public PermissionHandlerBPermsTest()
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
	 * Test of isMemberOfGroup method, of class PermissionHandlerBPerms.
	 */
	@Test
	public void testIsMemberOfGroup()
	{
		PowerMockito.mockStatic(ApiLayer.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		
		String goodPlayerName = "goodPlayer";
		String goodGroup = "goodGroup";
		String badPlayerName = "badPlayer";
		String badGroup = "badGroup";
		String noexistGroup = "thisgroupdoesnotexist";
		
		String [] goodPlayerGroups = { goodGroup };
		String [] badPlayerGroups = { badGroup };
		
		when(ApiLayer.getGroups(null, CalculableType.USER, goodPlayerName)).thenReturn(goodPlayerGroups);
		when(ApiLayer.getGroups(null, CalculableType.USER, badPlayerName)).thenReturn(badPlayerGroups);
		
		Main.permissions_system = "bPerms";
		PermissionHandler ph = Main.permissionHandler = new PermissionHandlerBPerms(plugin);

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
