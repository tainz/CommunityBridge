package org.communitybridge.achievement;

import org.bukkit.entity.Player;

/**
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */
public class AchievementGroup extends Achievement
{
	private String groupName;
	
	@Override
	public boolean playerQualifies(Player player)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public String getGroupName()
	{
		return groupName;
	}

	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}
	
}
