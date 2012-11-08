/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ruhlendavis.mc.communitybridge;

import java.lang.reflect.Method;
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
public class MainTest
{
	
	public MainTest()
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
	public void testIsOkayToSetPrimaryGroup()
	{
		//PowerMockito.mockStatic(Main.class);

		String intro = "isOkayToSetPrimaryGroup should return ";
		Main.primary_group_synchronization_enabled = true;
  	Assert.assertTrue(intro + "true if primary_group sync is enabled",
						          Main.isOkayToSetPrimaryGroup());
		Main.primary_group_synchronization_enabled = false;
  	Assert.assertFalse(intro + "false if primary_group sync is disabled",
						          Main.isOkayToSetPrimaryGroup());

	}
}
