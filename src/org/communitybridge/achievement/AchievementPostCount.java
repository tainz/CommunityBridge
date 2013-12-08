package org.communitybridge.achievement;

import org.bukkit.entity.Player;
import org.communitybridge.main.CommunityBridge;

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
		if (CommunityBridge.config.postCountEnabled)
		{			
			if (CommunityBridge.webapp.getUserPostCount(player.getName()) >= postCount)
			{
				if (state.getPostCountAchievements(Integer.toString(postCount))< limit)
				{
					if (canRewardAllItemRewards(player))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void rewardPlayer(Player player, PlayerAchievementState state)
	{
		super.rewardPlayer(player, state);
		state.postCountIncrement(Integer.toString(postCount));
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
