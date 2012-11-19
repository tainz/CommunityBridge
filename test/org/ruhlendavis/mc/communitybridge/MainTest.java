package org.ruhlendavis.mc.communitybridge;

import java.util.ArrayList;
import net.netmanagers.community.Main;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
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

	@Test
	public void testIsOkayToSetPrimaryGroup()
	{
		//PowerMockito.mockStatic(Main.class);

		String intro = "isOkayToSetPrimaryGroup should return ";
		String groupID = "1";
		String ignoredGroupID = "17";
		
		Main.primary_group_ids_to_ignore = new ArrayList();
		Main.primary_group_ids_to_ignore.add(ignoredGroupID);
		
		Main.primary_group_synchronization_enabled = false;
		Assert.assertFalse(intro + "false if primary_group sync is disabled and groupID is null",
						          Main.isOkayToSetPrimaryGroup(null));
		Assert.assertFalse(intro + "false if primary_group sync is disabled",
						          Main.isOkayToSetPrimaryGroup(groupID));
  	Main.primary_group_synchronization_enabled = true;
		Assert.assertTrue(intro + "true if primary group sync is enabled and groupID is null",
						          Main.isOkayToSetPrimaryGroup(null));
		Assert.assertTrue(intro + "true if primary_group sync is enabled",
						          Main.isOkayToSetPrimaryGroup(groupID));
		Assert.assertFalse(intro + "false if primary_group sync is enabled and the group is on the ignore list",
						           Main.isOkayToSetPrimaryGroup(ignoredGroupID));
	}
}
