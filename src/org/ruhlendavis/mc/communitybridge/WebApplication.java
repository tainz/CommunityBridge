package org.ruhlendavis.mc.communitybridge;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.netmanagers.api.SQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ruhlendavis.mc.utility.Log;
import org.ruhlendavis.utility.StringUtilities;

/**
 * Class representing the interface to the web application.
 *
 * @author Feaelin (Iain E. Davis) <iain@ruhlendavis.org>
 */
public class WebApplication
{
	private CommunityBridge plugin;
	private Configuration config;
	private Log log;
	private SQL sql;
	private int maxPlayers;

	private Map<String, String> playerUserIDs = new HashMap();

	public WebApplication(CommunityBridge plugin, Configuration config, Log log, SQL sql)
	{
		this.config = config;
		this.plugin = plugin;
		this.log = log;
		setSQL(sql);
		this.maxPlayers = Bukkit.getMaxPlayers();
	}

	/**
	 * Returns a given player's web application user ID.
	 *
	 * @param String containing the player's name.
	 * @return String containing the player's  web application user ID.
	 */
	public String getUserID(String playerName)
	{
		if (playerUserIDs.containsKey(playerName))
		{}
		else
		{
			loadUserIDfromDatabase(playerName);
		}
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
				avatar = result.getString(config.requireAvatarAvatarColumn);
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
		final String errorBase = "Error during WebApplication.getUserPostCount(): ";
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
				return result.getInt(config.requirePostsPostCountColumn);
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
	 * Retrieves a player's primary group ID from the web application database.
	 *
	 * @param String player name to retrieve.
	 * @return String containing the group ID or null if there was an error or it doesn't exist.
	 */
	public String getUserPrimaryGroupID(String playerName)
	{
		if (config.webappPrimaryGroupEnabled)
		{}
		else
		{
			return "";
		}

		final String errorBase = "Error during WebApplication.getUserPrimaryGroupID(): ";
		String query;

		if (config.webappPrimaryGroupUsesKey)
		{
			query = "SELECT `" + config.webappPrimaryGroupGroupIDColumn + "` "
						+ "FROM `" + config.webappPrimaryGroupTable + "` "
						+ "WHERE `" + config.webappPrimaryGroupUserIDColumn + "` = '" + getUserID(playerName) + "' "
						+ "AND `" + config.webappPrimaryGroupKeyColumn + "` = '" + config.webappPrimaryGroupKeyName + "' ";
		}
		else
		{
			query = "SELECT `" + config.webappPrimaryGroupGroupIDColumn + "` "
						+ "FROM `" + config.webappPrimaryGroupTable + "` "
						+ "WHERE `" + config.webappPrimaryGroupUserIDColumn + "` = '" + getUserID(playerName) + "'";
		}

		log.finest(query);

		try
		{
			ResultSet result = sql.sqlQuery(query);

			if (result.next())
			{
				return result.getString(config.webappPrimaryGroupGroupIDColumn);
			}
			else
			{
				return "";
			}
		}
		catch (SQLException error)
		{
			log.severe(errorBase + error.getMessage());
			return "";
		}
		catch (MalformedURLException error)
		{
			log.severe(errorBase + error.getMessage());
			return "";
		}
		catch (InstantiationException error)
		{
			log.severe(errorBase + error.getMessage());
			return "";
		}
		catch (IllegalAccessException error)
		{
			log.severe(errorBase + error.getMessage());
			return "";
		}
	}

	public List getUserGroupIDs(String playerName)
	{
		if (config.webappSecondaryGroupEnabled)
		{}
		else
		{
			return null;
		}

		if (config.webappSecondaryGroupStorageMethod.toLowerCase().startsWith("sin"))
		{
			return getUserGroupIDsSingleColumn(playerName);
		}
		else if (config.webappSecondaryGroupStorageMethod.toLowerCase().startsWith("jun"))

		{
			return getUserGroupIDsJunction(playerName);
		}
		else if (config.webappSecondaryGroupStorageMethod.toLowerCase().startsWith("key"))

		{
			return getUserGroupIDsKeyValue(playerName);
		}
		log.severe("Invalid storage method for secondary groups.");
		return null;
	}

	private List getUserGroupIDsSingleColumn(String playerName)
	{
		final String errorBase = "Error during WebApplication.getUserGroupIDsSingleColumn(): ";
		String query;

		query = "SELECT `" + config.webappSecondaryGroupGroupIDColumn + "` "
					+ "FROM `" + config.webappSecondaryGroupTable + "` "
					+ "WHERE `" + config.webappSecondaryGroupUserIDColumn + "` = '" + getUserID(playerName) + "' ";

		log.finest(query);

		try
		{
			ResultSet result = sql.sqlQuery(query);

			if (result.next())
			{
				return new ArrayList(Arrays.asList(result.getString(config.webappSecondaryGroupGroupIDColumn).split(config.webappSecondaryGroupGroupIDDelimiter)));
			}
			else
			{
				return null;
			}
		}
		catch (SQLException error)
		{
			log.severe(errorBase + error.getMessage());
			return null;
		}
		catch (MalformedURLException error)
		{
			log.severe(errorBase + error.getMessage());
			return null;
		}
		catch (InstantiationException error)
		{
			log.severe(errorBase + error.getMessage());
			return null;
		}
		catch (IllegalAccessException error)
		{
			log.severe(errorBase + error.getMessage());
			return null;
		}
	}

	private List getUserGroupIDsJunction(String playerName)
	{
		final String errorBase = "Error during WebApplication.getUserGroupIDsJunction(): ";
		String query;

		query = "SELECT `" + config.webappSecondaryGroupGroupIDColumn + "` "
					+ "FROM `" + config.webappSecondaryGroupTable + "` "
					+ "WHERE `" + config.webappSecondaryGroupUserIDColumn + "` = '" + getUserID(playerName) + "' ";

		log.finest(query);

		try
		{
			ResultSet result = sql.sqlQuery(query);
			List groupIDs = new ArrayList();

			while (result.next())
			{
				groupIDs.add(result.getString(config.webappSecondaryGroupGroupIDColumn));
			}
			return groupIDs;
		}
		catch (SQLException error)
		{
			log.severe(errorBase + error.getMessage());
			return null;
		}
		catch (MalformedURLException error)
		{
			log.severe(errorBase + error.getMessage());
			return null;
		}
		catch (InstantiationException error)
		{
			log.severe(errorBase + error.getMessage());
			return null;
		}
		catch (IllegalAccessException error)
		{
			log.severe(errorBase + error.getMessage());
			return null;
		}
	}

	private List getUserGroupIDsKeyValue(String playerName)
	{
		final String errorBase = "Error during WebApplication.getUserGroupIDsKeyValue(): ";
		String query;

		query = "SELECT `" + config.webappSecondaryGroupGroupIDColumn + "` "
					+ "FROM `" + config.webappSecondaryGroupTable + "` "
					+ "WHERE `" + config.webappSecondaryGroupUserIDColumn + "` = '" + getUserID(playerName) + "' "
					+ "AND `" + config.webappSecondaryGroupKeyColumn + "` = '" + config.webappSecondaryGroupKeyName + "' ";

		log.finest(query);

		try
		{
			ResultSet result = sql.sqlQuery(query);
			List groupIDs = new ArrayList();

			while (result.next())
			{
				groupIDs.add(result.getString(config.webappSecondaryGroupGroupIDColumn));
			}
			return groupIDs;
		}
		catch (SQLException error)
		{
			log.severe(errorBase + error.getMessage());
			return null;
		}
		catch (MalformedURLException error)
		{
			log.severe(errorBase + error.getMessage());
			return null;
		}
		catch (InstantiationException error)
		{
			log.severe(errorBase + error.getMessage());
			return null;
		}
		catch (IllegalAccessException error)
		{
			log.severe(errorBase + error.getMessage());
			return null;
		}
	}

	/**
	 * Returns true if the player is registered on the web application.
	 * @param String The name of the player.
	 * @return boolean True if the player is registered.
	 */
	public boolean isPlayerRegistered(String playerName)
	{
		return !(getUserID(playerName) == null || getUserID(playerName).isEmpty());
	}

	/**
	 * Retrieves user IDs for all connected players, required after a cache
	 * cleanup and after cb reload.
	 */
	public synchronized void loadOnlineUserIDsFromDatabase()
	{
		Player [] players =	Bukkit.getOnlinePlayers();

		for (Player player : players)
		{
			loadUserIDfromDatabase(player.getName());
		}
	}

	/**
	 * Performs the database query that should be done when a player connects.
	 *
	 * @param String containing the player's name.
	 */
	public synchronized void loadUserIDfromDatabase(String playerName)
	{
		if (playerUserIDs.size() >= (maxPlayers * 4))
		{
			playerUserIDs.clear();
			loadOnlineUserIDsFromDatabase();
		}

		final String errorBase = "Error during WebApplication.onPreLogin(): ";
		String query = "SELECT `" + config.linkingTableName + "`.`" + config.linkingUserIDColumn + "` "
								 + "FROM `" + config.linkingTableName + "` ";

		if (config.linkingUsesKey)
		{
			query = query
						+ "WHERE `" + config.linkingKeyColumn + "` = '" + config.linkingKeyName + "' "
						+ "AND `" + config.linkingValueColumn + "` = '" + playerName + "' ";
		}
		else
		{
			query = query	+ "WHERE LOWER(`" + config.linkingPlayerNameColumn + "`) = LOWER('" + playerName + "') ";
		}
		query = query + "ORDER BY `" + config.linkingUserIDColumn + "` DESC";

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
	} // loadUserIDfromDatabase()

	/**
	 * Performs operations when a player joins
	 *
	 * @param String The player who joined.
	 */
	public void onJoin(final Player player)
	{
		if (config.webappPrimaryGroupEnabled || config.webappSecondaryGroupEnabled)
		{
			runGroupSynchronizationTask(player);
		}
		runUpdateStatisticsTask(player, true);
	}

	/**
	 * Performs operations when a player quits.
	 *
	 * @param String containing the player's name.
	 */
	public void onQuit(Player player)
	{
		runUpdateStatisticsTask(player, false);
	}

	/**
	 * If statistics is enabled, this method sets up an update statistics task
	 * for the given player.
	 *
	 * @param String The player's name.
	 */
	private void runGroupSynchronizationTask(final Player player)
	{
		Bukkit.getScheduler().runTaskAsynchronously(plugin,	new Runnable()
		{
			@Override
			public void run()
			{
				synchronizeGroups(player);
			}
		});
	}

	/**
	 * If statistics is enabled, this method sets up an update statistics task
	 * for the given player.
	 *
	 * @param String The player's name.
	 */
	private void runUpdateStatisticsTask(final Player player, final boolean online)
	{
		if (config.statisticsEnabled)
		{
			Bukkit.getScheduler().runTaskAsynchronously(plugin,	new Runnable()
			{
				@Override
				public void run()
				{
					updateStatistics(player, online);
				}
			});
		}
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

	private void setPrimaryGroup(String userID, String groupID)
	{
		String errorBase = "Error during setPrimaryGroup(): ";

		try
		{
			if (config.webappPrimaryGroupUsesKey)
			{
				String query = "UPDATE `" + config.webappPrimaryGroupTable + "` "
								     + "SET `" + config.webappPrimaryGroupGroupIDColumn + "` = '" + groupID + "' "
										 + "WHERE `" + config.webappPrimaryGroupKeyColumn + "` = '" + config.webappPrimaryGroupKeyName + "' "
										 + "AND `" + config.webappPrimaryGroupUserIDColumn + "` = '" + userID + "'";
				log.finest(query);
				sql.updateQuery(query);
			}
			else
			{
				String query = "UPDATE `" + config.webappPrimaryGroupTable + "` "
										 + "SET `" + config.webappPrimaryGroupGroupIDColumn + "` = '" + groupID + "' "
										 + "WHERE `" + config.webappPrimaryGroupUserIDColumn + "` = '" + userID + "' ";
				log.finest(query);
				sql.updateQuery(query);
			}
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
	}

	private void synchronizeGroups(Player player)
	{
		String playerName = player.getName();
		String userID = getUserID(playerName);
		File playerFolder = new File(plugin.getDataFolder(), "Players");
		// 1. Retrieve previous group state for forum groups and permissions groups.
		PlayerGroupState previousState = new PlayerGroupState(playerName, playerFolder);
		previousState.load();

		// 2. Capture current group state
		PlayerGroupState currentState = new PlayerGroupState(playerName, playerFolder);
		currentState.generate();

		if (config.webappPrimaryGroupEnabled)
		{
			if (previousState.webappPrimaryGroupID.equals(currentState.webappPrimaryGroupID))
			{}
			else
			{
				String groupName = config.getGroupNameByGroupID(currentState.webappPrimaryGroupID);
				if (groupName == null)
				{
					// TODO: Issue warning?
				}
				else
				{
					CommunityBridge.permissionHandler.setPrimaryGroup(playerName, groupName);
				}
			}

			if (previousState.permissionsSystemPrimaryGroupName.equals(currentState.permissionsSystemPrimaryGroupName))
			{}
			else
			{
				String groupID = config.getWebappGroupIDbyGroupName(currentState.permissionsSystemPrimaryGroupName);

				if (groupID == null)
				{
					// TODO: Issue warning?
				}
				else
				{
					setPrimaryGroup(userID, groupID);
				}
			}
		}

		if (config.webappSecondaryGroupEnabled)
		{
			for(String groupName : previousState.permissionsSystemGroupNames)
			{
				if (currentState.permissionsSystemGroupNames.contains(groupName))
				{}
				else
				{
					removeGroup(userID, groupName);
				}
			}

			for (String groupName : currentState.permissionsSystemGroupNames)
			{
				if (previousState.permissionsSystemGroupNames.contains(groupName))
				{}
				else
				{
					addGroup(userID, groupName, currentState.webappGroupIDs.size());
				}
			}

			for(String groupID : previousState.webappGroupIDs)
			{
				if (currentState.webappGroupIDs.contains(groupID))
				{}
				else
				{
					String groupName = config.getGroupNameByGroupID(groupID);
					CommunityBridge.permissionHandler.removeFromGroup(playerName, groupName);
				}
			}

			for(String groupID : currentState.webappGroupIDs)
			{
				if (previousState.webappGroupIDs.contains(groupID))
				{}
				else
				{
					String groupName = config.getGroupNameByGroupID(groupID);
					CommunityBridge.permissionHandler.addToGroup(playerName, groupName);
				}
			}
		}

		// 5. Save newly created state
		currentState.generate();
		try
		{
			currentState.save();
		}
		catch (IOException error)
		{
			log.severe("Error when saving group state for player " + playerName + ": " + error.getMessage());
		}
	}

	/**
	 * Handles adding a group to the user's group list on the web application.
	 *
	 * @param String Name from permissions system of group added.
	 */
	private void addGroup(String userID, String groupName, int currentGroupCount)
	{
		String groupID = config.getWebappGroupIDbyGroupName(groupName);
		String errorBase = "Error during addGroup(): ";

		try
		{
			if (config.webappSecondaryGroupStorageMethod.toLowerCase().startsWith("sin"))
			{
				if (currentGroupCount > 0)
				{
					groupID = config.webappSecondaryGroupGroupIDDelimiter + groupID;
				}
				String query = "UPDATE `" + config.webappSecondaryGroupTable + "` "
										 + "SET `" + config.webappSecondaryGroupGroupIDColumn + "` = CONCAT(`" + config.webappSecondaryGroupGroupIDColumn + "`, '" + groupID + "') "
										 + "WHERE `" + config.webappSecondaryGroupUserIDColumn + "` = '" + userID + "'";
				log.finest(query);
				sql.updateQuery(query);
			}
			else if (config.webappSecondaryGroupStorageMethod.toLowerCase().startsWith("jun"))
			{
				String query = "INSERT INTO `" + config.webappSecondaryGroupTable + "` "
										 + "(`" + config.webappSecondaryGroupUserIDColumn + "`, `" + config.webappSecondaryGroupGroupIDColumn + "`) "
										 + "VALUES ('" + userID + "', '" + groupID +"')";
				log.finest(query);
				sql.insertQuery(query);
			}
			else if (config.webappSecondaryGroupStorageMethod.toLowerCase().startsWith("key"))
			{
				String query = "INSERT INTO `" + config.webappSecondaryGroupTable + "` "
										 + "(`" + config.webappPrimaryGroupKeyColumn + "`, `" + config.webappSecondaryGroupGroupIDColumn + "`) "
										 + "VALUES ('" + config.webappSecondaryGroupKeyName + "', '" + groupID + "'";
				log.finest(query);
				sql.insertQuery(query);
			}
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
	}

	/**
	 * Handles removing a group from the user's group list on the web application.
	 *
	 * @param String Name from permissions system of group to remove.
	 */
	private void removeGroup(String userID, String groupName)
	{
		String groupID = config.getWebappGroupIDbyGroupName(groupName);
		String errorBase = "Error during addGroup(): ";

		try
		{
			if (config.webappSecondaryGroupStorageMethod.toLowerCase().startsWith("sin"))
			{
				String groupIDs;
				String query = "SELECT `" + config.webappSecondaryGroupGroupIDColumn + "` "
										 + "FROM `" + config.webappSecondaryGroupTable + "` "
										 + "WHERE `" + config.webappSecondaryGroupUserIDColumn + "` = '" + userID + "'";
				log.finest(query);
				ResultSet result = sql.sqlQuery(query);

				if (result.next())
				{
					groupIDs = result.getString(config.webappSecondaryGroupGroupIDColumn);
					List<String> groupIDsAsList = Arrays.asList(groupIDs.split("\\" + config.webappSecondaryGroupGroupIDDelimiter));
					groupIDsAsList.remove(groupID);
					groupIDs = StringUtilities.joinStrings(groupIDsAsList, config.webappSecondaryGroupGroupIDDelimiter);
					query = "UPDATE `" + config.webappSecondaryGroupTable + "` "
								+ " SET `" + config.webappSecondaryGroupGroupIDColumn + "` = '" + groupIDs + "'";
					log.finest(query);
					sql.updateQuery(query);
				}
			}
			else if (config.webappSecondaryGroupStorageMethod.toLowerCase().startsWith("jun"))
			{
				String query = "DELETE FROM `" + config.webappSecondaryGroupTable + "` "
									   + "WHERE `" + config.webappSecondaryGroupUserIDColumn + "` = '" + userID + "' "
										 + "AND `" + config.webappSecondaryGroupGroupIDColumn + "` = '" + groupID + "' ";
				log.finest(query);
				sql.deleteQuery(query);
			}
			else if (config.webappSecondaryGroupStorageMethod.toLowerCase().startsWith("key"))
			{
				String query = "DELETE FROM `" + config.webappSecondaryGroupTable + "` "
									   + "WHERE `" + config.webappSecondaryGroupKeyColumn + "` = '" + config.webappSecondaryGroupKeyName + "' "
										 + "AND `" + config.webappSecondaryGroupGroupIDColumn + "` = '" + groupID + "' ";
				log.finest(query);
				sql.deleteQuery(query);
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
	}

	/**
	 * Update the player's statistical information on the forum.
	 *
	 * @param String Name of player to update
	 * @param boolean Set to true if the player is currently online
	 */
	private void updateStatistics(Player player, boolean online)
	{
		String query;
		ResultSet result;
		String playerName = player.getName();
		String userID = getUserID(playerName);

		int previousLastOnline = 0;
		int previousGameTime = 0;

		// If gametime is enabled, it depends on lastonline. Also, we need to
		// retrieve previously recorded lastonline time and the previously
		// recorded gametime to compute the new gametime.
		if (config.gametimeEnabled)
		{
			if (config.statisticsUsesKey)
			{
				query = "SELECT `" + config.statisticsKeyColumn +  "`, `" + config.statisticsValueColumn
							+ " FROM `" + config.statisticsTableName + "`"
							+ " WHERE `" + config.statisticsUserIDColumn + "` = '" + userID + "'";
				try
				{
					result = sql.sqlQuery(query);
					while (result.next())
					{
						String key = result.getString(config.statisticsKeyColumn);
						if (key.equalsIgnoreCase(config.lastonlineColumnOrKey))
						{
							previousLastOnline = result.getInt(config.statisticsValueColumn);
						}
						else if (key.equalsIgnoreCase(config.gametimeColumnOrKey))
						{
							previousGameTime = result.getInt(config.statisticsValueColumn);
						}
					}
				}
				catch (SQLException error)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + error.getMessage());
				}
				catch (MalformedURLException error)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + error.getMessage());
				}
				catch (InstantiationException error)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + error.getMessage());
				}
				catch (IllegalAccessException error)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + error.getMessage());
				}
			}
			else
			{
				query = "SELECT `" + config.lastonlineColumnOrKey + "`, `" + config.gametimeColumnOrKey + "`"
							+ " FROM `" + config.statisticsTableName + "`"
							+ " WHERE `" + config.statisticsUserIDColumn + "` = '" + userID + "'";
				try
				{
					result = sql.sqlQuery(query);

					if (result.next())
					{
						previousLastOnline = result.getInt(config.lastonlineColumnOrKey);
						previousGameTime = result.getInt(config.gametimeColumnOrKey);
					}
				}
				catch (SQLException error)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + error.getMessage());
				}
				catch (MalformedURLException error)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + error.getMessage());
				}
				catch (InstantiationException error)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + error.getMessage());
				}
				catch (IllegalAccessException error)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + error.getMessage());
				}
			}
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss a");

		String onlineStatus;
		if (online)
		{
			onlineStatus = config.onlineStatusValueOnline;
		}
		else
		{
			onlineStatus = config.onlineStatusValueOffline;
		}

		// last online
		int lastonlineTime = (int) (System.currentTimeMillis() / 1000L);
		String lastonlineTimeFormatted = dateFormat.format(new Date());

		// game time (time played)
		int gametime = 0;
		if (previousLastOnline > 0)
		{
			gametime = previousGameTime + (lastonlineTime - previousLastOnline);
		}
		String gametimeFormatted = StringUtilities.timeElapsedtoString (gametime);

		int level = player.getLevel();
		int totalxp = player.getTotalExperience();
		float currentxp = player.getExp();
		String currentxpFormatted = ((int)(currentxp * 100)) + "%";

		int health = player.getHealth();
		int lifeticks = player.getTicksLived();
		String lifeticksFormatted = StringUtilities.timeElapsedtoString((int)(lifeticks / 20));

		double wallet = 0.0;
		if (config.walletEnabled)
		{
			wallet = CommunityBridge.economy.getBalance(playerName);
		}

		if (config.statisticsUsesKey)
		{
			updateStatisticsKeyStyle(userID, onlineStatus, lastonlineTime, lastonlineTimeFormatted, gametime, gametimeFormatted, level, totalxp, currentxp, currentxpFormatted, health, lifeticks, lifeticksFormatted, wallet);
		}
		else
		{
			updateStatisticsKeylessStyle(userID, onlineStatus, lastonlineTime, lastonlineTimeFormatted, gametime, gametimeFormatted, level, totalxp, currentxp, currentxpFormatted, health, lifeticks, lifeticksFormatted, wallet);
		}
	}

	/**
	 * Called by updateStatistics() to update a statistics table that uses Key-Value Pairs.
	 *
	 * @param String Player's forum user ID.
	 * @param String Set to the appropriate value representing player's online status.
	 * @param int systime value for the last time the player was last online
	 * @param String A formatted version of the systime value of when the player was last online.
	 * @param int Amount of time the player has played in seconds.
	 * @param String Amount of time the player has played formatted nicely.
	 * @param int Level of the player
	 * @param int Total amount of XP the player currently has.
	 * @param float Amount of progress the player has towards the next level as a percentage.
	 * @param String Readable version of the percentage the player has towards the next level.
	 * @param int Player's current health level.
	 * @param int Amount of time played since last death, in ticks.
	 * @param String Formatted amount of time played since last death.
	 * @param double Current balance of the player.
	 */
	private void updateStatisticsKeyStyle(String userID, String onlineStatus, int lastonlineTime, String lastonlineFormattedTime, int gameTime, String gameTimeFormatted, int level, int totalxp, float currentxp, String currentxpFormatted, int health, int lifeticks, String lifeticksFormatted, double wallet)
	{
		List<String> fields = new ArrayList();
		String query = "UPDATE `" + config.statisticsTableName + "` "
								 + "SET ";

		/* To collapse multiple MySQL queries into one query, we're using the
		 * MySQL CASE operator. Recommended reading:
		 * http://www.karlrixon.co.uk/writing/update-multiple-rows-with-different-values-and-a-single-sql-query/
		 * Prototype:
		 * UPDATE tablename
		 * SET valueColumn = CASE keycolumn
		 *                   WHEN keyname THEN keyvalue
		 *                   WHEN keyname THEN keyvalue
		 *                   END
		 * WHERE useridcolumn = userid;
		 */
		query = query + "`" + config.statisticsValueColumn + "` = CASE " + "`" + config.statisticsKeyColumn + "` ";
		if (config.onlineStatusEnabled)
		{
			fields.add("WHEN '" + config.onlineStatusColumnOrKey + "' THEN '" + onlineStatus + "' ");
		}
		if (config.lastonlineEnabled)
		{
			fields.add("WHEN '" + config.lastonlineColumnOrKey + "' THEN '" + lastonlineTime + "' ");
			if (config.lastonlineFormattedColumnOrKey.isEmpty())
			{}
			else
			{
				fields.add("WHEN '" + config.lastonlineFormattedColumnOrKey + "' THEN '" + lastonlineFormattedTime + "' ");
			}
		}
		// Gametime actually relies on the prior lastonlineTime...
		if (config.gametimeEnabled && config.lastonlineEnabled)
		{
			fields.add("WHEN '" + config.gametimeColumnOrKey + "' THEN '" + gameTime + "' ");
			if (config.gametimeFormattedColumnOrKey.isEmpty())
			{}
			else
			{
				fields.add("WHEN '" + config.gametimeFormattedColumnOrKey + "' THEN '" + gameTimeFormatted + "' ");
			}
		}
		if (config.levelEnabled)
		{
			fields.add("WHEN '" + config.levelColumnOrKey + "' THEN '" + level + "' ");
		}
		if (config.totalxpEnabled)
		{
			fields.add("WHEN '" + config.levelColumnOrKey + "' THEN '" + totalxp + "' ");
		}
		if (config.currentxpEnabled)
		{
			fields.add("WHEN '" + config.levelColumnOrKey + "' THEN '" + currentxp + "' ");
			if (config.currentxpFormattedColumnOrKey.isEmpty())
			{}
			else
			{
				fields.add("WHEN '" + config.currentxpFormattedColumnOrKey + "' THEN '" + currentxpFormatted + "' ");
			}
		}
		if (config.healthEnabled)
		{
			fields.add("WHEN '" + config.healthColumnOrKey + "' THEN '" + health + "' ");
		}
		if (config.lifeticksEnabled)
		{
			fields.add("WHEN '" + config.lifeticksColumnOrKey + "' THEN '" + lifeticks + "' ");
			if (config.lifeticksFormattedColumnOrKey.isEmpty())
			{}
			else
			{
				fields.add("WHEN '" + config.lifeticksFormattedColumnOrKey + "' THEN '" + lifeticksFormatted + "' ");
			}
		}
		if (config.walletEnabled)
		{
			fields.add("WHEN '" + config.walletColumnOrKey + "' THEN '" + wallet + "' ");
		}
		query = query + StringUtilities.joinStrings(fields, " ");
		query = query + "END";
		query = query + " WHERE `" + config.statisticsUserIDColumn + "` = '" + userID + "'";

		String errorBase = "Error during updateStatisticsKeyStyle(): ";

		log.finest(query);
		try
		{
			sql.updateQuery(query);
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
	}

	/**
	 * Called by updateStatistics when updating a table that columns (instead of keyvalue pairs).
	 *
	 * @param String Player's forum user ID.
	 * @param String Set to the appropriate value representing player's online status.
	 * @param int systime value for the last time the player was last online
	 * @param String A formatted version of the systime value of when the player was last online.
	 * @param int Amount of time the player has played in seconds.
	 * @param String Amount of time the player has played formatted nicely.
	 * @param int Level of the player
	 * @param int Total amount of XP the player currently has.
	 * @param float Amount of progress the player has towards the next level as a percentage.
	 * @param String Readable version of the percentage the player has towards the next level.
	 * @param int Player's current health level.
	 * @param int Amount of time played since last death, in ticks.
	 * @param String Formatted amount of time played since last death.
	 * @param double Current balance of the player.
	 */
	private void updateStatisticsKeylessStyle(String userID, String onlineStatus, int lastonlineTime, String lastonlineTimeFormatted, int gametime, String gametimeFormatted, int level, int totalxp, float currentxp, String currentxpFormatted, int health, int lifeticks, String lifeticksFormatted, double wallet)
	{
		String query;
		List<String> fields = new ArrayList();
		query = "UPDATE `" + config.statisticsTableName + "` "
					+ "SET ";

		if (config.onlineStatusEnabled)
		{
			fields.add("`" + config.onlineStatusColumnOrKey + "` = '" + onlineStatus +  "'");
		}

		if (config.lastonlineEnabled)
		{
			fields.add("`" + config.lastonlineColumnOrKey + "` = '" + lastonlineTime + "'");
			if (config.lastonlineFormattedColumnOrKey.isEmpty())
			{}
			else
			{
				fields.add("`" + config.lastonlineFormattedColumnOrKey + "` = '" + lastonlineTimeFormatted + "'");
			}
		}

		if (config.gametimeEnabled)
		{
			fields.add("`" + config.gametimeColumnOrKey + "` = '" + gametime + "'");
			if (config.gametimeFormattedColumnOrKey.isEmpty())
			{}
			else
			{
				fields.add("`" + config.gametimeFormattedColumnOrKey + "` = '" + gametimeFormatted + "'");
			}
		}

		if (config.levelEnabled)
		{
			fields.add("`" + config.levelColumnOrKey + "` = '" + level + "'");
		}

		if (config.totalxpEnabled)
		{
			fields.add("`" + config.totalxpColumnOrKey + "` = '" + totalxp + "'");
		}

		if (config.currentxpEnabled)
		{
			fields.add("`" + config.currentxpColumnOrKey + "` = '" + currentxp + "'");
			if (config.currentxpFormattedColumnOrKey.isEmpty())
			{}
			else
			{
				fields.add("`" + config.currentxpFormattedColumnOrKey + "` = '" + currentxpFormatted + "'");
			}
		}

		if (config.healthEnabled)
		{
			fields.add("`" + config.healthColumnOrKey + "` = '" + health + "'");
		}

		if (config.lifeticksEnabled)
		{
			fields.add("`" + config.lifeticksColumnOrKey + "` = '" + lifeticks + "'");
			if (config.lifeticksFormattedColumnOrKey.isEmpty())
			{}
			else
			{
				fields.add("`" + config.lifeticksFormattedColumnOrKey + "` = '" + lifeticksFormatted + "'");
			}
		}

		if (config.walletEnabled)
		{
			fields.add("`" + config.walletColumnOrKey + "` = '" + wallet + "'");
		}

		query = query + StringUtilities.joinStrings(fields, ", ") + " WHERE `" + config.statisticsUserIDColumn + "` = '" + userID + "'";

		String errorBase = "Error during updateStatisticsKeylessStyle(): ";

		log.finest(query);
		try
		{
			sql.updateQuery(query);
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
	}
} // WebApplication class
