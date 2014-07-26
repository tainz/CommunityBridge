package org.communitybridge.synchronization.ban;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.communitybridge.main.BukkitWrapper;
import org.communitybridge.main.Environment;

public class BanState
{
	private Environment environment;
	private File file;
	private BukkitWrapper bukkit;
	private List<String> bannedUUIDs = new ArrayList<String>();
	private List<String> bannedUserIDs = new ArrayList<String>();

	public BanState(Environment environment)
	{
		this.environment = environment;
		this.file = new File(environment.getPlugin().getDataFolder(), "banstate.yml");
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
		if (environment.getConfiguration().banSynchronizationMethod.startsWith("tab"))
		{
			collectWebBansTableMethod();
		}
		else if (environment.getConfiguration().banSynchronizationMethod.startsWith("use"))
		{
			collectWebBansUserMethod();
		}
		else if (environment.getConfiguration().banSynchronizationMethod.startsWith("gro"))
		{
			collectWebBansGroupMethod(environment.getConfiguration().banSynchronizationBanGroup);
		}
	}

	private void collectWebBansTableMethod()
	{
		String exceptionBase = "Exception in collectWebBans: ";
		String query = "SELECT * FROM `" + environment.getConfiguration().banSynchronizationTableName + "`";

		try
		{
			ResultSet result = environment.getSql().sqlQuery(query);
			while(result.next())
			{
				bannedUserIDs.add(result.getString(environment.getConfiguration().banSynchronizationUserIDColumn));
			}
		}
		catch (MalformedURLException exception)
		{
			environment.getLog().severe(exceptionBase + exception.getMessage());
		}
		catch (InstantiationException exception)
		{
			environment.getLog().severe(exceptionBase + exception.getMessage());
		}
		catch (IllegalAccessException exception)
		{
			environment.getLog().severe(exceptionBase + exception.getMessage());
		}
		catch (SQLException exception)
		{
			environment.getLog().severe(exceptionBase + exception.getMessage());
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
		environment.getWebApplication().getWebGroupDao().getGroupUserIDs(groupID);
	}

	private void collectWebBansUserMethod()
	{
		String exceptionBase = "Exception in collectWebBansUser: ";
		String query = "SELECT * FROM `" + environment.getConfiguration().banSynchronizationTableName + "` "
								 + "WHERE `" + environment.getConfiguration().banSynchronizationBanColumn + "` = '" + environment.getConfiguration().banSynchronizationValueBanned + "'";

		try
		{
			ResultSet result = environment.getSql().sqlQuery(query);

			while(result.next())
			{
				bannedUserIDs.add(result.getString(environment.getConfiguration().banSynchronizationUserIDColumn));
			}
		}
		catch (MalformedURLException exception)
		{
			environment.getLog().severe(exceptionBase + exception.getMessage());
		}
		catch (InstantiationException exception)
		{
			environment.getLog().severe(exceptionBase + exception.getMessage());
		}
		catch (IllegalAccessException exception)
		{
			environment.getLog().severe(exceptionBase + exception.getMessage());
		}
		catch (SQLException exception)
		{
			environment.getLog().severe(exceptionBase + exception.getMessage());
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
