/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ruhlendavis.mc.communitybridge;

/**
 *  Defines a common interface for accessing permission systems information.
 * 
 * @author Feaelin
 */
public interface PermissionHandler
{
	/**
	 *  Determines whether a player is a member of a group.
	 * 
	 * @param playerName String containing the player's name
	 * @param groupName  String containing the group's name
	 * @return True only if the player is a member of the group.
	 */
	public boolean isMemberOfGroup(String playerName, String groupName);
		
	public String getPrimaryGroup(String playerName);
//	public boolean setPrimaryGroup(String playerName, String groupName);
//	public boolean isPrimaryGroup(String playerName, String groupName);
	
//  public boolean getGroups(String playerName);
//	public boolean addToGroup(String playerName, String groupName);
//	public boolean removeFromGroup(String playerName, String groupName);

}
