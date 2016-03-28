package org.communitybridge.achievement;

import org.bukkit.entity.Player;
import org.communitybridge.main.Environment;

public class AchievementGroup extends Achievement
{
	private String groupName;

	public AchievementGroup(Environment environment)
	{
		super(environment);
	}

	@Override
	public boolean playerQualifies(Player player, PlayerAchievementState state)
	{
		return environment.getPermissionHandler().isMemberOfGroup(player, groupName)
				&& state.getGroupAchievement(groupName) < limit
				&& canRewardAllItemRewards(player);
	}

	@Override
	public void rewardPlayer(Player player, PlayerAchievementState state)
	{
		super.rewardPlayer(player, state);
		state.groupIncrement(groupName);
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
