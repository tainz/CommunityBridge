package org.communitybridge.permissionhandlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * 2013-July-06: As vault is a gateway to many permissions systems, most of whom
 *               do not support the notion of a primary group, in effect,
 *							 vault does not support the notion of a primary group.
 */
public class PermissionHandlerVault extends PermissionHandler
{
	private static Permission vault;
	public PermissionHandlerVault() throws IllegalStateException
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Vault");
		validate(plugin, "Vault", "1.2");
		RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
		vault = rsp.getProvider();
	}

	@Override
	public boolean addToGroup(Player player, String groupName)
	{
		return vault.playerAddGroup(determineWorld(player), player.getName(), groupName);
	}

	@Override
	public List<String> getGroups(Player player)
	{
		return new ArrayList(Arrays.asList(vault.getPlayerGroups(determineWorld(player), player.getName())));
	}

	@Override
	public List<String> getGroupsPure(Player player)
	{
		return getGroups(player);
	}

	@Override
	public String getPrimaryGroup(Player player)
	{
		throw new UnsupportedOperationException("Vault does not support primary groups.");
	}

	@Override
	public boolean isMemberOfGroup(Player player, String groupName)
	{
		return vault.playerInGroup(determineWorld(player), player.getName(), groupName);
	}

	@Override
	public boolean isPrimaryGroup(Player player, String groupName)
	{
		throw new UnsupportedOperationException("Vault does not support primary groups.");
	}

	@Override
	public boolean removeFromGroup(Player player, String groupName)
	{
		return vault.playerRemoveGroup(determineWorld(player), player.getName(), groupName);
	}

	@Override
	public boolean setPrimaryGroup(Player player, String groupName, String formerGroupName)
	{
		throw new UnsupportedOperationException("Vault does not support setting a primary group.");
	}

	@Override
	public boolean supportsPrimaryGroups()
	{
		return false;
	}
}
