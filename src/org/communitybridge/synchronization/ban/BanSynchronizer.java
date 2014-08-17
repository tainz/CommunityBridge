package org.communitybridge.synchronization.ban;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.UUID;
import org.bukkit.BanList;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.communitybridge.main.BukkitWrapper;
import org.communitybridge.main.Configuration;
import org.communitybridge.main.Environment;
import org.communitybridge.synchronization.Synchronizer;
import org.communitybridge.utility.Log;

public class BanSynchronizer extends Synchronizer
{
	private Configuration configuration;
	private Log log;

	private BukkitWrapper bukkit;

	public BanSynchronizer(Environment environment)
	{
		super(environment);
		this.configuration = environment.getConfiguration();
		this.log = environment.getLog();
		this.bukkit = new BukkitWrapper();
	}

	public void synchronize()
	{
		BanState previous = new BanState(environment);
		previous.load();

		BanState current = new BanState(environment);
		current.generate();

		if (isValidDirection(configuration.banSynchronizationDirection, "web"))
		{
			synchronizeWebToGame(previous, current);
		}

		if (isValidDirection(configuration.banSynchronizationDirection, "min"))
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

	private void unbanPlayerGame(String uuid)
	{
		OfflinePlayer playerOffline = bukkit.getOfflinePlayer(UUID.fromString(uuid));
		bukkit.getBanList(BanList.Type.NAME).pardon(playerOffline.getName());
	}

	private void banPlayerGame(String uuidString)
	{
		UUID uuid = UUID.fromString(uuidString);

		Player player = bukkit.getPlayer(uuid);
		if (player == null)
		{
			player = bukkit.getOfflinePlayer(uuid).getPlayer();
		}

		bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "banned by CommunityBridge synchronization", null, "");
		player.kickPlayer("Banned via forums.");
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
			environment.getSql().updateQuery(query);
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
			environment.getSql().updateQuery(query);
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
			environment.getSql().insertQuery(query);
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
			environment.getSql().deleteQuery(query);
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
		for (String userID : previous.getBannedUserIDs())
		{
			if (!current.getBannedUserIDs().contains(userID))
			{
				unbanPlayerGame(environment.getUserPlayerLinker().getPlayerName(userID));
			}
		}

		for (String userID : current.getBannedUserIDs())
		{
			if (!previous.getBannedUserIDs().contains(userID))
			{
				banPlayerGame(environment.getUserPlayerLinker().getPlayerName(userID));
			}
		}
	}

	private void synchronizeGameToWeb(BanState previous, BanState current)
	{
		for (String uuid : previous.getBannedUUIDs())
		{
			if (!current.getBannedUUIDs().contains(uuid))
			{
				unbanPlayerWeb(environment.getUserPlayerLinker().getUserID(uuid));
			}
		}

		for (String uuid : current.getBannedUUIDs())
		{
			if (!previous.getBannedUUIDs().contains(uuid))
			{
				banPlayerWeb(environment.getUserPlayerLinker().getUserID(uuid));
			}
		}
	}
}
