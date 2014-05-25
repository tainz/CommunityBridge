package org.communitybridge.achievement;

import org.bukkit.entity.Player;
import org.communitybridge.main.CommunityBridge;
import org.communitybridge.main.Environment;

/**
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */
public class AchievementPostCount extends Achievement
{
	private int postCount;

	public AchievementPostCount(Environment environment)
	{
		super(environment);
	}

	@Override
	public boolean playerQualifies(Player player, PlayerAchievementState state)
	{
		return environment.getConfiguration().postCountEnabled
				&& CommunityBridge.webapp.getUserPostCount(player) >= postCount
				&& state.getPostCountAchievements(Integer.toString(postCount))< limit
				&& canRewardAllItemRewards(player);
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
