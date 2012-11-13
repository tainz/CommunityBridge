package org.ruhlendavis.mc.communitybridge;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *	Implements the permission handler interface for GroupManager.
 * 
 * @author Feaelin
 */
public class PermissionHandlerGroupManager implements PermissionHandler
{
	private static GroupManager groupManager;

	/**
	 * Setup for the GroupManager Permissions Handler
	 * 
	 * @param plugin JavaPlugin This plugin, i.e., CommunityBridge
	 * @throws IllegalStateException When GroupManager is not loaded or not enabled.
	 */
	public PermissionHandlerGroupManager() throws IllegalStateException
	{
		Plugin groupManagerPlugin = Bukkit.getServer().getPluginManager().getPlugin("GroupManager");

		if (groupManagerPlugin != null && groupManagerPlugin.isEnabled())
		{
			groupManager = (GroupManager)groupManagerPlugin;
		}
		else
		{
			throw new IllegalStateException("GroupManager is either not present or not enabled.");
		}
	}
	
	/**
	 * This is actually here to allow the unit tests to work. This bypasses
	 * the need to mock all the object types used in the normal constructor.
	 * 
	 * @param groupManagerIn
	 */
	public PermissionHandlerGroupManager(GroupManager groupManagerIn) throws IllegalStateException
	{
		groupManager = groupManagerIn;
	}

	/**
	 * Determines whether a player is a member of a group.
	 * 
	 * @param playerName String containing the name of the player to check
	 * @param groupName  String containing the name of the group to check
	 * @return boolean true if the player is a member of the group
	 * @throws RuntimeException If it fails to get a GroupManager permissions handler
	 */
	@Override
	public boolean isMemberOfGroup(String playerName, String groupName) throws RuntimeException
	{
		String worldName;
		
		Player player = Bukkit.getServer().getPlayerExact(playerName);
		
		if (player == null)
		{
			worldName = Bukkit.getServer().getWorlds().get(0).getName();
		}
		else
		{
			worldName = player.getWorld().getName();
		}
		
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);

		if (handler == null)
		{
			throw new RuntimeException("isMemberOfGroup(): Failed to obtain a GroupManager permissions handler");
		}
		else
		{
			return handler.inGroup(playerName, groupName);
		}
	}
}
