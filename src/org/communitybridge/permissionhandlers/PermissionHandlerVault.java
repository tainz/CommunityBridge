package org.communitybridge.permissionhandlers;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Implements the PermissionHandler interface for Vault
 * Notes about Vault
 * 2013-July-06: As vault is a gateway to many permissions systems, most of whom
 *               do not support the notion of a primary group, in effect,
 *							 vault does not support the notion of a primary group.
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */
public class PermissionHandlerVault extends PermissionHandler
{
	private static Permission vault;
	public PermissionHandlerVault() throws IllegalStateException
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Vault");
		validateHandler(plugin, "Vault");
		RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
		vault = rsp.getProvider();
	}
	
	@Override
	public boolean addToGroup(String playerName, String groupName)
	{
		return vault.playerAddGroup((String)null, playerName, groupName);
	}

	@Override
	public String[] getGroups(String playerName)
	{
		return vault.getPlayerGroups((String) null, playerName);
	}

	@Override
	public String[] getGroupsPure(String playerName)
	{
		return getGroups(playerName);
	}

	@Override
	public String getPrimaryGroup(String playerName)
	{
		return vault.getPrimaryGroup((String) null, playerName);
	}

	@Override
	public boolean isMemberOfGroup(String playerName, String groupName)
	{
		return vault.playerInGroup((String) null, playerName, groupName);
	}

	@Override
	public boolean isPrimaryGroup(String playerName, String groupName)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean removeFromGroup(String playerName, String groupName)
	{
		return vault.playerRemoveGroup((String) null, playerName, groupName);
	}

	@Override
	public boolean setPrimaryGroup(String playerName, String groupName, String formerGroupName)
	{
		throw new UnsupportedOperationException("Vault does not support setting a primary group.");
	}

	@Override
	public boolean supportsPrimaryGroups()
	{
		return false;
	}
}
