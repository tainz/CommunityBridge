package org.ruhlendavis.mc.communitybridge;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import java.util.ArrayList;
import java.util.List;
import net.netmanagers.community.Main;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
		List<String> groupPlayers = new ArrayList<String>();
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
