package org.communitybridge.permissionhandlers;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

public class PermissionHandlerZPermissions extends PermissionHandler
{
	 private ZPermissionsService service;

	public PermissionHandlerZPermissions() throws IllegalStateException
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("zPermissions");
		validate(plugin, "zPermissions", "1.3");
		service = Bukkit.getServer().getServicesManager().load(ZPermissionsService.class);

		if (service == null)
		{
			throw new IllegalStateException("zPermissions service class load failed.");
		}
	}

	@Override
	public boolean addToGroup(Player player, String groupName)
	{
		return Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group " + groupName + " add " + player.getName());
	}

	@Override
	public List<String> getGroups(Player player)
	{
		return new ArrayList<String>(service.getPlayerGroups(player.getUniqueId()));
	}

	@Override
	public List<String> getGroupsPure(Player player)
	{
		return getGroups(player);
	}

	@Override
	public String getPrimaryGroup(Player player)
	{
		List<String> groups = getGroups(player);

		return groups.isEmpty() ? "" : groups.get(0);
	}

	@Override
	public boolean isMemberOfGroup(Player player, String groupName)
	{
		List<String> groups = getGroups(player);

		return groups.contains(groupName);
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
		return Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group " + groupName + " remove " + player.getName());
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
