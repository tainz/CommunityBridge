package org.communitybridge.synchronization;

import java.net.MalformedURLException;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.communitybridge.main.Environment;
import org.communitybridge.synchronization.dao.MoneyDao;

public class MoneySynchronizer extends Synchronizer implements PlayerSynchronizer
{
	private MoneyDao money = new MoneyDao();

	MoneySynchronizer(Environment environment)
	{
		super(environment);
	}

	@Override
	public boolean isActive(Environment environment)
	{
		return environment.getConfiguration().getMoney().isEnabled();
	}

	@Override
	public PlayerState synchronize(Environment environment, Player player, String userId, PlayerState previous, PlayerState current, PlayerState result)
	{
		result = synchronizeGameToWeb(environment, previous, current, result, userId);
		return synchronizeWebToGame(environment.getEconomy(), previous, current, result, player);
	}

	private PlayerState synchronizeGameToWeb(Environment environment, PlayerState previous, PlayerState current, PlayerState result, String userId)
	{
		double change = current.getMinecraftWallet() - previous.getMinecraftWallet();
		double amount = current.getWebApplicationWallet() + change;
		result.setWebApplicationWallet(amount);
		if (change != 0)
		{
			try
			{
				money.setBalance(environment, userId, amount);
			}
			catch (IllegalAccessException exception)
			{
				environment.getLog().severe("Exception updating web application money" + exception.getMessage());
			}
			catch (InstantiationException exception)
			{
				environment.getLog().severe("Exception updating web application money" + exception.getMessage());
			}
			catch (MalformedURLException exception)
			{
				environment.getLog().severe("Exception updating web application money" + exception.getMessage());
			}
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
			economy.withdrawPlayer(player, Math.abs(change));
		}

		return result;
	}
}
