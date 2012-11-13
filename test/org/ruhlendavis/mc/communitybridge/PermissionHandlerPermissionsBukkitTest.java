package org.ruhlendavis.mc.communitybridge;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
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
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.mockito.Mockito.mock;

/**
 *
 * @author Feaelin
 */
public class PermissionHandlerPermissionsBukkitTest
{
	
	public PermissionHandlerPermissionsBukkitTest()
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
	 * Test of isMemberOfGroup method, of class PermissionHandlerPermissionsBukkit.
	 */
	@Test
	public void testIsMemberOfGroup()
	{
		String goodPlayer = "goodPlayer";
		String badPlayer = "badPlayer";
		String goodGroupName = "goodGroup";
		String badGroupName = "badGroup";
		List<String> groupPlayers = new ArrayList();
		groupPlayers.add(goodPlayer.toLowerCase());
		
		PermissionsPlugin permissions = mock(PermissionsPlugin.class);
		Group goodGroup = mock(Group.class);
		when(permissions.getGroup(goodGroupName)).thenReturn(goodGroup);
		when(permissions.getGroup(badGroupName)).thenReturn(null);
		when(goodGroup.getPlayers()).thenReturn(groupPlayers);
		
		PermissionHandler ph = Main.permissionHandler = new PermissionHandlerPermissionsBukkit(permissions);
		
		Assert.assertTrue("isMemberOfGroup should return true with PermissionsBukkit, correct"
						        + " player (with caps) and correct group",
										  ph.isMemberOfGroup(goodPlayer, goodGroupName));
		Assert.assertTrue("isMemberOfGroup should return true with PermissionsBukkit, correct"
						        + " player (without caps) and correct group",
										  ph.isMemberOfGroup(goodPlayer.toLowerCase(), goodGroupName));
		Assert.assertFalse("isMemberOfGroup should return false with PermissionsBukkit, incorrect"
						         + " player and correct group",
											 ph.isMemberOfGroup(badPlayer, goodGroupName));
		Assert.assertFalse("isMemberOfGroup should return false with PermissionsBukkit, correct"
						         + " player and incorrect group",
											 ph.isMemberOfGroup(badPlayer, badGroupName));
		Assert.assertFalse("isMemberOfGroup should return false with PermissionsBukkit, incorrect"
						         + " player and incorrect group",
											 ph.isMemberOfGroup(badPlayer, badGroupName));

	}
}
