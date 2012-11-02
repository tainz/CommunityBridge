package org.ruhlendavis.mc.communitybridge;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.bpermissions.api.util.CalculableType;
import java.util.Arrays;
import net.netmanagers.community.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Implements the PermissionHandler interface for bPermissions
 * 
 * @author Feaelin
 */
public class PermissionHandlerBPerms implements PermissionHandler
{
	public PermissionHandlerBPerms()
	{}
	
	@Override
	public boolean isMemberOfGroup(String playerName, String groupName)
	{
		try
		{
			// getGroups for bPerms needs a world, but we'll use 'null' and use
			// the default world. For now.
			String [] groups = ApiLayer.getGroups(null, CalculableType.USER, playerName);
		  return Arrays.asList(groups).contains(groupName);
		}
		catch (Error e)
		{
			Main.log.severe(e.getMessage());
		}
		
		return false;
		
	}
}
