package org.communitybridge.main;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.communitybridge.achievement.Achievement;
import org.communitybridge.achievement.PlayerAchievementState;
import org.communitybridge.bansynchronizer.BanSynchronizer;
import org.communitybridge.dao.JunctionWebGroupDao;
import org.communitybridge.dao.KeyValueWebGroupDao;
import org.communitybridge.dao.MultipleKeyValueWebGroupDao;
import org.communitybridge.dao.SingleWebGroupDao;
import org.communitybridge.dao.WebGroupDao;
import org.communitybridge.utility.Log;
import org.communitybridge.utility.MinecraftUtilities;
import org.communitybridge.utility.StringUtilities;

public class WebApplication
{
	public static final List<String> EMPTY_LIST = new ArrayList<String>();
	public static final String EXCEPTION_MESSAGE_GETSECONDARY = "Exception during WebApplication.getUserSecondaryGroupIDs(): ";
	private final Boolean synchronizationLock = true;
	private CommunityBridge plugin;
	private Configuration config;
	private Log log;
	private SQL sql;
	private BanSynchronizer banSynchronizer;
	private WebGroupDao webGroupDao;
	private int maxPlayers;

	private Map<String, String> playerUserIDs = new HashMap<String, String>();
	private List<Player> playerLocks = new ArrayList<Player>();
	
	public WebApplication(WebGroupDao webGroupDao, Log log)
	{
		this.webGroupDao = webGroupDao;
		this.log = log;
	}
	
	public WebApplication(CommunityBridge plugin, Configuration config, Log log, SQL sql)
	{
		this.config = config;
		this.plugin = plugin;
		this.log = log;
		setSQL(sql);
		maxPlayers = Bukkit.getMaxPlayers();
		configureDao();
		if (config.banSynchronizationEnabled)
		{
			banSynchronizer = new BanSynchronizer(log, plugin.getDataFolder(), config, this, sql);
		}
	}

	/**
	 * Returns a given player's web application user ID.
	 *
	 * @param String containing the player's name.
	 * @return String containing the player's  web application user ID.
	 */
	public String getUserID(String playerName)
	{
		if (!playerUserIDs.containsKey(playerName))
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
		final String exceptionBase = "Exception during WebApplication.playerHasAvatar(): ";
		String query;

		query = "SELECT `" + config.avatarTableName + "`.`" + config.avatarAvatarColumn + "` "
					+ "FROM `" + config.avatarTableName + "` "
					+ "WHERE `" + config.avatarUserIDColumn + "` = '" + getUserID(playerName) + "'";

		try
		{
			String avatar = null;
			ResultSet result = sql.sqlQuery(query);

			if (result.next())
			{
				avatar = result.getString(config.avatarAvatarColumn);
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
		catch (SQLException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
			return false;
		}
		catch (MalformedURLException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
			return false;
		}
		catch (InstantiationException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
			return false;
		}
		catch (IllegalAccessException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
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
		final String exceptionBase = "Exception during WebApplication.getUserPostCount(): ";
		String query;

		query = "SELECT `" + config.postCountTableName + "`.`" + config.postCountPostCountColumn + "` "
					+ "FROM `" + config.postCountTableName + "` "
					+ "WHERE `" + config.postCountUserIDColumn + "` = '" + getUserID(playerName) + "'";

		try
		{
			ResultSet result = sql.sqlQuery(query);

			if (result.next())
			{
				return result.getInt(config.postCountPostCountColumn);
			}
			else
			{
				return 0;
			}
		}
		catch (SQLException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
			return 0;
		}
		catch (MalformedURLException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
			return 0;
		}
		catch (InstantiationException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
			return 0;
		}
		catch (IllegalAccessException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
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
		return webGroupDao.getUserPrimaryGroupID(getUserID(playerName));
	}

	public List<String> getUserSecondaryGroupIDs(String playerName)
	{	
		try
		{
			return webGroupDao.getUserSecondaryGroupIDs(getUserID(playerName));
		}
		catch (SQLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return EMPTY_LIST;
		}
		catch (MalformedURLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return EMPTY_LIST;
		}
		catch (InstantiationException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return EMPTY_LIST;
		}
		catch (IllegalAccessException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETSECONDARY + exception.getMessage());
			return EMPTY_LIST;
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

		final String exceptionBase = "Exception during WebApplication.onPreLogin(): ";
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

		try
		{
			String userID = null;
			ResultSet result = sql.sqlQuery(query);

			if (result != null && result.next())
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
		catch (SQLException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
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
	} // loadUserIDfromDatabase()

	/**
	 * Performs operations when a player joins
	 *
	 * @param String The player who joined.
	 */
	public void onJoin(final Player player)
	{
		if (config.syncDuringJoin)
		{
			runSynchronizePlayer(player, true);
		}
	}

	/**
	 * Performs operations when a player quits.
	 *
	 * @param String containing the player's name.
	 */
	public void onQuit(Player player)
	{
		if (config.syncDuringQuit)
		{
			runSynchronizePlayer(player, false);
		}
	}

	/**
	 * If statistics is enabled, this method sets up an update statistics task
	 * for the given player.
	 *
	 * @param String The player's name.
	 */
	public void runSynchronizePlayer(final Player player, final boolean online)
	{
		MinecraftUtilities.startTask(plugin,
																	new Runnable()
																	{
																		@Override
																		public void run()
																		{
																			synchronizePlayer(player, online);
																		}
																	}
																);
	}

	public void runSynchronizeAll()
	{
		MinecraftUtilities.startTask(plugin,
																	new Runnable()
																	{
																		@Override
																		public void run()
																		{
																			synchronizeAll();
																		}
																	}
																);
	}

	public void synchronizeAll()
	{
		Player[] onlinePlayers = Bukkit.getOnlinePlayers();
		for (Player player : onlinePlayers)
		{
			synchronizePlayer(player, true);
		}
		
		if (config.banSynchronizationEnabled)
		{
			banSynchronizer.synchronize();
		}
	}

	private void synchronizePlayer(Player player, boolean online)
	{
		if (!playerLocks.contains(player))
		{
			synchronized (synchronizationLock) { playerLocks.add(player);}
			if (config.groupSynchronizationActive)
			{
				synchronizeGroups(player);
			}
			if (config.statisticsEnabled)
			{
				updateStatistics(player, online);
			}
			if (config.useAchievements)
			{
				rewardAchievements(player);
			}
			synchronized (synchronizationLock) { playerLocks.remove(player); }
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
		String exceptionBase = "Exception during setPrimaryGroup(): ";

		try
		{
			if (config.webappPrimaryGroupUsesKey)
			{
				String query = "UPDATE `" + config.webappPrimaryGroupTable + "` "
										 + "SET `" + config.webappPrimaryGroupGroupIDColumn + "` = '" + groupID + "' "
										 + "WHERE `" + config.webappPrimaryGroupKeyColumn + "` = '" + config.webappPrimaryGroupKeyName + "' "
										 + "AND `" + config.webappPrimaryGroupUserIDColumn + "` = '" + userID + "'";
				sql.updateQuery(query);
			}
			else
			{
				String query = "UPDATE `" + config.webappPrimaryGroupTable + "` "
										 + "SET `" + config.webappPrimaryGroupGroupIDColumn + "` = '" + groupID + "' "
										 + "WHERE `" + config.webappPrimaryGroupUserIDColumn + "` = '" + userID + "' ";
				sql.updateQuery(query);
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
	}

	private void synchronizeGroups(Player player)
	{
		String playerName = player.getName();
		String direction = config.simpleSynchronizationDirection;
		String userID = getUserID(playerName);

		// This can happen if the player disconnects after synchronization has
		// already begun.
		if (userID == null)
		{
			return;
		}
		
		if (userID.equalsIgnoreCase(config.simpleSynchronizationSuperUserID))
		{
			// If we're configured to have minecraft be 'master' only,
			// we'll do nothing at all with the super-user.
			if (direction.startsWith("min"))
			{
				return;
			}
			
			// Otherwise, we'll temporarily override the direction to be one-way
			// for the super-user.
			direction = "web";					
		}
		
		File playerFolder = new File(plugin.getDataFolder(), "Players");

		PlayerGroupState previous = new PlayerGroupState(playerName, playerFolder);
		previous.load();

		PlayerGroupState current = new PlayerGroupState(playerName, playerFolder);
		current.generate();
		PlayerGroupState result = current.copy();

		if(config.simpleSynchronizationFirstDirection.startsWith("web") && previous.isNewFile) {
			direction = "web";
		}

		if (config.webappPrimaryGroupEnabled)
		{
			synchronizeGroupsPrimary(direction, previous, current, result, playerName, player, userID);
		}
		else
		{
			// With synchronization turned off the currentState should always be the previous state.
			current.permissionsSystemPrimaryGroupName = previous.permissionsSystemPrimaryGroupName;
			current.webappPrimaryGroupID = previous.webappPrimaryGroupID;
		}

		// 4. Synchronize secondary group state
		if (config.webappSecondaryGroupEnabled)
		{
			synchronizeGroupsSecondary(direction, previous, current, result, userID, playerName);
		}
		else
		{
			// With synchronization turned off the currentState should always be the previous state.
			current.permissionsSystemGroupNames = previous.permissionsSystemGroupNames;
			current.webappGroupIDs = previous.webappGroupIDs;
		}
		// 5. Save newly created state
		try
		{
			result.save();
		}
		catch (IOException exception)
		{
			log.severe("Exception when saving group state for player " + playerName + ": " + exception.getMessage());
		}
	}

	/**
	 * Handles adding a group to the user's group list on the web application.
	 *
	 * @param String Name from permissions system of group added.
	 */
	private void addGroup(String userID, String groupID, int currentGroupCount)
	{
		String exceptionBase = "Exception during addGroup(): ";

		try
		{
			if (config.webappSecondaryGroupStorageMethod.startsWith("sin"))
			{
				if (currentGroupCount > 1)
				{
					groupID = config.webappSecondaryGroupGroupIDDelimiter + groupID;
				}
				String query = "UPDATE `" + config.webappSecondaryGroupTable + "` "
										 + "SET `" + config.webappSecondaryGroupGroupIDColumn + "` = CONCAT(`" + config.webappSecondaryGroupGroupIDColumn + "`, '" + groupID + "') "
										 + "WHERE `" + config.webappSecondaryGroupUserIDColumn + "` = '" + userID + "'";
				sql.updateQuery(query);
			}
			else if (config.webappSecondaryGroupStorageMethod.startsWith("key"))
			{
				if (currentGroupCount > 0)
				{
					groupID = config.webappSecondaryGroupGroupIDDelimiter + groupID;
				}
				String query = "UPDATE `" + config.webappSecondaryGroupTable + "` "
										 + "SET `" + config.webappSecondaryGroupGroupIDColumn + "` = CONCAT(`" + config.webappSecondaryGroupGroupIDColumn + "`, '" + groupID + "') "
										 + "WHERE `" + config.webappSecondaryGroupUserIDColumn + "` = '" + userID + "' "
										 + "AND `" + config.webappSecondaryGroupKeyColumn + "` = '" + config.webappSecondaryGroupKeyName + "' ";
				sql.updateQuery(query);
			}
			else if (config.webappSecondaryGroupStorageMethod.startsWith("jun"))
			{
				String query = "INSERT INTO `" + config.webappSecondaryGroupTable + "` "
										 + "(`" + config.webappSecondaryGroupUserIDColumn + "`, `" + config.webappSecondaryGroupGroupIDColumn + "`) "
										 + "VALUES ('" + userID + "', '" + groupID +"')";
				sql.insertQuery(query);
			}
			else if (config.webappSecondaryGroupStorageMethod.startsWith("mul"))
			{
				String query = "INSERT INTO `" + config.webappSecondaryGroupTable + "` "
										 + "(`" + config.webappSecondaryGroupUserIDColumn + "`, `" + config.webappPrimaryGroupKeyColumn + "`, `" + config.webappSecondaryGroupGroupIDColumn + "`) "
										 + "VALUES ('" + userID + "', '" + config.webappSecondaryGroupKeyName + "', '" + groupID + "')";
				sql.insertQuery(query);
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

	/**
	 * Handles removing a group from the user's group list on the web application.
	 *
	 * @param String Name from permissions system of group to remove.
	 */
	private void removeGroup(String userID, String groupName)
	{
		String groupID = config.getWebappGroupIDbyGroupName(groupName);
		String exceptionBase = "Exception during addGroup(): ";

		try
		{
			if (config.webappSecondaryGroupStorageMethod.startsWith("sin"))
			{
				String groupIDs;
				String query = "SELECT `" + config.webappSecondaryGroupGroupIDColumn + "` "
										 + "FROM `" + config.webappSecondaryGroupTable + "` "
										 + "WHERE `" + config.webappSecondaryGroupUserIDColumn + "` = '" + userID + "'";
				ResultSet result = sql.sqlQuery(query);

				if (result.next())
				{
					groupIDs = result.getString(config.webappSecondaryGroupGroupIDColumn);
					List<String> groupIDsAsList = new ArrayList<String>(Arrays.asList(groupIDs.split(config.webappSecondaryGroupGroupIDDelimiter)));
					groupIDsAsList.remove(groupID);
					groupIDs = StringUtilities.joinStrings(groupIDsAsList, config.webappSecondaryGroupGroupIDDelimiter);
					query = "UPDATE `" + config.webappSecondaryGroupTable + "` "
								+ "SET `" + config.webappSecondaryGroupGroupIDColumn + "` = '" + groupIDs + "' "
								+ "WHERE `" + config.webappSecondaryGroupUserIDColumn + "` = '" + userID + "'";
					sql.updateQuery(query);
				}
			}
			else if (config.webappSecondaryGroupStorageMethod.startsWith("key"))
			{
				String groupIDs;
				String query = "SELECT `" + config.webappSecondaryGroupGroupIDColumn + "` "
										 + "FROM `" + config.webappSecondaryGroupTable + "` "
										 + "WHERE `" + config.webappSecondaryGroupUserIDColumn + "` = '" + userID + "' "
										 + "AND `" + config.webappSecondaryGroupKeyColumn + "` = '" + config.webappSecondaryGroupKeyName + "' ";
				ResultSet result = sql.sqlQuery(query);

				if (result.next())
				{
					groupIDs = result.getString(config.webappSecondaryGroupGroupIDColumn);
					List<String> groupIDsAsList = new ArrayList<String>(Arrays.asList(groupIDs.split(config.webappSecondaryGroupGroupIDDelimiter)));
					groupIDsAsList.remove(groupID);
					groupIDs = StringUtilities.joinStrings(groupIDsAsList, config.webappSecondaryGroupGroupIDDelimiter);
					query = "UPDATE `" + config.webappSecondaryGroupTable + "` "
								+ " SET `" + config.webappSecondaryGroupGroupIDColumn + "` = '" + groupIDs + "' "
								+ "WHERE `" + config.webappSecondaryGroupUserIDColumn + "` = '" + userID + "' "
								+ "AND `" + config.webappSecondaryGroupKeyColumn + "` = '" + config.webappSecondaryGroupKeyName + "' ";
					sql.updateQuery(query);
				}
			}
			else if (config.webappSecondaryGroupStorageMethod.startsWith("jun"))
			{
				String query = "DELETE FROM `" + config.webappSecondaryGroupTable + "` "
										 + "WHERE `" + config.webappSecondaryGroupUserIDColumn + "` = '" + userID + "' "
										 + "AND `" + config.webappSecondaryGroupGroupIDColumn + "` = '" + groupID + "' ";
				sql.deleteQuery(query);
			}
			else if (config.webappSecondaryGroupStorageMethod.startsWith("mul"))
			{
				String query = "DELETE FROM `" + config.webappSecondaryGroupTable + "` "
										 + "WHERE `" + config.webappSecondaryGroupKeyColumn + "` = '" + config.webappSecondaryGroupKeyName + "' "
										 + "AND `" + config.webappSecondaryGroupGroupIDColumn + "` = '" + groupID + "' ";
				sql.deleteQuery(query);
			}
		}
		catch (SQLException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
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
	}

	/**
	 * Update the player's statistical information on the forum.
	 *
	 * @param String Name of player to update
	 * @param boolean Set to true if the player is currently online
	 */
	private void updateStatistics(Player player, boolean online)
	{
		PlayerStatistics playerStatistics = new PlayerStatistics(config.dateFormat);
		
		String query;
		ResultSet result;
		
		int previousLastOnline = 0;
		int previousGameTime = 0;

		String playerName = player.getName();
		playerStatistics.setUserID(getUserID(playerName));
		if (playerStatistics.getUserID() == null) 
		{
			return;
		}
		
		// If gametime is enabled, it depends on lastonline. Also, we need to
		// retrieve previously recorded lastonline time and the previously
		// recorded gametime to compute the new gametime.
		if (config.gametimeEnabled)
		{
			if (config.statisticsUsesKey)
			{
				query = "SELECT `" + config.statisticsKeyColumn +  "`, `" + config.statisticsValueColumn + "` "
							+ "FROM `" + config.statisticsTableName + "` "
							+ "WHERE `" + config.statisticsUserIDColumn + "` = '" + playerStatistics.getUserID() + "'";
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
				catch (SQLException exception)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + exception.getMessage());
				}
				catch (MalformedURLException exception)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + exception.getMessage());
				}
				catch (InstantiationException exception)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + exception.getMessage());
				}
				catch (IllegalAccessException exception)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + exception.getMessage());
				}
			}
			else
			{
				query = "SELECT `" + config.lastonlineColumnOrKey + "`, `" + config.gametimeColumnOrKey + "`"
							+ " FROM `" + config.statisticsTableName + "`"
							+ " WHERE `" + config.statisticsUserIDColumn + "` = '" + playerStatistics.getUserID() + "'";
				try
				{
					result = sql.sqlQuery(query);

					if (result.next())
					{
						previousLastOnline = result.getInt(config.lastonlineColumnOrKey);
						previousGameTime = result.getInt(config.gametimeColumnOrKey);
					}
				}
				catch (SQLException exception)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + exception.getMessage());
				}
				catch (MalformedURLException exception)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + exception.getMessage());
				}
				catch (InstantiationException exception)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + exception.getMessage());
				}
				catch (IllegalAccessException exception)
				{
					log.severe("Error in UpdateStatistics() during retrieval: " + exception.getMessage());
				}
			}
		}
		
		if (config.onlineStatusEnabled)
		{
			if (online)
			{
				playerStatistics.setOnlineStatus(config.onlineStatusValueOnline);
			}
			else
			{
				playerStatistics.setOnlineStatus(config.onlineStatusValueOffline);
			}
		}
		
		if (config.lastonlineEnabled)
		{
			playerStatistics.setLastOnlineTime((int) (System.currentTimeMillis() / 1000L));
		}

		if (config.gametimeEnabled && previousLastOnline > 0)
		{
			playerStatistics.setGameTime(previousGameTime + playerStatistics.getLastOnlineTime() - previousLastOnline);
		}
		
		if (config.levelEnabled)
		{
			playerStatistics.setLevel(player.getLevel());
		}
		
		if (config.totalxpEnabled)
		{
			playerStatistics.setTotalXP(player.getTotalExperience());
		}
		
		if (config.currentxpEnabled)
		{
			playerStatistics.setCurrentXP(player.getExp());
		}
		
		if (config.healthEnabled)
		{
			playerStatistics.setHealth((double)player.getHealth());
		}
		
		if (config.lifeticksEnabled)
		{
			playerStatistics.setLifeTicks(player.getTicksLived());
		}

		if (config.walletEnabled)
		{
			playerStatistics.setWallet(CommunityBridge.economy.getBalance(playerName));
		}

		if (config.statisticsUsesKey)
		{
			updateStatisticsKeyStyle(playerStatistics);
		}
		else
		{
			updateStatisticsKeylessStyle(playerStatistics);
		}
	}

	/**
	 * Called by updateStatistics() to update a statistics table that uses Key-Value Pairs.
	 *
	 * @param PlayerStatistics Bean containing the player's statistics
	 */
	private void updateStatisticsKeyStyle(PlayerStatistics playerStatistics)
	{
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
		 * 
		 * insert prototype:
		 * INSERT INTO tableName (user_idcolumn,theme_idcolumn,keycolumn,valuecolumn) VALUES (user_id,theme_id,keyname1,keyvalue1),(user_id,theme_id,keyname2,keyvalue2)
		 */

		List<String> foundFields = new ArrayList<String>();
		String exceptionBase = "Exception during updateStatisticsKeyStyle(): ";
		String insertQuery = "INSERT INTO `" + config.statisticsTableName + "` ("
											 + config.statisticsUserIDColumn + ", "
											 + (config.statisticsUsesInsert && config.statisticsInsertMethod.startsWith("smf") ? config.statisticsThemeIDColumn + ", " : "")
											 + config.statisticsKeyColumn + ", "
											 + config.statisticsValueColumn + ") VALUES ";
		String updateQuery = "UPDATE `" + config.statisticsTableName + "` "
											 + "SET " + "`" + config.statisticsValueColumn
											 + "` = CASE " + "`" + config.statisticsKeyColumn + "` ";
		try
		{
			if (config.statisticsUsesInsert)
			{
				String selectQuery = "SELECT `" + config.statisticsKeyColumn + "` "
													 + " FROM `" + config.statisticsTableName + "` "
													 + " WHERE `" + config.statisticsUserIDColumn + "` = '"
													 + playerStatistics.getUserID() + "'"
													 + (config.statisticsInsertMethod.startsWith("smf") ? " AND `" + config.statisticsThemeIDColumn + "` = '" + config.statisticsThemeID + "'" : "");

				ResultSet result = sql.sqlQuery(selectQuery);
				while (result.next())
				{
					foundFields.add(result.getString(config.statisticsKeyColumn));
				}
			}

			FieldBuilder builder = new FieldBuilder(foundFields);
			
			if (config.onlineStatusEnabled)
			{
				builder.add(playerStatistics.getUserID(), config.onlineStatusColumnOrKey, playerStatistics.getOnlineStatus());
			}
			
			if (config.lastonlineEnabled)
			{
				builder.add(playerStatistics.getUserID(), config.lastonlineColumnOrKey, playerStatistics.getLastOnlineTime());
				if (!config.lastonlineFormattedColumnOrKey.isEmpty())
				{
					builder.add(playerStatistics.getUserID(), config.lastonlineFormattedColumnOrKey, playerStatistics.getLastOnlineTimeFormatted());
				}
			}

			// Gametime actually relies on the prior lastonlineTime...
			if (config.gametimeEnabled && config.lastonlineEnabled)
			{
				builder.add(playerStatistics.getUserID(), config.gametimeColumnOrKey, playerStatistics.getGameTime());
				if (!config.gametimeFormattedColumnOrKey.isEmpty())
				{
					builder.add(playerStatistics.getUserID(), config.gametimeFormattedColumnOrKey, playerStatistics.getGameTimeFormatted());
				}
			}
			
			if (config.levelEnabled)
			{
				builder.add(playerStatistics.getUserID(), config.levelColumnOrKey, playerStatistics.getLevel());
			}
			
			if (config.totalxpEnabled)
			{
				builder.add(playerStatistics.getUserID(), config.totalxpColumnOrKey, playerStatistics.getTotalXP());
			}
			
			if (config.currentxpEnabled)
			{
				builder.add(playerStatistics.getUserID(), config.currentxpColumnOrKey, playerStatistics.getCurrentXP());
				if (!config.currentxpFormattedColumnOrKey.isEmpty())
				{
					builder.add(playerStatistics.getUserID(), config.currentxpFormattedColumnOrKey, playerStatistics.getCurrentXPFormatted());
				}
			}
			
			if (config.healthEnabled)
			{
				builder.add(playerStatistics.getUserID(), config.healthColumnOrKey, (int)playerStatistics.getHealth());
			}
			
			if (config.lifeticksEnabled)
			{
				builder.add(playerStatistics.getUserID(), config.lifeticksColumnOrKey, playerStatistics.getLifeTicks());
				if (!config.lifeticksFormattedColumnOrKey.isEmpty())
				{
					builder.add(playerStatistics.getUserID(), config.lifeticksFormattedColumnOrKey, playerStatistics.getLifeTicksFormatted());
				}
			}
			
			if (config.walletEnabled)
			{
				builder.add(playerStatistics.getUserID(), config.walletColumnOrKey, playerStatistics.getWallet());
			}

			if (builder.insertFields.size() > 0)
			{
				insertQuery = insertQuery + StringUtilities.joinStrings(builder.insertFields, ", ") + ";";
				sql.insertQuery(insertQuery);
			}
			
			if (builder.updateFields.size() > 0)
			{
				updateQuery = updateQuery + StringUtilities.joinStrings(builder.updateFields, " ")
										+ " END"
										+ " WHERE `" + config.statisticsUserIDColumn + "` = '"
										+ playerStatistics.getUserID() + "'"
										+ " AND `" + config.statisticsKeyColumn + "`"
										+ " IN (" + StringUtilities.joinStrings(builder.inFields, ", ") + ");";
				
				sql.updateQuery(updateQuery);
			}
		}
		catch (SQLException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
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
	}

	private void synchronizeGroupsPrimary(String direction, PlayerGroupState previous, PlayerGroupState current, PlayerGroupState result, String playerName, Player player, String userID)
	{
		if (isValidDirection(direction, "web") && !previous.webappPrimaryGroupID.equals(current.webappPrimaryGroupID))
		{
			synchronizeGroupsPrimaryWebToGame(player, previous, current, result);
		}

		if (isValidDirection(direction, "min") && !previous.permissionsSystemPrimaryGroupName.equals(current.permissionsSystemPrimaryGroupName))
		{
			synchronizeGroupsPrimaryGameToWeb(userID, playerName, previous, current, result);
		}
	}

	private boolean isValidDirection(String direction, String validDirection)
	{
		return direction.startsWith("two") || direction.startsWith(validDirection);
	}

	private void synchronizeGroupsSecondary(String direction, PlayerGroupState previous, PlayerGroupState current, PlayerGroupState result, String userID, String playerName)
	{
		if (isValidDirection(direction, "min"))
		{
			synchronizeGroupsSecondaryGameToWeb(userID, previous, current, result);
		}

		if (isValidDirection(direction, "web"))
		{
			synchronizeGroupsSecondaryWebToGame(playerName, previous, current, result);
		}
	}

	private void synchronizeGroupsPrimaryWebToGame(Player player, PlayerGroupState previous, PlayerGroupState current, PlayerGroupState result)
	{
		if (previous.webappPrimaryGroupID.equalsIgnoreCase(current.webappPrimaryGroupID))
		{
			return;
		}

		String formerGroupName = config.getGroupNameByGroupID(previous.webappPrimaryGroupID);
		String newGroupName = config.getGroupNameByGroupID(current.webappPrimaryGroupID);
		String playerName = player.getName();

		if (newGroupName == null)
		{
			log.warning("Not changing permissions group due to permissions system group name lookup failure for web application group ID: " + current.webappPrimaryGroupID + ". Player '" + playerName + "' primary group state unchanged.");
			result.webappPrimaryGroupID = previous.webappPrimaryGroupID;
			return;
		}

		if (config.simpleSynchronizationPrimaryGroupNotify)
		{
			String message = ChatColor.YELLOW + CommunityBridge.config.messages.get("group-synchronization-primary-notify-player");
			message = message.replace("~GROUPNAME~", newGroupName);
			player.sendMessage(message);
		}

		String pseudo = "";
		if (CommunityBridge.permissionHandler.supportsPrimaryGroups())
		{
			CommunityBridge.permissionHandler.setPrimaryGroup(playerName, newGroupName, formerGroupName);
		}
		else
		{
			CommunityBridge.permissionHandler.switchGroup(playerName, formerGroupName, newGroupName);
			pseudo = "pseudo-primary ";
		}
		result.permissionsSystemPrimaryGroupName = newGroupName;
		if (formerGroupName == null)
		{
			log.fine("Placed player '" + playerName + "' in " + pseudo + "permissions group '" + newGroupName + "'.");
		}
		else
		{
			log.fine("Moved player '" + playerName + "' to " + pseudo + "permissions group '" + newGroupName + "' from '" + formerGroupName + "'.");			
		}
	}

	private void synchronizeGroupsPrimaryGameToWeb(String userID, String playerName, PlayerGroupState previous, PlayerGroupState current, PlayerGroupState result)
	{
		String groupID = config.getWebappGroupIDbyGroupName(current.permissionsSystemPrimaryGroupName);

		if (groupID == null)
		{
			log.warning("Not changing web application group due to web application group ID lookup failure for: " + current.permissionsSystemPrimaryGroupName + ". Player '" + playerName + "' primary group state unchanged.");
			result.permissionsSystemPrimaryGroupName = previous.permissionsSystemPrimaryGroupName;
		}
		else
		{
			result.webappPrimaryGroupID = groupID;
			setPrimaryGroup(userID, groupID);
			log.fine("Moved player '" + playerName + "' to web application group ID '" + groupID + "'.");
		}
	}

	private void synchronizeGroupsSecondaryGameToWeb(String userID, PlayerGroupState previous, PlayerGroupState current, PlayerGroupState result)
	{
		for (String groupName : previous.permissionsSystemGroupNames)
		{
			if (!current.permissionsSystemGroupNames.contains(groupName) && !config.simpleSynchronizationGroupsTreatedAsPrimary.contains(groupName))
			{
				String groupID = config.getWebappGroupIDbyGroupName(groupName);
				removeGroup(userID, groupName);
				result.webappGroupIDs.remove(groupID);
			}
		}

		int addedCount = 0;
		for (Iterator<String> iterator = current.permissionsSystemGroupNames.iterator(); iterator.hasNext();)
		{
			String groupName = iterator.next();
			
			if (!previous.permissionsSystemGroupNames.contains(groupName))
			{
				String groupID = config.getWebappGroupIDbyGroupName(groupName);

				// Since the group is not in the mapping, we'll NOT record it as
				// part of the current state. That way, if the group is added to
				// the mapping later, we'll see it as a 'new' group and synchronize.
				if (groupID == null)
				{
					result.permissionsSystemGroupNames.remove(groupName);
				}
				else if (current.webappGroupIDs.contains(groupID))
				{
					log.warning("We thought we needed to add a secondary group ID " + groupID + "...but we didn't?");
				}
				else
				{
					addGroup(userID, groupID, result.webappGroupIDs.size() + addedCount);
					result.webappGroupIDs.add(groupID);
					addedCount++;
				}
			}
		}
	}

	private void synchronizeGroupsSecondaryWebToGame(String playerName, PlayerGroupState previous, PlayerGroupState current, PlayerGroupState result)
	{
		for (String groupID : previous.webappGroupIDs)
		{
			if (!current.webappGroupIDs.contains(groupID))
			{
				String groupName = config.getGroupNameByGroupID(groupID);
				CommunityBridge.permissionHandler.removeFromGroup(playerName, groupName);
				result.permissionsSystemGroupNames.remove(groupName);
			}
		}

		for (Iterator<String> iterator = current.webappGroupIDs.iterator(); iterator.hasNext();)
		{
			String groupID = iterator.next();

			if (!previous.webappGroupIDs.contains(groupID))
			{
				String groupName = config.getGroupNameByGroupID(groupID);
				
				// Since this group is not in the mapping, we shouldn't record it
				// This way, if the group is later added, it will be 'new' to us
				// and we will synchronize.
				if (groupName == null)
				{
					result.webappGroupIDs.remove(groupID);
				}
				else if (!current.permissionsSystemPrimaryGroupName.equals(groupName) && !current.permissionsSystemGroupNames.contains(groupName))
				{
					CommunityBridge.permissionHandler.addToGroup(playerName, groupName);
					result.permissionsSystemGroupNames.add(groupName);
				} // Check for null/primaryalreadyset/secondaryalreadyset
			} // if previousState contains group ID
		} // for each group ID in currentState
	}

	private void rewardAchievements(Player player)
	{
		PlayerAchievementState state = new PlayerAchievementState(player.getName(), new File(plugin.getDataFolder(), "Players"));
		state.load();
		for (Achievement achievement : config.achievements)
		{
			if (achievement.playerQualifies(player, state))
			{
				achievement.rewardPlayer(player, state);
			}
		}
		state.save();
	}

	public String getPlayerName(String userID)
	{
		for (Entry<String, String> entry : playerUserIDs.entrySet())
		{
			if (userID.equals(entry.getValue()))
			{
				return entry.getKey();
			}
		}
		
		String playerName = loadPlayerNameFromDatabase(userID);
		return playerName;
	}
	
	private String loadPlayerNameFromDatabase(String userID)
	{
		final String exceptionBase = "Exception during WebApplication.getPlayerName(): ";
		String query;
		
		if (config.linkingUsesKey)
		{
			query = "SELECT `" + config.linkingValueColumn + "` "
						+ "FROM `" + config.linkingTableName + "` "
						+ "WHERE `" + config.linkingKeyColumn + "` = '" + config.linkingKeyName + "' "
						+ "AND `" + config.linkingUserIDColumn + "` = '" + userID + "'";
		}
		else
		{
			query = "SELECT `" + config.linkingPlayerNameColumn + "` "
						+ "FROM `" + config.linkingTableName + "` "
						+ "WHERE `" + config.linkingUserIDColumn + "` = '" + userID + "'";
		}

		try
		{
			String playerName = null;
			ResultSet result = sql.sqlQuery(query);

			if (result != null && result.next())
			{
				playerName = result.getString(1);
			}
			
			if (playerName == null)
			{
				log.finest("Player name for " + userID + " not found.");
			}
			return playerName;
		}
		catch (SQLException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
			return null;
		}
		catch (MalformedURLException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
			return null;
		}
		catch (InstantiationException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
			return null;
		}
		catch (IllegalAccessException exception)
		{
			log.severe(exceptionBase + exception.getMessage());
			return null;
		}		
	}

	private void configureDao()
	{
		if (config.webappSecondaryGroupStorageMethod.startsWith("sin"))
		{
			webGroupDao = new SingleWebGroupDao(config, sql, log);
		}
		else if (config.webappSecondaryGroupStorageMethod.startsWith("jun"))
		{
			webGroupDao = new JunctionWebGroupDao(config, sql, log);
		}
		else if (config.webappSecondaryGroupStorageMethod.startsWith("key"))
		{
			webGroupDao = new KeyValueWebGroupDao(config, sql, log);
		}
		else if (config.webappSecondaryGroupStorageMethod.startsWith("mul"))
		{
			webGroupDao = new MultipleKeyValueWebGroupDao(config, sql, log);
		}
		else
		{
			log.severe("Invalid storage method for secondary groups, disabling secondary synchronization.");
			config.webappSecondaryGroupEnabled = false;
		}
	}

	private class FieldBuilder
	{
		public List<String> insertFields;
		public List<String> updateFields;
		public List<String> inFields;
		List<String> foundFields;
		
		FieldBuilder(List<String> foundFields)
		{
			this.foundFields = foundFields;
			this.insertFields = new ArrayList<String>();
			this.updateFields = new ArrayList<String>();
			this.inFields = new ArrayList<String>();
		}

		/**
		 * Adds field data to the appropriate list depending on whether it needs inserted or updated.
		 * 
		 * @param userID
		 * @param data
		 */
		private void add(String userID, String key, String data)
		{
			if (config.statisticsUsesInsert && !foundFields.contains(key))
			{
				if (config.statisticsInsertMethod.startsWith("gen"))
				{
					insertFields.add("('" + userID + "', '" + key + "', '" + data + "')");
				}
				else if (config.statisticsInsertMethod.startsWith("smf"))
				{
					insertFields.add("('" + userID + "', '" + config.statisticsThemeID + "', '" + key + "', '" + data + "')");
				}
			}
			else
			{
				updateFields.add("WHEN '" + key + "' THEN '" + data + "'");
				inFields.add("'" + key + "'");
			}
		}
		
		private void add(String userID, String key, int data)
		{
			add(userID, key, Integer.toString(data));
		}
		
		private void add(String userID, String key, double data)
		{
			add(userID, key, Double.toString(data));
		}

		private void add(String userID, String key, float data)
		{
			add(userID, key, Float.toString(data));
		}
	}

	/**
	 * Called by updateStatistics when updating a table that columns (instead of keyvalue pairs).
	 *
	 * @param PlayerStatistics Bean containing the player's statistics
	 */
	private void updateStatisticsKeylessStyle(PlayerStatistics playerStatistics)
	{
		String query;
		List<String> fields = new ArrayList<String>();
		query = "UPDATE `" + config.statisticsTableName + "` "
					+ "SET ";

		if (config.onlineStatusEnabled)
		{
			fields.add("`" + config.onlineStatusColumnOrKey + "` = '" + playerStatistics.getOnlineStatus() +  "'");
		}

		if (config.lastonlineEnabled)
		{
			fields.add("`" + config.lastonlineColumnOrKey + "` = '" + playerStatistics.getLastOnlineTime() + "'");
			if (!config.lastonlineFormattedColumnOrKey.isEmpty())
			{
				fields.add("`" + config.lastonlineFormattedColumnOrKey + "` = '" + playerStatistics.getLastOnlineTimeFormatted() + "'");
			}
		}

		if (config.gametimeEnabled)
		{
			fields.add("`" + config.gametimeColumnOrKey + "` = '" + playerStatistics.getGameTime() + "'");
			if (!config.gametimeFormattedColumnOrKey.isEmpty())
			{
				fields.add("`" + config.gametimeFormattedColumnOrKey + "` = '" + playerStatistics.getGameTimeFormatted() + "'");
			}
		}

		if (config.levelEnabled)
		{
			fields.add("`" + config.levelColumnOrKey + "` = '" + playerStatistics.getLevel() + "'");
		}

		if (config.totalxpEnabled)
		{
			fields.add("`" + config.totalxpColumnOrKey + "` = '" + playerStatistics.getTotalXP() + "'");
		}

		if (config.currentxpEnabled)
		{
			fields.add("`" + config.currentxpColumnOrKey + "` = '" + playerStatistics.getCurrentXP() + "'");
			if (!config.currentxpFormattedColumnOrKey.isEmpty())
			{
				fields.add("`" + config.currentxpFormattedColumnOrKey + "` = '" + playerStatistics.getCurrentXPFormatted() + "'");
			}
		}

		if (config.healthEnabled)
		{
			fields.add("`" + config.healthColumnOrKey + "` = '" + (int)playerStatistics.getHealth() + "'");
		}

		if (config.lifeticksEnabled)
		{
			fields.add("`" + config.lifeticksColumnOrKey + "` = '" + playerStatistics.getLifeTicks() + "'");
			if (!config.lifeticksFormattedColumnOrKey.isEmpty())
			{
				fields.add("`" + config.lifeticksFormattedColumnOrKey + "` = '" + playerStatistics.getLifeTicksFormatted() + "'");
			}
		}

		if (config.walletEnabled)
		{
			fields.add("`" + config.walletColumnOrKey + "` = '" + playerStatistics.getWallet() + "'");
		}

		query = query + StringUtilities.joinStrings(fields, ", ") + " WHERE `" + config.statisticsUserIDColumn + "` = '" + playerStatistics.getUserID() + "'";

		String exceptionBase = "Exception during updateStatisticsKeylessStyle(): ";

		try
		{
			sql.updateQuery(query);
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
	}
} // WebApplication class
