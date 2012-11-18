package org.ruhlendavis.mc.communitybridge;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Implements the PermissionHandler interface for PermissionsEx.
 * 
 * @author Feaelin
 */
public class PermissionHandlerPermissionsBukkit implements PermissionHandler
{
	private static PermissionsPlugin permissions;

	/**
	 * 
	 * @throws IllegalStateException When PermissionsBukkit is not present or not enabled.
	 */
	public PermissionHandlerPermissionsBukkit() throws IllegalStateException
	{
		if (permissions == null)
		{
			Plugin plugin;
			plugin = Bukkit.getServer().getPluginManager().getPlugin("PermissionsBukkit");
			if (plugin != null && plugin.isEnabled())
			{
				permissions = (PermissionsPlugin) plugin;
			}
			else
			{
				throw new IllegalStateException("PermissionsBukkit is either not present or not enabled.");
			}
		}
	}
	
	/**
	 * This is here to simplify unit testing. Bypasses the normal constructor so
	 * unit tests can mock objects as appropriate.
	 * 
	 * @param pIn PermissionPlugin object
	 * @throws IllegalStateException Doesn't actually throw, but required for signature matching.
	 */
	
	public PermissionHandlerPermissionsBukkit(PermissionsPlugin pIn) throws IllegalStateException
	{
		permissions = pIn;
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
		return Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
						                                  "permissions player addgroup "
						                                  + playerName + " " + groupName);
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
		List<String> groupNames = new ArrayList<String>();
		
		for (Group group : permissions.getAllGroups())
		{
			groupNames.add(group.getName());
		}

		return groupNames.toArray(new String[0]);
	}

	/**
	 * Retrieves the player's primary group.
	 * 
	 * @param playerName String containing name of the player to look up.
	 * @return String containing the name of the player's primary group or null if the player is not found or does not have a primary group.
	 */
	@Override
	public String getPrimaryGroup(String playerName)
	{
		if (permissions.getPlayerInfo(playerName) == null
		 || permissions.getPlayerInfo(playerName).getGroups() == null
		 || permissions.getPlayerInfo(playerName).getGroups().isEmpty())
		{
			return null;
		}
		
		return permissions.getPlayerInfo(playerName).getGroups().get(0).getName();
	}
	
 /**
	 * Asks permissions system if a player is the member of a given group.
	 * 
	 * @param groupName String containing name of group to check
	 * @param player    String containing name of player to check 
	 * @return boolean which is true if the the player is a member of the group
	 */
	@Override
	public boolean isMemberOfGroup(String playerName, String groupName)
	{
		Group group = permissions.getGroup(groupName);
		
		if (group == null)
		{
			return false;
		}
		
		return group.getPlayers().contains(playerName.toLowerCase());
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

	/**
	 * Removes a player from a group.
	 * 
	 * @param playerName String containing the name of the player.
	 * @param groupName  String containing the name of the group.
	 * @return True if the removal succeeded, false if it failed for any reason.
	 */
	@Override
	public boolean removeFromGroup(String playerName, String groupName)
	{
		return Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
						                                  "permissions player removegroup "
						                                + playerName + " " + groupName);
	}

	/**
	 * Sets a player's primary group. PermissionsBukkit doesn't have a primary group, so this calls AddToGroup.
	 * 
	 * @param playerName String containing player's name to set
	 * @param groupName  String containing group name to set player's primary group to.
	 * @return true if the set succeeded, false if it failed for any reason.
	 */
	// TODO: Revise this so we can ensure that the 'primary group' is group 0.
	@Override
	public boolean setPrimaryGroup(String playerName, String groupName)
	{
		return addToGroup(playerName, groupName);
	}
}
