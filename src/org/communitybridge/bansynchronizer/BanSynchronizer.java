package org.communitybridge.bansynchronizer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.SQL;
import org.communitybridge.main.Synchronizer;
import org.communitybridge.main.WebApplication;
import org.communitybridge.utility.Log;

public class BanSynchronizer extends Synchronizer
{
	private Log log;
	private SQL sql;
	private File folder;
	private WebApplication webApplication;
	private Configuration configuration;

	public BanSynchronizer(File folder, Configuration configuration, Log log, SQL sql, WebApplication webApplication)
	{
		this.log = log;
		this.folder = folder;
		this.webApplication = webApplication;
		this.configuration = configuration;
		this.sql = sql;
	}

	public void synchronize()
	{
		BanState previous = new BanState(configuration.banSynchronizationMethod, folder, log);
		previous.load();

		BanState current = new BanState(configuration.banSynchronizationMethod, folder, log);
		current.generate();

		if (isValidDirection(configuration.banSynchronizationDirection, "web"))
		{
			synchronizeWebToGame(previous, current);
		}
		else if (isValidDirection(configuration.banSynchronizationDirection, "min"))
		{
			synchronizeGameToWeb(previous, current);
		}

		current.generate();
		try
		{
			current.save();
		}
		catch (IOException exception)
		{
			log.severe("Error while saving ban synchronization state: " + exception.getMessage());
		}
	}

	private void unbanPlayerGame(String playerName)
	{
		OfflinePlayer playerOffline = Bukkit.getOfflinePlayer(playerName);
		playerOffline.setBanned(false);
	}

	private void banPlayerGame(String playerName)
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

	private void unbanPlayerWeb(String userID)
	{
		if (configuration.banSynchronizationMethod.startsWith("tab"))
		{
			unbanPlayerWebTable(userID);
		}
		else if (configuration.banSynchronizationMethod.startsWith("use"))
		{
			unbanPlayerWebUser(userID);
		}
	}

	private void banPlayerWeb(String userID)
	{
		if (configuration.banSynchronizationMethod.startsWith("tab"))
		{
			banPlayerWebTable(userID);
		}
		else if (configuration.banSynchronizationMethod.startsWith("use"))
		{
			banPlayerWebUser(userID);
		}
	}

	private void banPlayerWebUser(String userID)
	{
		String errorBase = "Error during banPlayerWebUser: ";
		String query = "UPDATE `" + configuration.banSynchronizationTableName + "` "
						     + "SET `" + configuration.banSynchronizationBanColumn + "` = '" + configuration.banSynchronizationValueBanned + "' "
								 + "WHERE `" + configuration.banSynchronizationUserIDColumn + "` = '" + userID + "'";

		try
		{
			sql.updateQuery(query);
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
	}

	private void unbanPlayerWebUser(String userID)
	{
		String errorBase = "Error during unbanPlayerWebUser: ";
		String query = "UPDATE `" + configuration.banSynchronizationTableName + "` "
						     + "SET `" + configuration.banSynchronizationBanColumn + "` = '" + configuration.banSynchronizationValueNotBanned + "' "
								 + "WHERE `" + configuration.banSynchronizationUserIDColumn + "` = '" + userID + "'";

		try
		{
			sql.updateQuery(query);
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
	}

	private void banPlayerWebTable(String userID)
	{
		String errorBase = "Error during banPlayerWebTable: ";
		String columns = "`" + configuration.banSynchronizationUserIDColumn + "`, ";
		String values = userID + ", ";

		if (!configuration.banSynchronizationReasonColumn.isEmpty())
		{
			columns = columns + "`" + configuration.banSynchronizationReasonColumn + "`, ";
			values = values + "'banned via minecraft server', ";
		}
		if (!configuration.banSynchronizationStartTimeColumn.isEmpty())
		{
			columns = columns + "`" + configuration.banSynchronizationStartTimeColumn + "`, ";
			values = values + (System.currentTimeMillis() / 1000) + ", ";
		}
		if (!configuration.banSynchronizationEndTimeColumn.isEmpty())
		{
			columns = columns + "`" + configuration.banSynchronizationEndTimeColumn + "`, ";
			values = values + "2147483647, ";
		}
		if (!configuration.banSynchronizationBanGroupIDColumn.isEmpty() && !configuration.banSynchronizationBanGroupID.isEmpty())
		{
			columns = columns + "`" + configuration.banSynchronizationBanGroupIDColumn + "`, ";
			values = values + "'" + configuration.banSynchronizationBanGroupID + "', ";
		}

		columns = columns.substring(0, columns.length() - 2);
		values = values.substring(0, values.length() - 2);
		String query = "INSERT INTO `" + configuration.banSynchronizationTableName + "` (" + columns + ") " + "VALUES (" + values + ")";

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

	private void unbanPlayerWebTable(String userID)
	{
		String errorBase = "Error during unbanPlayerWebTable: ";
		String query = "DELETE FROM `" + configuration.banSynchronizationTableName
							+ "`  WHERE `" + configuration.banSynchronizationUserIDColumn	+ "` = '" + userID + "'";

		try
		{
			sql.deleteQuery(query);
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
	}

	private void synchronizeWebToGame(BanState previous, BanState current)
	{
		for (String userID : previous.getWebBannedUserIDs())
		{
			if (!current.getWebBannedUserIDs().contains(userID))
			{
				unbanPlayerGame(webApplication.getPlayerName(userID));
			}
		}

		for (String userID : current.getWebBannedUserIDs())
		{
			if (!previous.getWebBannedUserIDs().contains(userID))
			{
				banPlayerGame(webApplication.getPlayerName(userID));
			}
		}
	}

	private void synchronizeGameToWeb(BanState previous, BanState current)
	{
		for (String playerName : previous.getGameBannedPlayerNames())
		{
			if (!current.getGameBannedPlayerNames().contains(playerName))
			{
				unbanPlayerWeb(webApplication.getUserID(playerName));
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
}
