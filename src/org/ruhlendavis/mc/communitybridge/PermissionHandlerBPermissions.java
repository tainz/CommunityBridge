package org.ruhlendavis.mc.communitybridge;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Implements the PermissionHandler interface for bPermissions
 * Notes about bPermissions
 * 2013-May-03: bP does not support the notion of a primary group.
 *
 * @author Feaelin
 */
public class PermissionHandlerBPermissions implements PermissionHandler
{
	/**
	 * Actual constructor.
	 *
	 * @throws IllegalStateException thrown when bPermissions plugin is not present or disabled.
	 */
	public PermissionHandlerBPermissions() throws IllegalStateException
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("bPermissions");

		if (plugin != null && plugin.isEnabled())
		{}
		else
		{
			throw new IllegalStateException("bPermissions is either not present or not enabled.");
		}
	}

	/**
	 * Adds a player to a group.
	 *
	 * @param playerName String containing the player's name.
	 * @param groupName  String containing the group's name.
	 * @return True if the add succeeded, false if it failed for any reason.
	 */
	@Override
	public boolean addToGroup(String playerName, String groupName)
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

		try
		{
			ApiLayer.addGroup(worldName, CalculableType.USER, playerName, groupName);
		}
		catch (Error e)
		{
			CommunityBridge.log.severe("addToGroup(): " + e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Retrieves an array of group names for the player.
	 *
	 * @param playerName String containing the name of the player.
	 * @return An String array containing the group names.
	 */
	@Override
	public String [] getGroups(String playerName)
	{
		String worldName;
		String [] groups = {};
		Player player = Bukkit.getServer().getPlayerExact(playerName);

		if (player == null)
		{
			worldName = Bukkit.getServer().getWorlds().get(0).getName();
		}
		else
		{
			worldName = player.getWorld().getName();
		}

		try
		{
			groups = ApiLayer.getGroups(worldName, CalculableType.USER, playerName);
		}
		catch (Error e)
		{
			CommunityBridge.log.severe("getGroups(): " + e.getMessage());
		}

		return groups;
	}

	/**
	 * Retrieves an array of group names for the player excluding any 'default' groups.
	 *
	 * @param playerName String containing the name of the player.
	 * @return An String array containing the group names.
	 */
	@Override
	public String [] getGroupsPure(String playerName)
	{
		List<String> list = Arrays.asList(getGroups(playerName));
		
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();)
		{
			String group = iterator.next();
			if (group.equalsIgnoreCase("default"))
			{
				iterator.remove();
			}
		}
		
		if (list.isEmpty())
		{
			return EMPTY_ARRAY;
		}
		
		return list.toArray(new String[]{});
	}

	/**
	 * Retrieves the primary group for a given player.
	 * Note that for bPermissions, it returns the first group on the player's
	 * group list for their current world, or the default world if they are
	 * offline.
	 *
	 * @param playerName String containing the player's name.
	 * @return empty String if the player does not exist, has no groups, or some
	 *         other error, otherwise a String containing the group's name.
	 */
	@Override
	public String getPrimaryGroup(String playerName)
	{
		String worldName;
		String [] groups;

		Player player = Bukkit.getServer().getPlayerExact(playerName);

		if (player == null)
		{
			worldName = Bukkit.getServer().getWorlds().get(0).getName();
		}
		else
		{
			worldName = player.getWorld().getName();
		}

		try
		{
			groups = ApiLayer.getGroups(worldName, CalculableType.USER, playerName);
		}
		catch(Error e)
		{
			CommunityBridge.log.severe("getPrimaryGroup(): " + e.getMessage());
			return "";
		}

		if (groups == null || groups.length == 0)
		{
			return "";
		}
		else
		{
			return groups[0];
		}
	}

	/**
	 * Checks to see if a player is the member of a group.
	 * Note that it checks the groups for their current world, or it checks the
	 * default world if they are offline.
	 *
	 * @param playerName String containing the name of the player to check.
	 * @param groupName	 String containing the name of the group to check.
	 * @return A boolean value which is true if the player is a member of the group.
	 */
	@Override
	public boolean isMemberOfGroup(String playerName, String groupName)
	{
		String worldName;
		String [] groups;

		Player player = Bukkit.getServer().getPlayerExact(playerName);

		if (player == null)
		{
			worldName = Bukkit.getServer().getWorlds().get(0).getName();
		}
		else
		{
			worldName = player.getWorld().getName();
		}

		try
		{
			groups = ApiLayer.getGroups(worldName, CalculableType.USER, playerName);
		}
		catch (Error e)
		{
			CommunityBridge.log.severe("isMemberOfGroup():" + e.getMessage());
			return false;
		}

		return Arrays.asList(groups).contains(groupName);
	}

	/**
	 * Determines whether a player has a group has their primary group.
	 *
	 * @param playerName String containing the player's name
	 * @param groupName  String containing the group's name
	 * @return True if the group is the player's primary group.
	 */
	@Override
	public boolean isPrimaryGroup(String playerName, String groupName)
	{
		String primaryGroup = this.getPrimaryGroup(playerName);
		return primaryGroup != null && groupName.equalsIgnoreCase(primaryGroup);
	}

	/**
	 * Removes a player from a group.
	 *
	 * @param playerName String containing the name of the player.
	 * @param groupName  String containing the name of the group.
	 * @return True if the removal succeeded, false if it failed for any reason.
	 */
	@Override
	public boolean removeFromGroup(String playerName, String groupName)
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

		try
		{
			ApiLayer.removeGroup(worldName, CalculableType.USER, playerName, groupName);
		}
		catch (Error e)
		{
			CommunityBridge.log.severe("removeFromGroup():" + e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * Sets a player's primary group. bPermissions doesn't really have a notion
	 * of a "primary" group. For now, this simply performs an addToGroup.
	 *
	 * @param playerName String containing player's name to set
	 * @param groupName  String containing group name to set player's primary group to.
	 * @param formerGroupName String containing the former group name (will be removed, unless null).
	 * @return true if the set succeeded, false if it failed for any reason.
	 */
	@Override
	public boolean setPrimaryGroup(String playerName, String groupName, String formerGroupName)
	{
		boolean result;
		if (formerGroupName == null)
		{
			result = true;
		}
		else
		{
			result = removeFromGroup(playerName, formerGroupName);
		}
		return result && addToGroup(playerName, groupName);
	}

	/**
	 * Returns true if the permissions system has a concept of a primary group.
	 *
	 * @return boolean true if the permissions system can handle primary groups.
	 */
	@Override
	public boolean supportsPrimaryGroups()
	{
		return false;
	}
}
