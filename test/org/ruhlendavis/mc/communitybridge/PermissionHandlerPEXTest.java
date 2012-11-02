/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ruhlendavis.mc.communitybridge;

import net.netmanagers.community.Main;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.ruhlendavis.mc.communitybridge.PermissionHandlerPEX;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import java.sql.ResultSet;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.ruhlendavis.mc.communitybridge.PermissionHandlerPEX;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author Feaelin
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PermissionsEx.class)
public class PermissionHandlerPEXTest
{
	
	public PermissionHandlerPEXTest()
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
	 * Test of isMemberOfGroup method, of class PermissionHandlerPEX.
	 */
	@Test
	public void testIsMemberOfGroup()
	{
		PowerMockito.mockStatic(PermissionsEx.class);
		PermissionUser goodUser = mock(PermissionUser.class);
		PermissionUser badUser = mock(PermissionUser.class);
		
		String goodPlayer = "goodPlayerName";
		String badPlayer = "badPlayerName";
		String goodGroup = "goodGroupName";
		String badGroup = "badGroupName";

		when(PermissionsEx.getUser(goodPlayer)).thenReturn(goodUser);
		when(PermissionsEx.getUser(badPlayer)).thenReturn(badUser);
		when(goodUser.inGroup(goodGroup)).thenReturn(true);
		when(goodUser.inGroup(badGroup)).thenReturn(false);
		when(badUser.inGroup(anyString())).thenReturn(false);
		Main.permissions_system = "PEX";
		PermissionHandler ph = Main.permissionHandler = new PermissionHandlerPEX();
		
		Assert.assertTrue("isMemberOfGroup should return true with PEX, correct"
						        + " player and correct group",
										  ph.isMemberOfGroup(goodPlayer, goodGroup));
		Assert.assertFalse("isMemberOfGroup should return false with PEX, incorrect"
						         + " player and correct group",
											 ph.isMemberOfGroup(badPlayer, goodGroup));
		Assert.assertFalse("isMemberOfGroup should return false with PEX, correct"
						         + " player and incorrect group",
											 ph.isMemberOfGroup(badPlayer, badGroup));
		Assert.assertFalse("isMemberOfGroup should return false with PEX, incorrect"
						         + " player and incorrect group",
											 ph.isMemberOfGroup(badPlayer, badGroup));
	}
}
