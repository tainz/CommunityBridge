package org.communitybridge.achievement;

import org.bukkit.entity.Player;
import org.communitybridge.main.CommunityBridge;

public class AchievementAvatar extends Achievement
{
	@Override
	public boolean playerQualifies(Player player, PlayerAchievementState state)
	{
		if (CommunityBridge.config.avatarEnabled)
		{			
			if (CommunityBridge.webapp.playerHasAvatar(player.getName()))
			{
				if (state.getAvatarAchievements() < limit)
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
		state.avatarIncrement();
	}
}
