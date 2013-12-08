package org.communitybridge.achievement;

import org.bukkit.entity.Player;

/**
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */
public class AchievementPostCount extends Achievement
{
	private int postCount;
	
	@Override
	public boolean playerQualifies(Player player, PlayerAchievementState state)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public int getPostCount()
	{
		return postCount;
	}

	public void setPostCount(int postCount)
	{
		this.postCount = postCount;
	}
	
	public void setPostCount(String postCount)
	{
		this.postCount = Integer.parseInt(postCount);
	}	
}
