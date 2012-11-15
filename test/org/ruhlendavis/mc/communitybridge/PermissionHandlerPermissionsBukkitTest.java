package org.ruhlendavis.mc.communitybridge;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionInfo;
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
	private final String goodPlayerName = "goodPlayer";
	private final String badPlayerName = "badPlayer";
	private final String goodGroupName = "goodGroup";
	private final	String badGroupName = "badGroup";
	private final String noexistGroupName = "thisgroupdoesnotexist";
	private Group goodGroup;
	private PermissionInfo goodPlayerInfo;
  private PermissionsPlugin permissions;
  private PermissionHandler permissionHandler;
	private List<String> groupPlayers;
	private List<Group> goodPlayerGroups;

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
		groupPlayers = new ArrayList<String>();
		groupPlayers.add(goodPlayerName.toLowerCase());
		
		permissions = mock(PermissionsPlugin.class);
		goodGroup = mock(Group.class);
		goodPlayerGroups = new ArrayList<Group>();
		goodPlayerGroups.add(goodGroup);
		goodPlayerInfo = mock(PermissionInfo.class);
		when(permissions.getGroup(goodGroupName)).thenReturn(goodGroup);
		when(permissions.getGroup(badGroupName)).thenReturn(null);
		when(goodGroup.getPlayers()).thenReturn(groupPlayers);
		when(goodGroup.getName()).thenReturn(goodGroupName);
		when(permissions.getPlayerInfo(goodPlayerName)).thenReturn(goodPlayerInfo);
		when(goodPlayerInfo.getGroups()).thenReturn(goodPlayerGroups);
		
		permissionHandler = new PermissionHandlerPermissionsBukkit(permissions);
	}
	
	@After
	public void tearDown()
	{
	}

	@Test
	public void testGetPrimaryGroup()
	{
		String validNullGroupPlayerName = "validNullGroupPlayerName";
		String validNoGroupPlayerName = "validNoGroupPlayerName";
		PermissionInfo validNullGroupPlayerInfo = mock(PermissionInfo.class);
		PermissionInfo validNoGroupPlayerInfo = mock(PermissionInfo.class);
		
		List<Group> NoGroup = new ArrayList<Group>();

		when(permissions.getPlayerInfo(validNullGroupPlayerName)).thenReturn(validNullGroupPlayerInfo);
		when(validNullGroupPlayerInfo.getGroups()).thenReturn(null);
		when(permissions.getPlayerInfo(validNoGroupPlayerName)).thenReturn(validNoGroupPlayerInfo);
		when(validNoGroupPlayerInfo.getGroups()).thenReturn(NoGroup);

		Assert.assertEquals("getPrimaryGroup should return null with invalid player",
						            null, permissionHandler.getPrimaryGroup(badPlayerName));
		Assert.assertEquals("getPrimaryGroup should return null with player without grouplist",
						            null, permissionHandler.getPrimaryGroup(validNullGroupPlayerName));
		Assert.assertEquals("getPrimaryGroup should return null with player without valid group",
						            null, permissionHandler.getPrimaryGroup(validNoGroupPlayerName));
		Assert.assertEquals("getPrimaryGroup should return correct group with valid player",
						            goodGroupName,
												permissionHandler.getPrimaryGroup(goodPlayerName));
	}
	/**
	 * Test of isMemberOfGroup method, of class PermissionHandlerPermissionsBukkit.
	 */
	@Test
	public void testIsMemberOfGroup()
	{
		Assert.assertTrue("isMemberOfGroup should return true with PermissionsBukkit, correct"
						        + " player (with caps) and correct group",
										  permissionHandler.isMemberOfGroup(goodPlayerName, goodGroupName));
		Assert.assertTrue("isMemberOfGroup should return true with PermissionsBukkit, correct"
						        + " player (without caps) and correct group",
										  permissionHandler.isMemberOfGroup(goodPlayerName.toLowerCase(), goodGroupName));
		Assert.assertFalse("isMemberOfGroup should return false with PermissionsBukkit, incorrect"
						         + " player and correct group",
											 permissionHandler.isMemberOfGroup(badPlayerName, goodGroupName));
		Assert.assertFalse("isMemberOfGroup should return false with PermissionsBukkit, correct"
						         + " player and incorrect group",
											 permissionHandler.isMemberOfGroup(badPlayerName, badGroupName));
		Assert.assertFalse("isMemberOfGroup should return false with PermissionsBukkit, incorrect"
						         + " player and incorrect group",
											 permissionHandler.isMemberOfGroup(badPlayerName, badGroupName));

	}
	
	/**
	 * Test of isPrimaryGroup method, of class PermissionHandlerPermissionsBukkit
	 */
	@Test
	public void testIsPrimaryGroup()
	{	
		Assert.assertTrue("isPrimaryGroup() should return true with valid player/group combo",
						          permissionHandler.isPrimaryGroup(goodPlayerName, goodGroupName));
		Assert.assertFalse("isPrimaryGroup() should return false with valid player, wrong group",
						          permissionHandler.isPrimaryGroup(goodPlayerName, noexistGroupName));
		Assert.assertFalse("isPrimaryGroup() should return false with invalid player",
						          permissionHandler.isPrimaryGroup(badPlayerName, goodGroupName));
	}

}
