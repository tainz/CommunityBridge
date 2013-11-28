package org.communitybridge.main;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *	Implements the permission handler interface for GroupManager.
 *  Notes for GroupManager:
 *  2013-May-03: GroupManager has a notion of a primary group (an odd notion)
 *
 * @author Feaelin
 */
public class PermissionHandlerGroupManager extends PermissionHandler
{
	private static GroupManager groupManager;

	/**
	 * Setup for the GroupManager Permissions Handler
	 *
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
	 * Adds a player to a group.
	 *
	 * @param playerName String containing the player's name.
	 * @param groupName  String containing the group's name.
	 * @return True if the add succeeded, false if it failed for any reason.
	 */
	@Override
	public boolean addToGroup(String playerName, String groupName)
	{
		OverloadedWorldHolder worldHolder = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);

		if (worldHolder == null)
		{
			return false;
    }

		User user = worldHolder.getUser(playerName);

		if (user == null)
		{
				return false;
		}

		Group group = worldHolder.getGroup(groupName);
    if (group == null)
		{
			return false;
    }

		// If it is a primary group, set as a primary group.
		if (user.getGroup().equals(worldHolder.getDefaultGroup()))
		{
			user.setGroup(group, false);
		}
		else if (group.getInherits().contains(user.getGroup().getName().toLowerCase()))
		{
			user.setGroup(group, false);
		}
		else
		{
			user.addSubGroup(group);
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
	public String[] getGroups(String playerName)
	{
		OverloadedWorldHolder worldHolder = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);

		if (worldHolder == null)
		{
			return EMPTY_ARRAY;
    }

		User user = worldHolder.getUser(playerName);

		if (user == null)
		{
				return EMPTY_ARRAY;
		}

		return user.subGroupListStringCopy().toArray(new String[0]);
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
	 * Retrieves the primary group for a given player.
	 *
	 * @param playerName String containing the player's name.
	 * @return empty String if the player does not exist, has no groups, or some
	 *         other error, otherwise a String containing the group's name.
	 */
	@Override
	public String getPrimaryGroup(String playerName)
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

		String group = handler.getGroup(playerName);

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

	@Override
	public boolean removeFromGroup(String playerName, String groupName)
	{
		OverloadedWorldHolder worldHolder = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);

		if (worldHolder == null)
		{
			return false;
    }

		User user = worldHolder.getUser(playerName);

		if (user == null)
		{
				return false;
		}

		if (user.getGroup().getName().equalsIgnoreCase(groupName))
		{
			user.setGroup(worldHolder.getDefaultGroup(), false);
			return true;
		}
		else
		{
			Group group = worldHolder.getGroup(groupName);
			if (group == null)
			{
				return false;
			}
			else
			{
				return user.removeSubGroup(group);
			}
		}
	}

	@Override
	public boolean setPrimaryGroup(String playerName, String groupName, String formerGroupName)
	{
		OverloadedWorldHolder worldHolder = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);

		if (worldHolder == null)
		{
			return false;
    }

		User user = worldHolder.getUser(playerName);

		if (user == null)
		{
				return false;
		}

		Group group = worldHolder.getGroup(groupName);
    if (group == null)
		{
			return false;
    }

		user.setGroup(group, false);

		return true;
	}

	/**
	 * Returns true if the permissions system has a concept of a primary group.
	 *
	 * @return boolean true if the permissions system can handle primary groups.
	 */
	@Override
	public boolean supportsPrimaryGroups()
	{
		return true;
	}
}
