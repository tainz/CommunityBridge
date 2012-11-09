/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ruhlendavis.mc.communitybridge;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.plugin.Plugin;

/**
 *  Implements the permission handler interface for GroupManager.
 * 
 * @author Feaelin
 */
public class PermissionHandlerGroupManager implements PermissionHandler
{
	private static GroupManager groupManager;
	
	public PermissionHandlerGroupManager(Plugin plugin) throws IllegalStateException
	{
		Plugin groupManagerPlugin = plugin.getServer().getPluginManager().getPlugin("GroupManager");
 
		if (groupManagerPlugin != null && groupManagerPlugin.isEnabled())
		{
			groupManager = (GroupManager)groupManagerPlugin;
		}
		else
		{
			throw new IllegalStateException("GroupManager is either not present or not enabled.");
		}
	}
	
	@Override
	public boolean isMemberOfGroup(String playerName, String groupName)
	{
		AnjoPermissionsHandler handler;
		
		handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);

		if (handler == null)
		{
			return false;
		}
		else
		{
			return handler.inGroup(playerName, groupName);
		}
	}
}
