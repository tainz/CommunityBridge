package org.ruhlendavis.mc.communitybridge;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
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
		
		return group.getPlayers().contains(playerName);
	}
}
