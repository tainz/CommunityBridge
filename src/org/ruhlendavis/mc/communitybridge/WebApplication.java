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
		this.log = log;
		setSQL(sql);
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
	 * Returns true if the user's avatar column contains data.
	 *
	 * @param String The player's name.
	 * @return boolean True if the user has an avatar.
	 */
	public boolean playerHasAvatar(String playerName)
	{
		final String errorBase = "Error during WebApplication.playerHasAvatar(): ";
		String query;

		query = "SELECT `" + config.requireAvatarTableName + "`.`" + config.requireAvatarAvatarColumn + "` "
					+ "FROM `" + config.requireAvatarTableName + "` "
					+ "WHERE `" + config.requireAvatarUserIDColumn + "` = '" + getUserID(playerName) + "'";

		log.finest(query);

		try
		{
			String avatar = null;
			ResultSet result = sql.sqlQuery(query);

			if (result.next())
			{
				avatar = result.getString(1);
			}

			if (avatar == null || avatar.isEmpty())
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		catch (SQLException error)
		{
			log.severe(errorBase + error.getMessage());
			return false;
		}
		catch (MalformedURLException error)
		{
			log.severe(errorBase + error.getMessage());
			return false;
		}
		catch (InstantiationException error)
		{
			log.severe(errorBase + error.getMessage());
			return false;
		}
		catch (IllegalAccessException error)
		{
			log.severe(errorBase + error.getMessage());
			return false;
		}
	}

	/**
	 * Fetches the user's post count from the web application.
	 *
	 * @param String The player's name.
	 * @return int Number of posts.
	 */
	public int getUserPostCount(String playerName)
	{
		final String errorBase = "Error during WebApplication.playerHasAvatar(): ";
		String query;

		query = "SELECT `" + config.requirePostsTableName + "`.`" + config.requirePostsPostCountColumn + "` "
					+ "FROM `" + config.requirePostsTableName + "` "
					+ "WHERE `" + config.requirePostsUserIDColumn + "` = '" + getUserID(playerName) + "'";

		log.finest(query);

		try
		{
			ResultSet result = sql.sqlQuery(query);

			if (result.next())
			{
				return result.getInt(1);
			}
			else
			{
				return 0;
			}
		}
		catch (SQLException error)
		{
			log.severe(errorBase + error.getMessage());
			return 0;
		}
		catch (MalformedURLException error)
		{
			log.severe(errorBase + error.getMessage());
			return 0;
		}
		catch (InstantiationException error)
		{
			log.severe(errorBase + error.getMessage());
			return 0;
		}
		catch (IllegalAccessException error)
		{
			log.severe(errorBase + error.getMessage());
			return 0;
		}
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
	 * Performs the database query that should be done when a player connects.
	 *
	 * @param String containing the player's name.
	 */
	public void onPreLogin(String playerName)
	{
		final String errorBase = "Error during WebApplication.onPreLogin(): ";
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
	} // onPreLogin()

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

	/**
	 * Sets the SQL object. Typically used during a reload.
	 *
	 * @param SQL SQL object to set.
	 */
	public final void setSQL(SQL sql)
	{
		this.sql = sql;
	}
} // WebApplication class
