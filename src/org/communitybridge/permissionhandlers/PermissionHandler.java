package org.communitybridge.permissionhandlers;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.communitybridge.main.BukkitWrapper;
import org.communitybridge.utility.StringUtilities;

public abstract class PermissionHandler
{
	protected final String NOT_FOUND = " not found.";
	protected final String NOT_ENABLED = " is not enabled.";
	protected final String WRONG_VERSION = " should be at least version ";

	protected BukkitWrapper bukkit = new BukkitWrapper();

	public abstract boolean addToGroup(Player player, String groupName);
	public abstract List<String> getGroups(Player player);
	public abstract List<String> getGroupsPure(Player player);
	public abstract String getPrimaryGroup(Player player);
	public abstract boolean isMemberOfGroup(Player player, String groupName);
	public abstract boolean isPrimaryGroup(Player player, String groupName);
	public abstract boolean removeFromGroup(Player player, String groupName);
	public abstract boolean setPrimaryGroup(Player player, String groupName, String formerGroupName);
	public abstract boolean supportsPrimaryGroups();

	public void switchGroup(Player player, String formerGroupName, String newGroupName)
	{
		if (formerGroupName != null)
		{
			removeFromGroup(player, formerGroupName);
		}
		addToGroup(player, newGroupName);
	}

	protected void validate(Plugin plugin, String name, String version) throws IllegalStateException
	{
		if (plugin == null)
		{
			throw new IllegalStateException(name + NOT_FOUND);
		}

		if (!plugin.isEnabled())
		{
			throw new IllegalStateException(name + NOT_ENABLED);
		}

		if (StringUtilities.compareVersion(plugin.getDescription().getVersion(), version) < 0) {
			throw new IllegalStateException(name + WRONG_VERSION + version);
		}
	}

	protected String determineWorld(Player player)
	{
		String worldName;
		if (player == null)
		{
			worldName = bukkit.getServer().getWorlds().get(0).getName();
		}
		else
		{
			worldName = player.getWorld().getName();
		}
		return worldName;
	}
}
