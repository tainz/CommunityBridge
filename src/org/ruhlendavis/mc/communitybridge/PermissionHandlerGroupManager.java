/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ruhlendavis.mc.communitybridge;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 *  Implements the permission handler interface for GroupManager.
 * 
 * @author Feaelin
 */
// TODO: Test and confirm the functionality of this class
public class PermissionHandlerGroupManager implements PermissionHandler
{
	private static GroupManager groupManager;
	
	public PermissionHandlerGroupManager(Plugin plugin)
	{
		Plugin GMplugin = plugin.getServer().getPluginManager().getPlugin("GroupManager");
 
		if (GMplugin != null && GMplugin.isEnabled())
		{
			groupManager = (GroupManager)GMplugin;
		}
		else
		{
			// TODO: Determine what to throw when the GroupManager plugin isn't available
		}
	}
	
	@Override
	public boolean isMemberOfGroup(String playerName, String groupName)
	{
		AnjoPermissionsHandler h = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);

		if (h == null)
		{
			// TODO: handle a null handler
		}
		else
		{
			h.inGroup(playerName, groupName);
		}
						
		return true;
	}
}
