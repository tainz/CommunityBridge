package org.communitybridge.permissionhandlers;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

/**
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */
public class PermissionHandlerZPermissions extends PermissionHandler
{
	 private ZPermissionsService service;

	/**
	 * Actual constructor.
	 *
	 * @throws IllegalStateException thrown when zPermissions plugin is not present or disabled.
	 */
	public PermissionHandlerZPermissions() throws IllegalStateException
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("zPermissions");
		validateHandler(plugin, "zPermissions");
		service = Bukkit.getServer().getServicesManager().load(ZPermissionsService.class);
		
		if (service == null)
		{
			throw new IllegalStateException("zPermissions service class load failed.");
		}
	}

	@Override
	public boolean addToGroup(String playerName, String groupName)
	{
		return Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group " + groupName + " add " + playerName);
	}

	@Override
	public String[] getGroups(String playerName)
	{
		return service.getPlayerGroups(playerName).toArray(new String[0]);
	}

	@Override
	public String[] getGroupsPure(String playerName)
	{
		return getGroups(playerName);
	}

	@Override
	public String getPrimaryGroup(String playerName)
	{
		return getGroups(playerName)[0];
	}

	@Override
	public boolean isMemberOfGroup(String playerName, String groupName)
	{
		List<String> groups = Arrays.asList(getGroups(playerName));
		
		return groups.contains(groupName);
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
		return Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group " + groupName + " remove " + playerName);
	}

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
