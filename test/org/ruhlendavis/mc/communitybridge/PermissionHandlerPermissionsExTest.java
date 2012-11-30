package org.ruhlendavis.mc.communitybridge;

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
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Tests for the PermissionHandlerPermissionsEx Class
 * 
 * @author Feaelin
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PermissionsEx.class)
public class PermissionHandlerPermissionsExTest
{
	private final String goodPlayerName = "goodPlayerName";
	private final String badPlayerName = "badPlayerName";
	private final String goodGroupName = "goodGroupName";
	private final String badGroupName = "badGroupName";
	private final String noexistGroupName = "thisgroupdoesnotexist";
	private final String noGroupPlayerName = "noGroupPlayerName";
	private final String [] noGroupPlayerGroups = {};
	private final String [] goodPlayerGroups = { goodGroupName };

	private PermissionUser goodUser;
	private PermissionUser noGroupUser;
	private PermissionHandler permissionHandler = new PermissionHandlerPermissionsEx(true);
	
	public PermissionHandlerPermissionsExTest()
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
		PowerMockito.mockStatic(PermissionsEx.class);
		goodUser = mock(PermissionUser.class);
		noGroupUser = mock(PermissionUser.class);
		
		when(PermissionsEx.getUser(goodPlayerName)).thenReturn(goodUser);
		when(PermissionsEx.getUser(badPlayerName)).thenReturn(null);
		when(goodUser.getGroupsNames()).thenReturn(goodPlayerGroups);
		when(noGroupUser.getGroupsNames()).thenReturn(noGroupPlayerGroups);
	}
	
	@After
	public void tearDown()
	{
	}

	/**
	 * Test of getPrimaryGroup method of class PermissionHandlerPermissionsEx
	 */
	@Test
	public void testGetPrimaryGroup()
	{
		Assert.assertEquals("getPrimaryGroup() should return null with an invalid player",
						          null, permissionHandler.getPrimaryGroup(badPlayerName));
		Assert.assertEquals("getPrimaryGroup() should return null with a valid player with no groups",
						          null, permissionHandler.getPrimaryGroup(noGroupPlayerName));
		Assert.assertEquals("getPrimaryGroup() should return correct group with an valid player",
						          goodGroupName, permissionHandler.getPrimaryGroup(goodPlayerName));

	}
	
	/**
	 * Test of isMemberOfGroup method, of class PermissionHandlerPermissionsEx.
	 */
	@Test
	public void testIsMemberOfGroup()
	{
		when(goodUser.inGroup(goodGroupName)).thenReturn(true);
		when(goodUser.inGroup(badGroupName)).thenReturn(false);
		Assert.assertTrue("isMemberOfGroup should return true with PEX, correct"
						        + " player and correct group",
										  permissionHandler.isMemberOfGroup(goodPlayerName, goodGroupName));
		Assert.assertFalse("isMemberOfGroup should return false with PEX, incorrect"
						         + " player and correct group",
											 permissionHandler.isMemberOfGroup(badPlayerName, goodGroupName));
		Assert.assertFalse("isMemberOfGroup should return false with PEX, correct"
						         + " player and incorrect group",
											 permissionHandler.isMemberOfGroup(badPlayerName, badGroupName));
		Assert.assertFalse("isMemberOfGroup should return false with PEX, incorrect"
						         + " player and incorrect group",
											 permissionHandler.isMemberOfGroup(badPlayerName, badGroupName));
	}
	
	/**
	 * Test of isPrimaryGroup method, of class PermissionHandlerPermissionsEx
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
