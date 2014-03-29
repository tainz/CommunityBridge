package org.communitybridge.permissionhandlers;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.CalculableType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.communitybridge.main.CommunityBridge;

/**
 * Implements the PermissionHandler interface for bPermissions
 * Notes about bPermissions
 * 2013-May-03: bP does not support the notion of a primary group.
 *
 * @author Feaelin
 */
public class PermissionHandlerBPermissions extends PermissionHandler
{
	public PermissionHandlerBPermissions() throws IllegalStateException
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("bPermissions");

		validate(plugin, "bPermissions");
	}

	@Override
	public boolean addToGroup(String playerName, String groupName)
	{
		try
		{
			ApiLayer.addGroup(determineWorld(playerName), CalculableType.USER, playerName, groupName);
		}
		catch (Error e)
		{
			CommunityBridge.log.severe("addToGroup(): " + e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public String [] getGroups(String playerName)
	{
		String [] groups = {};

		try
		{
			groups = ApiLayer.getGroups(determineWorld(playerName), CalculableType.USER, playerName);
		}
		catch (Error e)
		{
			CommunityBridge.log.severe("getGroups(): " + e.getMessage());
		}

		return groups;
	}

	@Override
	public String [] getGroupsPure(String playerName)
	{
		List<String> groupList = Arrays.asList(getGroups(playerName));
		List<String> finalList = new ArrayList<String>();
		
		for (String group : groupList)
		{
			if (!group.equalsIgnoreCase("default"))
			{
				finalList.add(group);
			}
		}
				
		if (finalList.isEmpty())
		{
			return EMPTY_ARRAY;
		}
		
		return finalList.toArray(new String[]{});
	}

	/**
	 * Note that for bPermissions, it returns the first group on the player's
	 * group list for their current world, or the default world if they are
	 * offline.
	 */
	@Override
	public String getPrimaryGroup(String playerName)
	{
		String [] groups;

		try
		{
			groups = ApiLayer.getGroups(determineWorld(playerName), CalculableType.USER, playerName);
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
	 */
	@Override
	public boolean isMemberOfGroup(String playerName, String groupName)
	{
		String [] groups;

		try
		{
			groups = ApiLayer.getGroups(determineWorld(playerName), CalculableType.USER, playerName);
		}
		catch (Error e)
		{
			CommunityBridge.log.severe("isMemberOfGroup():" + e.getMessage());
			return false;
		}

		return Arrays.asList(groups).contains(groupName);
	}

	@Override
	public boolean isPrimaryGroup(String playerName, String groupName)
	{
		String primaryGroup = this.getPrimaryGroup(playerName);
		return primaryGroup != null && groupName.equalsIgnoreCase(primaryGroup);
	}

	@Override
	public boolean removeFromGroup(String playerName, String groupName)
	{
		try
		{
			ApiLayer.removeGroup(determineWorld(playerName), CalculableType.USER, playerName, groupName);
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

	@Override
	public boolean supportsPrimaryGroups()
	{
		return false;
	}
}
