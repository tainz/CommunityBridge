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
import org.communitybridge.main.CommunityBridge;
import org.communitybridge.utility.Log;

public class BanState
{
	private Log log;	
	private File dataFile;
	private String storageMethod;
	private List<String> gameBannedPlayerNames = new ArrayList<String>();
	private List<String> webBannedUserIDs = new ArrayList<String>();
	
	public BanState(Log log, File dataFolder, String storageMethod)
	{
		this.dataFile = new File(dataFolder, "banstate.yml");
		this.log = log;
		this.storageMethod = storageMethod;
	}
		
	public void generate()
	{
		collectGameBans();
		collectWebBans();
	}
	
	public void load()
	{
		webBannedUserIDs.clear();
		gameBannedPlayerNames.clear();
		if (dataFile.exists())
		{
			FileConfiguration banData = YamlConfiguration.loadConfiguration(dataFile);
			webBannedUserIDs = banData.getStringList("banned-user-ids");
			gameBannedPlayerNames = banData.getStringList("banned-player-names");
		}
	}
	
	public void save() throws IOException
	{
		FileConfiguration banData = new YamlConfiguration();
		banData.set("banned-user-ids", webBannedUserIDs);
		banData.set("banned-player-names", gameBannedPlayerNames);
		banData.save(dataFile);
	}

	private void collectGameBans()
	{
		gameBannedPlayerNames.clear();
		for (OfflinePlayer player : Bukkit.getServer().getBannedPlayers())
		{
			gameBannedPlayerNames.add(player.getName());
		}
	}

	private void collectWebBans()
	{
		webBannedUserIDs.clear();
		if (storageMethod.startsWith("tab"))
		{
			collectWebBansTableMethod();
		}
		else if (storageMethod.startsWith("use"))
		{
			collectWebBansUserMethod();
		}
	}

	private void collectWebBansTableMethod()
	{
		String errorBase = "Error in collectWebBans: ";
		String query = "SELECT * FROM `" + CommunityBridge.config.banSynchronizationTableName + "`";
		
		try
		{
			ResultSet result = CommunityBridge.sql.sqlQuery(query);
			while(result.next())
			{
				webBannedUserIDs.add(result.getString(CommunityBridge.config.banSynchronizationUserIDColumn));
			}
		}
		catch (MalformedURLException exception)
		{
			log.severe(errorBase + exception.getMessage());
		}
		catch (InstantiationException exception)
		{
			log.severe(errorBase + exception.getMessage());
		}
		catch (IllegalAccessException exception)
		{
			log.severe(errorBase + exception.getMessage());
		}
		catch (SQLException exception)
		{
			log.severe(errorBase + exception.getMessage());
		}
	}

	public List<String> getGameBannedPlayerNames()
	{
		return gameBannedPlayerNames;
	}

	public void setGameBannedPlayerNames(List<String> gameBannedPlayerNames)
	{
		this.gameBannedPlayerNames = gameBannedPlayerNames;
	}

	public List<String> getWebBannedUserIDs()
	{
		return webBannedUserIDs;
	}

	public void setWebBannedUserIDs(List<String> webBannedUserIDs)
	{
		this.webBannedUserIDs = webBannedUserIDs;
	}

	private void collectWebBansUserMethod()
	{
		String errorBase = "Error in collectWebBansUser: ";
		String query = "SELECT * FROM `" + CommunityBridge.config.banSynchronizationTableName + "`";
		
		try
		{
			ResultSet result = CommunityBridge.sql.sqlQuery(query);
			while(result.next())
			{
				if (result.getString(CommunityBridge.config.banSynchronizationBanColumn).equals(CommunityBridge.config.banSynchronizationValueBanned))
				{
					webBannedUserIDs.add(result.getString(CommunityBridge.config.banSynchronizationUserIDColumn));
				}
			}
		}
		catch (MalformedURLException exception)
		{
			log.severe(errorBase + exception.getMessage());
		}
		catch (InstantiationException exception)
		{
			log.severe(errorBase + exception.getMessage());
		}
		catch (IllegalAccessException exception)
		{
			log.severe(errorBase + exception.getMessage());
		}
		catch (SQLException exception)
		{
			log.severe(errorBase + exception.getMessage());
		}
	}
}
