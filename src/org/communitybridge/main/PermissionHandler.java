package org.communitybridge.main;

/**
 *  Defines a common interface for accessing permission systems information.
 *
 * @author Feaelin
 */
public abstract class PermissionHandler
{
	/**
	 * Returned by getGroups or getGroupsPure when there are no groups.
	 */
	String[] EMPTY_ARRAY = new String[0];

	/**
	 * Adds a player to a group.
	 *
	 * @param playerName String containing the player's name.
	 * @param groupName  String containing the group's name.
	 * @return True if the add succeeded, false if it failed for any reason.
	 */
	abstract boolean addToGroup(String playerName, String groupName);

	/**
	 * Retrieves an array of group names for the player.
	 *
	 * @param playerName String containing the name of the player.
	 * @return An String array containing the group names.
	 */
	abstract String [] getGroups(String playerName);

	/**
	 * Retrieves an array of group names for the player excluding any 'default' groups.
	 *
	 * @param playerName String containing the name of the player.
	 * @return An String array containing the group names.
	 */
	abstract String [] getGroupsPure(String playerName);

	/**
	 * Retrieves a player's primary group.
	 *
	 * @param playerName String containing the player's name.
	 * @return empty String if the player does not exist, has no groups, or some
	 *         other error, otherwise a String containing the group's name.
	 */
	abstract String getPrimaryGroup(String playerName);

	/**
	 *  Determines whether a player is a member of a group.
	 *
	 * @param playerName String containing the player's name.
	 * @param groupName  String containing the group's name.
	 * @return True only if the player is a member of the group.
	 */
	abstract boolean isMemberOfGroup(String playerName, String groupName);

	/**
	 * Determines whether a player has a group has their primary group.
	 *
	 * @param playerName String containing the player's name.
	 * @param groupName  String containing the group's name.
	 * @return True if the group is the player's primary group.
	 */
	abstract boolean isPrimaryGroup(String playerName, String groupName);

	/**
	 * Removes a player from a group.
	 *
	 * @param playerName String containing the name of the player.
	 * @param groupName  String containing the name of the group.
	 * @return True if the removal succeeded, false if it failed for any reason.
	 */
	abstract boolean removeFromGroup(String playerName, String groupName);

	/**
	 * Sets a player's primary group. Note that this may not have any meaning
	 * depending on which permissions system is involved.
	 *
	 * @param playerName String containing player's name.
	 * @param groupName  String containing the group name.
	 * @return true if the set succeeded, false if it failed for any reason.
	 */
	abstract boolean setPrimaryGroup(String playerName, String groupName, String formerGroupName);

	/**
	 * Returns true if the permissions system has a concept of a primary group.
	 *
	 * @return boolean true if the permissions system can handle primary groups.
	 */
	abstract boolean supportsPrimaryGroups();

	public void switchGroup(String playerName, String formerGroupName, String newGroupName)
	{
		removeFromGroup(playerName, formerGroupName);
		addToGroup(playerName, newGroupName);
	}
}
