package org.ruhlendavis.mc.communitybridge;

import net.netmanagers.community.Main;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Implements the PermissionHandler interface for PermissionsEx.
 * 
 * @author Feaelin
 */
public class PermissionHandlerPEX implements PermissionHandler
{
	public PermissionHandlerPEX()
	{}
	
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
		try
		{
			return PermissionsEx.getUser(playerName).inGroup(groupName);
		}
		catch (Error e)
		{
			Main.log.severe(e.getMessage());
		}
		
		return false;
	}
}
