package org.ruhlendavis.mc.communitybridge;

/**
 * Implements the PermissionHandler interface for PermissionsEx.
 * 
 * @author Feaelin
 */
public class PermissionHandlerPermissionsBukkit implements PermissionHandler
{
	public PermissionHandlerPermissionsBukkit()
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
		return false;
	}
}
