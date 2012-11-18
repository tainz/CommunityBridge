package org.ruhlendavis.mc.communitybridge;

/**
 *  Defines a common interface for accessing permission systems information.
 *
 * @author Feaelin
 */
public interface PermissionHandler
{
	/**
	 * Adds a player to a group.
	 * 
	 * @param playerName String containing the player's name.
	 * @param groupName  String containing the group's name.
	 * @return True if the add succeeded, false if it failed for any reason.
	 */
	public boolean addToGroup(String playerName, String groupName);

	/**
	 * Retrieves an array of group names for the player.
	 * 
	 * @param playerName String containing the name of the player.
	 * @return An String array containing the group names.
	 */
	public String [] getGroups(String playerName);
	
	/**
	 * Retrieves a player's primary group. For some permission systems, this is
	 * merely the first group on the list.
	 * 
	 * @param playerName String containing the player's name.
	 * @return null if the player does not exist or has no groups, otherwise a
	 *         String containing the group's name.
	 */
	public String getPrimaryGroup(String playerName);
	
	/**
	 *  Determines whether a player is a member of a group.
	 * 
	 * @param playerName String containing the player's name.
	 * @param groupName  String containing the group's name.
	 * @return True only if the player is a member of the group.
	 */
	public boolean isMemberOfGroup(String playerName, String groupName);
	
	/**
	 * Determines whether a player has a group has their primary group.
	 * 
	 * @param playerName String containing the player's name.
	 * @param groupName  String containing the group's name.
	 * @return True if the group is the player's primary group.
	 */
	public boolean isPrimaryGroup(String playerName, String groupName);

	/**
	 * Removes a player from a group.
	 * 
	 * @param playerName String containing the name of the player.
	 * @param groupName  String containing the name of the group.
	 * @return True if the removal succeeded, false if it failed for any reason.
	 */
	public boolean removeFromGroup(String playerName, String groupName);

	/**
	 * Sets a player's primary group. Note that this may not have any meaning
	 * depending on which permissions system is involved.
	 * 
	 * @param playerName String containing player's name.
	 * @param groupName  String containing the group name.
	 * @return true if the set succeeded, false if it failed for any reason.
	 */
	public boolean setPrimaryGroup(String playerName, String groupName);
}
