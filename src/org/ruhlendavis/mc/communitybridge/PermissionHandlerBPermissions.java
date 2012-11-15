package org.ruhlendavis.mc.communitybridge;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import java.util.ArrayList;
import java.util.Arrays;
import net.netmanagers.community.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Implements the PermissionHandler interface for bPermissions
 * 
 * @author Feaelin
 */
public class PermissionHandlerBPermissions implements PermissionHandler
{
	/**
	 * Actual constructor.
	 * 
	 * @throws IllegalStateException thrown when bPermissions plugin is not present or disabled.
	 */
	public PermissionHandlerBPermissions() throws IllegalStateException
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("bPermissions");

		if (plugin != null && plugin.isEnabled())
		{}
		else
		{
			throw new IllegalStateException("bPermissions is either not present or not enabled.");
		}
	}

	/**
	 * Dummy constructor for unit testing.
	 * 
	 * @param dummy boolean can be any boolean value
	 * @throws IllegalStateException not actually thrown as this is a dummy method
	 */
	public PermissionHandlerBPermissions(boolean dummy) throws IllegalStateException
	{}

	/**
	 * Retrieves the primary group for a given player.
	 * Note that for bPermissions, it returns the first group on the player's
	 * group list for their current world, or the default world if they are
	 * offline.
	 * 
	 * @param playerName String containing the player's name.
	 * @return String containing the name of the player's primary group.
	 */
	@Override
	public String getPrimaryGroup(String playerName)
	{
		String worldName;
		String [] groups = {};
		
		Player player = Bukkit.getServer().getPlayerExact(playerName);

		if (player == null)
		{
			worldName = Bukkit.getServer().getWorlds().get(0).getName();
		}
		else
		{
			worldName = player.getWorld().getName();
		}

		try
		{
			groups = ApiLayer.getGroups(worldName, CalculableType.USER, playerName);
		}
		catch(Error e)
		{
			Main.log.severe(e.getMessage());
		}
		
		if (groups == null || groups.length == 0)
		{
			return null;
		}
		else
		{
			return groups[0];
		}
	}
	
	/**
	 * Checks to see if a player is the member of a group.
	 * Note that it checks the groups for their current world, or it checks the
	 * default world if they are offline.
	 * 
	 * @param playerName String containing the name of the player to check.
	 * @param groupName	 String containing the name of the group to check.
	 * @return A boolean value which is true if the player is a member of the group.
	 */
	@Override
	public boolean isMemberOfGroup(String playerName, String groupName)
	{
		String worldName;
		String [] groups = {};

		Player player = Bukkit.getServer().getPlayerExact(playerName);

		if (player == null)
		{
			worldName = Bukkit.getServer().getWorlds().get(0).getName();
		}
		else
		{
			worldName = player.getWorld().getName();
		}

		try
		{
			groups = ApiLayer.getGroups(worldName, CalculableType.USER, playerName);
		}
		catch (Error e)
		{
			Main.log.severe(e.getMessage());
		}
		
		return Arrays.asList(groups).contains(groupName);
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
}
