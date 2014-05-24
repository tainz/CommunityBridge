package org.communitybridge.permissionhandlers;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.communitybridge.main.CommunityBridge;
import org.communitybridge.utility.MinecraftUtilities;
import org.communitybridge.utility.StringUtilities;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionHandlerPermissionsEx extends PermissionHandler
{
	public PermissionHandlerPermissionsEx() throws IllegalStateException
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");

		validate(plugin, "PermissionsEx", "1.21.4");
	}

	@Override
	public boolean addToGroup(Player player, String groupName)
	{
		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		PermissionUser user = PermissionsEx.getUser(player);
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

	@Override
	public List<String> getGroups(Player player)
	{
		return PermissionsEx.getUser(player).getParentIdentifiers();
	}

	@Override
	public List<String> getGroupsPure(Player player)
	{
		List<String> groups = getGroups(player);

		if (groups.size() == 1 && groups.get(0).equalsIgnoreCase("default"))
		{
			return new ArrayList<String>();
		}

		return groups;
	}

	@Override
	public String getPrimaryGroup(Player player)
	{
		PermissionUser permissionUser = PermissionsEx.getUser(player);
		List<String> groups = getGroupsPure(player);
		if (permissionUser == null || groups.isEmpty())
		{
			return "";
		}

		return groups.get(0);
	}

	@Override
	public boolean isMemberOfGroup(Player player, String groupName)
	{
		try
		{
			PermissionUser permissionUser = PermissionsEx.getUser(player);

			if (permissionUser == null)
			{
				return false;
			}

			return permissionUser.inGroup(groupName, false);
		}
		catch (Error e)
		{
			CommunityBridge.log.severe(e.getMessage());
		}

		return false;
	}

	@Override
	public boolean isPrimaryGroup(Player player, String groupName)
	{
		String primaryGroup = this.getPrimaryGroup(player);
		return primaryGroup != null && groupName.equalsIgnoreCase(primaryGroup);
	}

	@Override
	public boolean removeFromGroup(Player player, String groupName)
	{
		PermissionsEx.getPermissionManager().getUser(player).removeGroup(groupName);
		return true;
	}

	@Override
	public boolean setPrimaryGroup(Player player, String groupName, String formerGroupName)
	{
		boolean result;
		if (formerGroupName == null)
		{
			result = true;
		}
		else
		{
			result = removeFromGroup(player, formerGroupName);
		}
		return result && addToGroup(player, groupName);
	}

	@Override
	public boolean supportsPrimaryGroups()
	{
		return false;
	}
}
