package org.communitybridge.achievement;

import org.bukkit.entity.Player;
import org.communitybridge.main.CommunityBridge;

public class AchievementAvatar extends Achievement
{
	@Override
	public boolean playerQualifies(Player player, PlayerAchievementState state)
	{
		return CommunityBridge.config.avatarEnabled
				&& CommunityBridge.webapp.playerHasAvatar(player.getName())
				&& state.getAvatarAchievements() < limit
				&& canRewardAllItemRewards(player);
	}
	
	@Override
	public void rewardPlayer(Player player, PlayerAchievementState state)
	{
		super.rewardPlayer(player, state);
		state.avatarIncrement();
	}
}
