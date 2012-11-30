package org.ruhlendavis.mc.communitybridge;

import net.netmanagers.community.Main;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Implements the PermissionHandler interface for PermissionsEx.
 *
 * @author Feaelin
 */
public class PermissionHandlerPermissionsEx implements PermissionHandler
{
	/**
	 * Actual constructor.
	 *
	 * @throws IllegalStateException when PermissionsEx is not present or disabled.
	 */
	public PermissionHandlerPermissionsEx() throws IllegalStateException
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");

		if (plugin != null && plugin.isEnabled())
		{}
		else
		{
			throw new IllegalStateException("PermissionsEx is either not present or not enabled.");
		}
	}

	/**
	 * Dummy constructor for unit testing purposes.
	 *
	 * @param dummy Any boolean value (not used)
	 * @throws IllegalStateException Not actually thrown as this is a dummy method
	 */
	public PermissionHandlerPermissionsEx(boolean dummy) throws IllegalStateException
	{}

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
		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
		if (group == null || user == null)
		{
			return false;
		}
		else
		{
			user.addGroup(group);
			return true;
		}
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
		return PermissionsEx.getPermissionManager().getUser(playerName).getGroupsNames();
	}

	/**
	 * Retrieves a player's primary group. For PermissionsEx this is merely the
	 * first group on the list.
	 *
	 * @param playerName String containing the player's name
	 * @return null if the player does not exist or has no groups, otherwise a
	 *         String containing the group's name.
	 */
	@Override
	public String getPrimaryGroup(String playerName)
	{
		PermissionUser permissionUser = PermissionsEx.getUser(playerName);
		if (permissionUser == null || permissionUser.getGroupsNames().length == 0)
		{
			return null;
		}

		return permissionUser.getGroupsNames()[0];
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
		try
		{
			PermissionUser permissionUser = PermissionsEx.getUser(playerName);

			if (permissionUser == null)
			{
				return false;
			}

			return permissionUser.inGroup(groupName, false);
		}
		catch (Error e)
		{
			Main.log.severe(e.getMessage());
		}

		return false;
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
		PermissionsEx.getPermissionManager().getUser(playerName).removeGroup(groupName);
		return true;
	}

	/**
	 * Sets a player's primary group. PermissionsEx doesn't have a notion of
	 * a primary group, so for now this simply calls addToGroup()
	 *
	 * @param playerName String containing player's name.
	 * @param groupName  String containing the group name.
	 * @return true if the set succeeded, false if it failed for any reason.
	 */
	// TODO: Work out a way to ensure the group is first on the player's group list
	@Override
	public boolean setPrimaryGroup(String playerName, String groupName)
	{
		return addToGroup(playerName, groupName);
	}
}
