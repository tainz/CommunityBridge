package org.communitybridge.bansynchronizer;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.SQL;
import org.communitybridge.main.WebApplication;
import org.communitybridge.utility.Log;

public class BanSynchronizer
{
	private Log log;
	private SQL sql;
	private File dataFolder;
	private WebApplication webApplication;
	private Configuration config;
	
	public BanSynchronizer(Log log, File dataFolder, Configuration config, WebApplication webApplication, SQL sql)
	{
		this.log = log;
		this.dataFolder = dataFolder;
		this.webApplication = webApplication;
		this.config = config;
		this.sql = sql;
	}
	
	public void synchronize()
	{
		BanState previous = new BanState(log, dataFolder, config.banSynchronizationMethod);
		previous.load();
		
		BanState current = new BanState(log, dataFolder, config.banSynchronizationMethod);
		current.generate();
		
		for (String userID : previous.getWebBannedUserIDs())
		{
			if (!current.getWebBannedUserIDs().contains(userID))
			{
				// unBanUserID
			}
		}
		
		for (String userID : current.getWebBannedUserIDs())
		{
			if (!previous.getWebBannedUserIDs().contains(userID))
			{
				banPlayerGame(webApplication.getPlayerName(userID));
			}
		}
		
		for (String playerName : previous.getGameBannedPlayerNames())
		{
			if (!current.getGameBannedPlayerNames().contains(playerName))
			{
				// unBanPlayerName
			}
		}
		
		for (String playerName : current.getGameBannedPlayerNames())
		{
			if (!previous.getGameBannedPlayerNames().contains(playerName))
			{
				banPlayerWeb(webApplication.getUserID(playerName));
			}
		}
	}

	void banPlayerGame(String playerName)
	{
		Player player = Bukkit.getPlayerExact(playerName);
		if (player == null)
		{
			OfflinePlayer playerOffline = Bukkit.getOfflinePlayer(playerName);
			playerOffline.setBanned(true);
		}
		else
		{
			player.setBanned(true);
			player.kickPlayer("Banned via forums.");
		}
	}

	private void banPlayerWeb(String userID)
	{
		if (config.banSynchronizationMethod.startsWith("tab"))
		{
			banPlayerWebTable(userID);
		}
	}
	
	void banPlayerWebTable(String userID)
	{
		String errorBase = "Error during banPlayerWebTable: ";
		String columns = "`" + config.banSynchronizationUserIDColumn + "`, ";
		String values = userID + ", ";
		
		if (!config.banSynchronizationReasonColumn.isEmpty())
		{
			columns = columns + "`" + config.banSynchronizationReasonColumn + "`, ";
			values = values + "'banned via minecraft server', ";
		}
		if (!config.banSynchronizationStartTimeColumn.isEmpty())
		{
			columns = columns + "`" + config.banSynchronizationStartTimeColumn + "`, ";
			values = values + (System.currentTimeMillis() / 1000) + ", ";
		}
		if (!config.banSynchronizationEndTimeColumn.isEmpty())
		{
			columns = columns + "`" + config.banSynchronizationEndTimeColumn + "`, ";
			values = values + "2147483647, ";
		}
		columns = columns.substring(0, columns.length() - 2);
		values = values.substring(0, values.length() - 2);
		String query = "INSERT INTO `" + config.banSynchronizationTableName + "` (" + columns + ") " + "VALUES (" + values + ")";
		
		try
		{
			sql.insertQuery(query);
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

	boolean isPlayerBanned(String userID, WebApplication webApplication)
	{
		String errorBase = "Error during isBanned: ";
		String query = "SELECT * FROM `" + config.banSynchronizationTableName + "` " + "WHERE `" + config.banSynchronizationTableName + "`.`" + config.banSynchronizationUserIDColumn + "` = '" + userID + "'";
		try
		{
			ResultSet result = sql.sqlQuery(query);
			return result != null && result.next();
		}
		catch (MalformedURLException exception)
		{
			log.severe(errorBase + exception.getMessage());
			return false;
		}
		catch (InstantiationException exception)
		{
			log.severe(errorBase + exception.getMessage());
			return false;
		}
		catch (IllegalAccessException exception)
		{
			log.severe(errorBase + exception.getMessage());
			return false;
		}
		catch (SQLException exception)
		{
			log.severe(errorBase + exception.getMessage());
			return false;
		}
	}
}
