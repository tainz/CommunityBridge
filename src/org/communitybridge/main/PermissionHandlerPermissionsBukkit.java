package org.communitybridge.main;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Implements the PermissionHandler interface for PermissionsEx.
 *
 * @author Feaelin
 */
public class PermissionHandlerPermissionsBukkit extends PermissionHandler
{
	private static PermissionsPlugin permissions;

	/**
	 *
	 * @throws IllegalStateException When PermissionsBukkit is not present or not enabled.
	 */
	public PermissionHandlerPermissionsBukkit() throws IllegalStateException
	{
		if (permissions == null)
		{
			Plugin plugin;
			plugin = Bukkit.getServer().getPluginManager().getPlugin("PermissionsBukkit");
			if (plugin != null && plugin.isEnabled())
			{
				permissions = (PermissionsPlugin) plugin;
			}
			else
			{
				throw new IllegalStateException("PermissionsBukkit is either not present or not enabled.");
			}
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
		return Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
						                                  "permissions player addgroup "
						                                  + playerName + " " + groupName);
	}

	/**
	 * Retrieves an array of group names for the player.
	 *
	 * @param playerName String containing the name of the player.
	 * @return An String array containing the group names.
	 */
	@Override
	public String[] getGroups(String playerName)
	{
		List<String> groupNames = new ArrayList<String>();

		for (Group group : permissions.getAllGroups())
		{
			groupNames.add(group.getName());
		}

		return groupNames.toArray(new String[0]);
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
		return getGroups(playerName);
	}

	/**
	 * Retrieves the player's primary group.
	 *
	 * @param playerName String containing name of the player to look up.
	 * @return empty String if the player does not exist, has no groups, or some
	 *         other error, otherwise a String containing the group's name.
	 */
	@Override
	public String getPrimaryGroup(String playerName)
	{
		if (permissions.getPlayerInfo(playerName) == null
		 || permissions.getPlayerInfo(playerName).getGroups() == null
		 || permissions.getPlayerInfo(playerName).getGroups().isEmpty())
		{
			return "";
		}
		String group = permissions.getPlayerInfo(playerName).getGroups().get(0).getName();
		if (group == null)
		{
			return "";
		}
		else
		{
			return group;
		}
	}

 /**
	 * Asks permissions system if a player is the member of a given group.
	 *
	 * @param groupName String containing name of group to check
	 * @param player    String containing name of player to check
	 * @return boolean which is true if the the player is a member of the group
	 */
	@Override
	public boolean isMemberOfGroup(String playerName, String groupName)
	{
		Group group = permissions.getGroup(groupName);

		if (group == null)
		{
			return false;
		}

		return group.getPlayers().contains(playerName.toLowerCase());
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
		return Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
						                                  "permissions player removegroup "
						                                + playerName + " " + groupName);
	}

	/**
	 * Sets a player's primary group. PermissionsBukkit doesn't have a primary group, so this calls AddToGroup.
	 *
	 * @param playerName String containing player's name to set
	 * @param groupName  String containing group name to set player's primary group to.
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
