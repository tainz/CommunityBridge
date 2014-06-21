package org.communitybridge.bansynchronizer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.communitybridge.main.BukkitWrapper;
import org.communitybridge.main.CommunityBridge;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.Environment;
import org.communitybridge.main.SQL;
import org.communitybridge.utility.Log;

public class BanState
{
	private Configuration configuration;
	private Log log;
	private SQL sql;
	private File file;
	private String storageMethod;
	private BukkitWrapper bukkit;
	private List<String> bannedUUIDs = new ArrayList<String>();
	private List<String> bannedUserIDs = new ArrayList<String>();

	public BanState(String storageMethod, Environment environment)
	{
		this.file = new File(environment.getPlugin().getDataFolder(), "banstate.yml");
		this.configuration = environment.getConfiguration();
		this.log = environment.getLog();
		this.sql = environment.getSql();
		this.storageMethod = storageMethod;
		this.bukkit = new BukkitWrapper();
	}

	public void generate()
	{
		collectGameBans();
		collectWebBans();
	}

	public void load()
	{
		bannedUserIDs.clear();
		bannedUUIDs.clear();
		if (file.exists())
		{
			FileConfiguration banData = YamlConfiguration.loadConfiguration(file);
			bannedUserIDs = banData.getStringList("banned-user-ids");
			bannedUUIDs = banData.getStringList("banned-uuids");

			convertNamesIfNeeded(banData);
		}
	}

	public void save() throws IOException
	{
		FileConfiguration banData = new YamlConfiguration();
		banData.set("banned-user-ids", bannedUserIDs);
		banData.set("banned-uuids", bannedUUIDs);
		banData.set("ban-file-version", "2");
		banData.save(file);
	}

	private void collectGameBans()
	{
		bannedUUIDs.clear();
		for (OfflinePlayer player : bukkit.getServer().getBannedPlayers())
		{
			bannedUUIDs.add(player.getPlayer().getUniqueId().toString());
		}
	}

	private void collectWebBans()
	{
		bannedUserIDs.clear();
		if (storageMethod.startsWith("tab"))
		{
			collectWebBansTableMethod();
		}
		else if (storageMethod.startsWith("use"))
		{
			collectWebBansUserMethod();
		}
		else if (storageMethod.startsWith("gro"))
		{
			collectWebBansGroupMethod(configuration.banSynchronizationBanGroup);
		}
	}

	private void collectWebBansTableMethod()
	{
		String exceptionBase = "Exception in collectWebBans: ";
		String query = "SELECT * FROM `" + configuration.banSynchronizationTableName + "`";

		try
		{
			ResultSet result = sql.sqlQuery(query);
			while(result.next())
			{
				bannedUserIDs.add(result.getString(configuration.banSynchronizationUserIDColumn));
			}
		}
		catch (MalformedURLException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
		}
		catch (InstantiationException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
		}
		catch (IllegalAccessException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
		}
		catch (SQLException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
		}
	}

	public List<String> getBannedUUIDs()
	{
		return bannedUUIDs;
	}

	public void setBannedUUIDs(List<String> bannedUUIDs)
	{
		this.bannedUUIDs = bannedUUIDs;
	}

	public List<String> getBannedUserIDs()
	{
		return bannedUserIDs;
	}

	public void setBannedUserIDs(List<String> bannedUserIDs)
	{
		this.bannedUserIDs = bannedUserIDs;
	}

	private void collectWebBansGroupMethod(String groupID)
	{
		CommunityBridge.webapp.getWebGroupDao().getGroupUserIDs(groupID);
	}

	private void collectWebBansUserMethod()
	{
		String exceptionBase = "Exception in collectWebBansUser: ";
		String query = "SELECT * FROM `" + configuration.banSynchronizationTableName + "` "
								 + "WHERE `" + configuration.banSynchronizationBanColumn + "` = '" + configuration.banSynchronizationValueBanned + "'";

		try
		{
			ResultSet result = sql.sqlQuery(query);

			while(result.next())
			{
				bannedUserIDs.add(result.getString(configuration.banSynchronizationUserIDColumn));
			}
		}
		catch (MalformedURLException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
		}
		catch (InstantiationException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
		}
		catch (IllegalAccessException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
		}
		catch (SQLException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
		}
	}

	private void convertNamesIfNeeded(FileConfiguration banData)
	{
		String version = banData.getString("ban-file-version", "");
		if (version.isEmpty())
		{
			List<String> names = banData.getStringList("banned-player-names");
			for (String name : names)
			{
				bannedUUIDs.add(bukkit.getOfflinePlayer(name).getUniqueId().toString());
			}
		}
	}
}
