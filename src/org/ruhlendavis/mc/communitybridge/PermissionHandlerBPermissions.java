package org.ruhlendavis.mc.communitybridge;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import java.util.Arrays;
import net.netmanagers.community.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Implements the PermissionHandler interface for bPermissions
 * 
 * @author Feaelin
 */
public class PermissionHandlerBPermissions implements PermissionHandler
{
	public PermissionHandlerBPermissions()
	{
	}

	/**
	 * Checks to see if a player is the member of a group.
	 * 
	 * @param playerName String containing the name of the player to check.
	 * @param groupName	 String containing the name of the group to check.
	 * @return boolean true if the player is a member of the group.
	 */
	@Override
	public boolean isMemberOfGroup(String playerName, String groupName)
	{
		try
		{
			String worldName;
			Player player = Bukkit.getServer().getPlayerExact(playerName);

			if (player == null)
			{
				worldName = Bukkit.getServer().getWorlds().get(0).getName();
			}
			else
			{
				worldName = player.getWorld().getName();
			}
			
			String [] groups = ApiLayer.getGroups(worldName, CalculableType.USER, playerName);
		  return Arrays.asList(groups).contains(groupName);
		}
		catch (Error e)
		{
			Main.log.severe(e.getMessage());
		}
		
		return false;
		
	}
}
