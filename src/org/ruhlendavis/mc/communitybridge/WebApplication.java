package org.ruhlendavis.mc.communitybridge;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.netmanagers.api.SQL;
import org.ruhlendavis.mc.utility.Log;

/**
 * Class representing the interface to the web application.
 *
 * @author Feaelin (Iain E. Davis) <iain@ruhlendavis.org>
 */
public class WebApplication
{
	private Configuration config;
	private SQL sql;
	private Log log;

	private Map<String, String> playerUserIDs = new HashMap();

	public WebApplication(Configuration config, SQL sql, Log log)
	{
		this.config = config;
		this.sql = sql;
		this.log = log;
	}

	/**
	 * Returns a given player's web application user ID.
	 *
	 * @param String containing the player's name.
	 * @return String containing the player's  web application user ID.
	 */
	public String getUserID(String playerName)
	{
		return playerUserIDs.get(playerName);
	}

	/**
	 * Returns a given player's web application user ID.
	 *
	 * @param String containing the player's name.
	 * @return String containing the player's  web application user ID.
	 */
	public int getUserIDint(String playerName)
	{
		if (playerUserIDs.get(playerName) == null)
		{
			return 0;
		}
		return Integer.parseInt(playerUserIDs.get(playerName));
	}

	public boolean isPlayerRegistered(String playerName)
	{
		return !(getUserID(playerName) == null || getUserID(playerName).isEmpty());
	}

	/**
	 * Performs the database query that should be done when a player joins.
	 *
	 */
	/**
	 * Performs the database query that should be done when a player joins.
	 * @param String containing the player's name.
	 */
	public void onPreLogin(String playerName)
	{
		final String errorBase = "Error during WebApplication.onJoin(): ";
		String query;

		if (config.linkingUsesKey)
		{
			query = "SELECT * FROM `" + config.linkingTableName + "` "
						+ "WHERE `" + config.linkingKeyColumn + "` = '" + config.linkingKeyName + "' "
						+ "AND `" + config.linkingValueColumn + "` = '" + playerName + "' "
						+ "ORDER BY `" + config.linkingUserIDColumn + "` DESC";
		}
		else
		{
			query = "SELECT * FROM `" + config.linkingTableName + "` "
						+ "WHERE LOWER(`" + config.linkingPlayerNameColumn + "`) = LOWER('" + playerName + "') "
						+ "ORDER BY `" + config.linkingUserIDColumn + "` DESC";

		}

		log.finest(query);

		try
		{
			String userID = null;
			ResultSet result = sql.sqlQuery(query);

			if (result.next())
			{
				userID = result.getString(config.linkingUserIDColumn);
			}
			if (userID == null)
			{
				log.finest("User ID for " + playerName + " not found.");
			}
			else
			{
				log.finest("User ID '" + userID + "' associated with " + playerName + ".");
				playerUserIDs.put(playerName, userID);
			}
		}
		catch (SQLException error)
		{
			log.severe(errorBase + error.getMessage());
		}
		catch (MalformedURLException error)
		{
			log.severe(errorBase + error.getMessage());
		}
		catch (InstantiationException error)
		{
			log.severe(errorBase + error.getMessage());
		}
		catch (IllegalAccessException error)
		{
			log.severe(errorBase + error.getMessage());
		}
	} // onJoin()

	/**
	 * Performs operations when a player quits.
	 *
	 * @param String containing the player's name.
	 */
	public void onQuit(String playerName)
	{
		// Only keep user IDs for connected players on hand.
		playerUserIDs.remove(playerName);
	}
} // WebApplication class
