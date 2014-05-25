package org.communitybridge.linker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.communitybridge.main.Environment;

public class UserPlayerLinker
{
	private ConcurrentHashMap<String, String> userIDCache = new ConcurrentHashMap<String, String>();
	private Environment environment;
	private UserIDDao userIDDao;
	private int cacheLimit;

	public UserPlayerLinker(Environment environment, int cacheLimit)
	{
		this.cacheLimit = cacheLimit;
		this.environment = environment;
		this.userIDDao = new UserIDDao(environment);
	}

	public void removeUserIDFromCache(String uuid, String name)
	{
		userIDCache.remove(uuid);
		userIDCache.remove(name);
	}

	public String getUUID(String userID)
	{
		for (Map.Entry<String, String> entry : userIDCache.entrySet())
		{
			if (userID.equals(entry.getValue()))
			{
				return entry.getKey();
			}
		}
		return userIDDao.getUUID(userID);
	}

	public String getUserID(String uuid)
	{
		return getUserIDFromCacheOrDatabase(uuid);
	}

	public String getUserID(Player player)
	{
		return getUserID(player.getUniqueId().toString(), player.getName());
	}

	public String getUserID(String uuid, String name)
	{
		String userID = "";
		String linkingMethod = environment.getConfiguration().linkingMethod;

		if (isValidMethod(linkingMethod, "uui"))
		{
			userID = getUserIDFromCacheOrDatabase(uuid);
		}

		if (userID.isEmpty() && isValidMethod(linkingMethod, "nam"))
		{
			userID = getUserIDFromCacheOrDatabase(name);
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
			if (userIDCache.size() == cacheLimit)
			{
				userIDCache.clear();
			}
			userIDCache.put(identifier, userID);
		}
		return userID;
	}

	protected ConcurrentHashMap<String, String> getUserIDCache()
	{
		return userIDCache;
	}
}
