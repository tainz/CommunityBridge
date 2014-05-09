package org.communitybridge.linker;

import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.OfflinePlayer;
import org.communitybridge.main.Environment;

public class UserPlayerLinker
{
	private ConcurrentHashMap<String, String> userIDCache = new ConcurrentHashMap<String, String>();
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
			userID = getUserIDFromCacheOrDatabase(player.getPlayer().getUniqueId().toString());
		}

		if (userID.isEmpty() && isValidMethod(linkingMethod, "nam"))
		{
			userID = getUserIDFromCacheOrDatabase(player.getName());
		}
		return userID;
	}

	private boolean isValidMethod(String linkingMethod, String valid)
	{
		return linkingMethod.startsWith(valid) || linkingMethod.startsWith("bot");
	}

	private String getUserIDFromCacheOrDatabase(String identifier)
	{
		String userID = userIDCache.get(identifier);
		if (userID == null)
		{
			userID = userIDDao.getUserID(identifier);
			userIDCache.put(identifier, userID);
		}
		return userID;
	}
}
