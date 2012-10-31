/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.netmanagers.community;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PermissionsEx.class)
public class MainTest
{    
//	public static void main(String args[])
//	{
//      org.junit.runner.JUnitCore.main("net.netmanagers.community.MainTest");
//  }
	
  public void setUp()
	{
  }

  public void tearDown()
	{
  }

	@Test
	public void testIsMemberOfGroupPEX()
	{
		PowerMockito.mockStatic(PermissionsEx.class);
		PermissionUser goodUser = mock(PermissionUser.class);
		PermissionUser badUser = mock(PermissionUser.class);

		when(PermissionsEx.getUser("testPlayerName")).thenReturn(goodUser);
		when(PermissionsEx.getUser("testBadPlayerName")).thenReturn(badUser);
		when(goodUser.inGroup("testGroupName")).thenReturn(true);
		when(goodUser.inGroup("testBadGroupName")).thenReturn(false);
		when(badUser.inGroup(anyString())).thenReturn(false);
		Main.permissions_system = "PEX";
		Assert.assertTrue("isMemberOfGroup should return true with PEX, correct"
						        + " player and correct group",
										  Main.isMemberOfGroup("testGroupName", "testPlayerName"));
		Assert.assertFalse("isMemberOfGroup should return false with PEX, incorrect"
						        + " player and correct group",
											 Main.isMemberOfGroup("testGroupName", "testBadPlayerName"));
		Assert.assertFalse("isMemberOfGroup should return false with PEX, correct"
						        + " player and incorrect group",
											 Main.isMemberOfGroup("testBadGroupName", "testBadPlayerName"));
		Assert.assertFalse("isMemberOfGroup should return false with PEX, incorrect"
						        + " player and incorrect group",
											 Main.isMemberOfGroup("testBadGroupName", "testBadPlayerName"));
	}
}
