package org.communitybridge.synchronization;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.communitybridge.main.Environment;
import org.communitybridge.main.WebApplication;

public class MoneySynchronizer extends Synchronizer implements PlayerSynchronizer
{
	MoneySynchronizer(Environment environment)
	{
		super(environment);
	}

	@Override
	public boolean isActive(Environment environment)
	{
		return environment.getConfiguration().walletEnabled;
	}

	@Override
	public PlayerState synchronize(Environment environment, Player player, String userId, PlayerState previous, PlayerState current, PlayerState result)
	{
		result = synchronizeGameToWeb(environment.getWebApplication(), previous, current, result, userId);
		return synchronizeWebToGame(environment.getEconomy(), previous, current, result, player);
	}

	private PlayerState synchronizeGameToWeb(WebApplication webApplication, PlayerState previous, PlayerState current, PlayerState result, String userId)
	{
		double change = current.getMinecraftWallet() - previous.getMinecraftWallet();
		double amount = current.getWebApplicationWallet() + change;
		result.setWebApplicationWallet(amount);
		if (change != 0)
		{
			webApplication.setBalance(userId, amount);
		}
		return result;
	}

	private PlayerState synchronizeWebToGame(Economy economy, PlayerState previous, PlayerState current, PlayerState result, Player player)
	{
		double change = current.getWebApplicationWallet() - previous.getWebApplicationWallet();
		result.setMinecraftWallet(current.getMinecraftWallet() + change);
		if (change > 0)
		{
			economy.depositPlayer(player, change);
		}
		else if (change < 0)
		{
			economy.withdrawPlayer(player, change);
		}
		return result;
	}
}
