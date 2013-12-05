/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.communitybridge.permissionhandlers;

import org.bukkit.plugin.Plugin;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */
public class PermissionHandlerTest
{
	PermissionHandler permissionHandler = new TestablePermissionHandler();
	
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
		permissionHandler = new TestablePermissionHandler();
	}
	
	@After
	public void tearDown()
	{
	}

	@Test
	public void validateHandlerDoesNotThrowErrorWithValidPlugin()
	{
		TestablePlugin plugin = new TestablePlugin();
		plugin.setEnabled(true);
		
		String name = "someplugin";
		try
		{
			permissionHandler.validateHandler(plugin, name);
		}
		catch (Exception exception)
		{
			fail("Caught an exception with valid plugin: " + exception.getMessage());
		}
	}
	
	@Test
	public void validateHandlerDoesThrowErrorWithNullPlugin()
	{
		Plugin plugin = null;
		
		String name = "someplugin";
		try
		{
			permissionHandler.validateHandler(plugin, name);
			fail("Failed to throw an exception.");
		}
		catch (IllegalStateException exception)
		{
			assertEquals(name + permissionHandler.NOT_FOUND, exception.getMessage());
		}
		catch (Exception exception)
		{
			fail("Threw incorrect exception:" + exception.getMessage());
		}
	}
	
	@Test
	public void validateHandlerDoesThrowErrorWithDisabledPlugin()
	{
		TestablePlugin plugin = new TestablePlugin();
		
		plugin.setEnabled(false);
		
		String name = "someplugin";
		try
		{
			permissionHandler.validateHandler(plugin, name);
			fail("Failed to throw an exception.");
		}
		catch (IllegalStateException exception)
		{
			assertEquals(name + permissionHandler.NOT_ENABLED, exception.getMessage());
		}
		catch (Exception exception)
		{
			fail("Threw incorrect exception.");
		}
	}
}