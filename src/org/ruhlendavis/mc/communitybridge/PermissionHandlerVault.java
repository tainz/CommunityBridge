/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ruhlendavis.mc.communitybridge;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */
public class PermissionHandlerVault implements PermissionHandler
{
	public PermissionHandlerVault() throws IllegalStateException
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Vault");

		if (plugin == null || plugin.isEnabled())
		{
			throw new IllegalStateException("bPermissions is either not present or not enabled.");
		}
	}
	@Override
	public boolean addToGroup(String playerName, String groupName)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String[] getGroups(String playerName)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String[] getGroupsPure(String playerName)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getPrimaryGroup(String playerName)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isMemberOfGroup(String playerName, String groupName)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isPrimaryGroup(String playerName, String groupName)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean removeFromGroup(String playerName, String groupName)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean setPrimaryGroup(String playerName, String groupName, String formerGroupName)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean supportsPrimaryGroups()
	{
		return false;
	}
}
