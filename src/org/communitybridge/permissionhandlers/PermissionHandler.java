package org.communitybridge.permissionhandlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class PermissionHandler
{
	protected final String NOT_FOUND = " not found.";
	protected final String NOT_ENABLED = " is not enabled.";

	String[] EMPTY_ARRAY = new String[0];

	public abstract boolean addToGroup(Player player, String groupName);
	public abstract String [] getGroups(Player player);
	public abstract String [] getGroupsPure(Player player);
	public abstract String getPrimaryGroup(Player player);
	public abstract boolean isMemberOfGroup(Player player, String groupName);
	public abstract boolean isPrimaryGroup(Player player, String groupName);
	public abstract boolean removeFromGroup(Player player, String groupName);
	public abstract boolean setPrimaryGroup(Player player, String groupName, String formerGroupName);
	public abstract boolean supportsPrimaryGroups();

	public void switchGroup(Player player, String formerGroupName, String newGroupName)
	{
		removeFromGroup(player, formerGroupName);
		addToGroup(player, newGroupName);
	}

	protected void validate(Plugin plugin, String name) throws IllegalStateException
	{
		if (plugin == null)
		{
			throw new IllegalStateException(name + NOT_FOUND);
		}

		if (!plugin.isEnabled())
		{
			throw new IllegalStateException(name + NOT_ENABLED);
		}
	}

	protected String determineWorld(Player player)
	{
		String worldName;
		if (player == null)
		{
			worldName = Bukkit.getServer().getWorlds().get(0).getName();
		}
		else
		{
			worldName = player.getWorld().getName();
		}
		return worldName;
	}
}
