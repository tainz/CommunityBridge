package org.communitybridge.linker;

import org.bukkit.OfflinePlayer;
import org.communitybridge.main.Environment;

public class UserPlayerLinker
{
	private Environment environment;
	private UserIDDao userIDDao;

	public UserPlayerLinker(Environment environment)
	{
		this.environment = environment;
		this.userIDDao = new UserIDDao(environment);
	}

	public String getUserID(OfflinePlayer player)
	{
		String userID = "";
		String linkingMethod = environment.getConfiguration().linkingMethod;

		if (isValidMethod(linkingMethod, "uui"))
		{
			userID = userIDDao.getUserID(player.getPlayer().getUniqueId().toString());
		}

		if (userID.isEmpty() && isValidMethod(linkingMethod, "nam"))
		{
			userID = userIDDao.getUserID(player.getName());
		}
		return userID;
	}

	private boolean isValidMethod(String linkingMethod, String valid)
	{
		return linkingMethod.startsWith(valid) || linkingMethod.startsWith("bot");
	}
}
