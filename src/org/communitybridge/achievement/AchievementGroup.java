package org.communitybridge.achievement;

import org.bukkit.entity.Player;
import org.communitybridge.main.CommunityBridge;

/**
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */
public class AchievementGroup extends Achievement
{
	private String groupName;
	
	@Override
	public boolean playerQualifies(Player player, PlayerAchievementState state)
	{
		return CommunityBridge.permissionHandler.isMemberOfGroup(player.getName(), groupName)
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
