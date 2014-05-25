package org.communitybridge.permissionhandlers;

import java.util.ArrayList;
import java.util.List;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *  2013-May-03: GroupManager has a notion of a primary group (an odd notion)
 *
 */
public class PermissionHandlerGroupManager extends PermissionHandler
{
	private static GroupManager groupManager;

	public PermissionHandlerGroupManager() throws IllegalStateException
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("GroupManager");

		validate(plugin, "GroupManager", "2.13");

		groupManager = (GroupManager)plugin;
	}

	@Override
	public boolean addToGroup(Player player, String groupName)
	{
		OverloadedWorldHolder worldHolder = groupManager.getWorldsHolder().getWorldDataByPlayerName(player.getName());

		if (worldHolder == null)
		{
			return false;
    }

		User user = worldHolder.getUser(player.getName());

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

	@Override
	public List<String> getGroups(Player player)
	{
		OverloadedWorldHolder worldHolder = groupManager.getWorldsHolder().getWorldDataByPlayerName(player.getName());

		if (worldHolder == null)
		{
			return new ArrayList<String>();
    }

		User user = worldHolder.getUser(player.getName());

		if (user == null)
		{
				return new ArrayList<String>();
		}

		return user.subGroupListStringCopy();
	}

	@Override
	public List<String> getGroupsPure(Player player)
	{
		return getGroups(player);
	}

	@Override
	public String getPrimaryGroup(Player player)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(determineWorld(player));

		if (handler == null)
		{
			throw new RuntimeException("isMemberOfGroup(): Failed to obtain a GroupManager permissions handler");
		}

		String group = handler.getGroup(player.getName());

		if (group == null)
		{
			return "";
		}
		else
		{
			return group;
		}
	}

	@Override
	public boolean isMemberOfGroup(Player player, String groupName) throws RuntimeException
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(determineWorld(player));

		if (handler == null)
		{
			throw new RuntimeException("isMemberOfGroup(): Failed to obtain a GroupManager permissions handler");
		}
		else
		{
			return handler.inGroup(player.getName(), groupName);
		}
	}

	@Override
	public boolean isPrimaryGroup(Player player, String groupName)
	{
		return groupName.equalsIgnoreCase(getPrimaryGroup(player));
	}

	@Override
	public boolean removeFromGroup(Player player, String groupName)
	{
		OverloadedWorldHolder worldHolder = groupManager.getWorldsHolder().getWorldDataByPlayerName(player.getName());

		if (worldHolder == null)
		{
			return false;
    }

		User user = worldHolder.getUser(player.getName());

		if (user == null)
		{
				return false;
		}

		if (user.getGroup() != null && user.getGroup().getName().equalsIgnoreCase(groupName))
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
	public boolean setPrimaryGroup(Player player, String groupName, String formerGroupName)
	{
		OverloadedWorldHolder worldHolder = groupManager.getWorldsHolder().getWorldDataByPlayerName(player.getName());

		if (worldHolder == null)
		{
			return false;
    }

		User user = worldHolder.getUser(player.getName());

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

	@Override
	public boolean supportsPrimaryGroups()
	{
		return true;
	}
}
