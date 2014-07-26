package org.communitybridge.main;

import org.communitybridge.synchronization.Synchronizer;
import org.communitybridge.synchronization.PlayerState;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.communitybridge.achievement.Achievement;
import org.communitybridge.achievement.PlayerAchievementState;
import org.communitybridge.synchronization.ban.BanSynchronizer;
import org.communitybridge.synchronization.group.JunctionWebGroupDao;
import org.communitybridge.synchronization.group.KeyValueWebGroupDao;
import org.communitybridge.synchronization.group.MultipleKeyValueWebGroupDao;
import org.communitybridge.synchronization.group.SingleWebGroupDao;
import org.communitybridge.synchronization.group.WebGroupDao;
import org.communitybridge.utility.Log;
import org.communitybridge.utility.MinecraftUtilities;
import org.communitybridge.utility.StringUtilities;

public class WebApplication extends Synchronizer
{
	public static final List<String> EMPTY_LIST = new ArrayList<String>();
	protected static final String EXCEPTION_MESSAGE_ADDGROUP = "Exception during WebApplication.addGroup(): ";
	protected static final String EXCEPTION_MESSAGE_GETPRIMARY = "Exception during WebApplication.getPrimaryGroupID(): ";
	protected static final String EXCEPTION_MESSAGE_GETSECONDARY = "Exception during WebApplication.getUserSecondaryGroupIDs(): ";
	protected	static final String EXCEPTION_MESSAGE_REMOVEGROUP = "Exception during WebApplication.addGroup(): ";
	protected	static final String EXCEPTION_MESSAGE_GETUSERID = "Exception during WebApplication.getUserIDfromDatabase(): ";

	private final Boolean synchronizationLock = true;
	private Configuration configuration;
	private Log log;
	private CommunityBridge plugin;
	private BanSynchronizer banSynchronizer;
	private WebGroupDao webGroupDao;

	private List<Player> playerLocks = new ArrayList<Player>();

	public WebApplication(Environment environment, WebGroupDao webGroupDao)
	{
		super(environment);
		this.configuration = environment.getConfiguration();
		this.log = environment.getLog();
		this.webGroupDao = webGroupDao;
	}

	public WebApplication(Environment environment)
	{
		super(environment);
		this.configuration = environment.getConfiguration();
		this.log = environment.getLog();
		this.plugin = environment.getPlugin();
		configureDao();
		if (configuration.banSynchronizationEnabled)
		{
			banSynchronizer = new BanSynchronizer(environment);
		}
	}

	public boolean playerHasAvatar(String userID)
	{
		final String exceptionBase = "Exception during WebApplication.playerHasAvatar(): ";
		String query;

		query = "SELECT `" + configuration.avatarTableName + "`.`" + configuration.avatarAvatarColumn + "` "
					+ "FROM `" + configuration.avatarTableName + "` "
					+ "WHERE `" + configuration.avatarUserIDColumn + "` = '" + userID + "'";

		try
		{
			String avatar = null;
			ResultSet result = environment.getSql().sqlQuery(query);

			if (result.next())
			{
				avatar = result.getString(configuration.avatarAvatarColumn);
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

	public int getUserPostCount(String userID)
	{
		final String exceptionBase = "Exception during WebApplication.getUserPostCount(): ";
		String query;

		query = "SELECT `" + configuration.postCountTableName + "`.`" + configuration.postCountPostCountColumn + "` "
					+ "FROM `" + configuration.postCountTableName + "` "
					+ "WHERE `" + configuration.postCountUserIDColumn + "` = '" + userID + "'";

		try
		{
			ResultSet result = environment.getSql().sqlQuery(query);

			if (result.next())
			{
				return result.getInt(configuration.postCountPostCountColumn);
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

	public String getUserPrimaryGroupID(String userID)
	{
		try
		{
			return webGroupDao.getPrimaryGroupID(userID);
		}
		catch (SQLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETPRIMARY + exception.getMessage());
			return "";
		}
		catch (MalformedURLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETPRIMARY + exception.getMessage());
			return "";
		}
		catch (InstantiationException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETPRIMARY + exception.getMessage());
			return "";
		}
		catch (IllegalAccessException exception)
		{
			log.severe(EXCEPTION_MESSAGE_GETPRIMARY + exception.getMessage());
			return "";
		}
	}

	public List<String> getUserSecondaryGroupIDs(String userID)
	{
		try
		{
			return webGroupDao.getSecondaryGroupIDs(userID);
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

	public void onJoin(Player player)
	{
		if (configuration.syncDuringJoin)
		{
			runSynchronizePlayer(player, true);
		}
	}

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
		environment.getLog().finest("Running player synchronization.");
		Player[] onlinePlayers = Bukkit.getOnlinePlayers();
		for (Player player : onlinePlayers)
		{
			synchronizePlayer(player, true);
		}
		environment.getLog().finest("Player synchronization complete.");

		if (configuration.banSynchronizationEnabled)
		{
			environment.getLog().finest("Running ban synchronization.");
			banSynchronizer.synchronize();
			environment.getLog().finest("Ban synchronization complete.");
		}
	}

	private void synchronizePlayer(Player player, boolean online)
	{
		if (!playerLocks.contains(player))
		{
			synchronized (synchronizationLock) { playerLocks.add(player);}
			if (configuration.groupSynchronizationActive)
			{
				synchronizeGroups(player);
			}
			if (configuration.statisticsEnabled)
			{
				updateStatistics(player, online);
			}
			if (configuration.useAchievements)
			{
				rewardAchievements(player);
			}
			synchronized (synchronizationLock) { playerLocks.remove(player); }
		}
	}

	private void setPrimaryGroup(String userID, String groupID)
	{
		String exceptionBase = "Exception during setPrimaryGroup(): ";

		try
		{
			if (configuration.webappPrimaryGroupUsesKey)
			{
				String query = "UPDATE `" + configuration.webappPrimaryGroupTable + "` "
										 + "SET `" + configuration.webappPrimaryGroupGroupIDColumn + "` = '" + groupID + "' "
										 + "WHERE `" + configuration.webappPrimaryGroupKeyColumn + "` = '" + configuration.webappPrimaryGroupKeyName + "' "
										 + "AND `" + configuration.webappPrimaryGroupUserIDColumn + "` = '" + userID + "'";
				environment.getSql().updateQuery(query);
			}
			else
			{
				String query = "UPDATE `" + configuration.webappPrimaryGroupTable + "` "
										 + "SET `" + configuration.webappPrimaryGroupGroupIDColumn + "` = '" + groupID + "' "
										 + "WHERE `" + configuration.webappPrimaryGroupUserIDColumn + "` = '" + userID + "' ";
				environment.getSql().updateQuery(query);
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
		String direction = configuration.simpleSynchronizationDirection;
		String userID = environment.getUserPlayerLinker().getUserID(player);

		// This can happen if the player disconnects after synchronization has
		// already begun.
		if (userID == null)
		{
			return;
		}

		if (userID.equalsIgnoreCase(configuration.simpleSynchronizationSuperUserID))
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

		PlayerState previous = new PlayerState(environment, playerFolder, player, userID);
		previous.load();

		PlayerState current = new PlayerState(environment, playerFolder, player, userID);
		current.generate();
		PlayerState result = current.copy();

		if(configuration.simpleSynchronizationFirstDirection.startsWith("web") && previous.isNewFile) {
			direction = "web";
		}

		if (configuration.webappPrimaryGroupEnabled)
		{
			synchronizeGroupsPrimary(direction, previous, current, result, playerName, player, userID);
		}
		else
		{
			// With synchronization turned off the currentState should always be the previous state.
			current.setPermissionsSystemPrimaryGroupName(previous.getPermissionsSystemPrimaryGroupName());
			current.setWebappPrimaryGroupID(previous.getWebappPrimaryGroupID());
		}

		// 4. Synchronize secondary group state
		if (configuration.webappSecondaryGroupEnabled)
		{
			synchronizeGroupsSecondary(direction, previous, current, result, userID, player);
		}
		else
		{
			// With synchronization turned off the currentState should always be the previous state.
			current.setPermissionsSystemGroupNames(previous.getPermissionsSystemGroupNames());
			current.setWebappGroupIDs(previous.getWebappGroupIDs());
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

	protected void addGroup(String userID, String groupID, int currentGroupCount)
	{
		try
		{
				webGroupDao.addUserToGroup(userID, groupID, currentGroupCount);
		}
		catch (MalformedURLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_ADDGROUP + exception.getMessage());
		}
		catch (InstantiationException exception)
		{
			log.severe(EXCEPTION_MESSAGE_ADDGROUP + exception.getMessage());
		}
		catch (IllegalAccessException exception)
		{
			log.severe(EXCEPTION_MESSAGE_ADDGROUP + exception.getMessage());
		}
		catch (SQLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_ADDGROUP + exception.getMessage());
		}
	}

	protected void removeGroup(String userID, String groupName)
	{
		try
		{
			webGroupDao.removeUserFromGroup(userID, configuration.getWebappGroupIDbyGroupName(groupName));
		}
		catch (SQLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_REMOVEGROUP + exception.getMessage());
		}
		catch (MalformedURLException exception)
		{
			log.severe(EXCEPTION_MESSAGE_REMOVEGROUP + exception.getMessage());
		}
		catch (InstantiationException exception)
		{
			log.severe(EXCEPTION_MESSAGE_REMOVEGROUP + exception.getMessage());
		}
		catch (IllegalAccessException exception)
		{
			log.severe(EXCEPTION_MESSAGE_REMOVEGROUP + exception.getMessage());
		}
	}

	private void updateStatistics(Player player, boolean online)
	{
		PlayerStatistics playerStatistics = new PlayerStatistics(configuration.dateFormat);

		String query;
		ResultSet result;

		int previousLastOnline = 0;
		int previousGameTime = 0;

		playerStatistics.setUserID(environment.getUserPlayerLinker().getUserID(player));
		if (playerStatistics.getUserID() == null)
		{
			return;
		}

		// If gametime is enabled, it depends on lastonline. Also, we need to
		// retrieve previously recorded lastonline time and the previously
		// recorded gametime to compute the new gametime.
		if (configuration.gametimeEnabled)
		{
			if (configuration.statisticsUsesKey)
			{
				query = "SELECT `" + configuration.statisticsKeyColumn +  "`, `" + configuration.statisticsValueColumn + "` "
							+ "FROM `" + configuration.statisticsTableName + "` "
							+ "WHERE `" + configuration.statisticsUserIDColumn + "` = '" + playerStatistics.getUserID() + "'";
				try
				{
					result = environment.getSql().sqlQuery(query);
					while (result.next())
					{
						String key = result.getString(configuration.statisticsKeyColumn);
						if (key.equalsIgnoreCase(configuration.lastonlineColumnOrKey))
						{
							previousLastOnline = result.getInt(configuration.statisticsValueColumn);
						}
						else if (key.equalsIgnoreCase(configuration.gametimeColumnOrKey))
						{
							previousGameTime = result.getInt(configuration.statisticsValueColumn);
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
				query = "SELECT `" + configuration.lastonlineColumnOrKey + "`, `" + configuration.gametimeColumnOrKey + "`"
							+ " FROM `" + configuration.statisticsTableName + "`"
							+ " WHERE `" + configuration.statisticsUserIDColumn + "` = '" + playerStatistics.getUserID() + "'";
				try
				{
					result = environment.getSql().sqlQuery(query);

					if (result.next())
					{
						previousLastOnline = result.getInt(configuration.lastonlineColumnOrKey);
						previousGameTime = result.getInt(configuration.gametimeColumnOrKey);
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

		if (configuration.onlineStatusEnabled)
		{
			if (online)
			{
				playerStatistics.setOnlineStatus(configuration.onlineStatusValueOnline);
			}
			else
			{
				playerStatistics.setOnlineStatus(configuration.onlineStatusValueOffline);
			}
		}

		if (configuration.lastonlineEnabled)
		{
			playerStatistics.setLastOnlineTime(System.currentTimeMillis());
		}

		if (configuration.gametimeEnabled && previousLastOnline > 0)
		{
			playerStatistics.setGameTime(previousGameTime + playerStatistics.getLastOnlineTimeInSeconds() - previousLastOnline);
		}

		if (configuration.levelEnabled)
		{
			playerStatistics.setLevel(player.getLevel());
		}

		if (configuration.totalxpEnabled)
		{
			playerStatistics.setTotalXP(player.getTotalExperience());
		}

		if (configuration.currentxpEnabled)
		{
			playerStatistics.setCurrentXP(player.getExp());
		}

		if (configuration.healthEnabled)
		{
			playerStatistics.setHealth((double)player.getHealth());
		}

		if (configuration.lifeticksEnabled)
		{
			playerStatistics.setLifeTicks(player.getTicksLived());
		}

		if (configuration.walletEnabled)
		{
			playerStatistics.setWallet(environment.getEconomy().getBalance(player));
		}

		if (configuration.statisticsUsesKey)
		{
			updateStatisticsKeyStyle(playerStatistics);
		}
		else
		{
			updateStatisticsKeylessStyle(playerStatistics);
		}
	}

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
		String insertQuery = "INSERT INTO `" + configuration.statisticsTableName + "` ("
											 + configuration.statisticsUserIDColumn + ", "
											 + (configuration.statisticsUsesInsert && configuration.statisticsInsertMethod.startsWith("smf") ? configuration.statisticsThemeIDColumn + ", " : "")
											 + configuration.statisticsKeyColumn + ", "
											 + configuration.statisticsValueColumn + ") VALUES ";
		String updateQuery = "UPDATE `" + configuration.statisticsTableName + "` "
											 + "SET " + "`" + configuration.statisticsValueColumn
											 + "` = CASE " + "`" + configuration.statisticsKeyColumn + "` ";
		try
		{
			if (configuration.statisticsUsesInsert)
			{
				String selectQuery = "SELECT `" + configuration.statisticsKeyColumn + "` "
													 + " FROM `" + configuration.statisticsTableName + "` "
													 + " WHERE `" + configuration.statisticsUserIDColumn + "` = '"
													 + playerStatistics.getUserID() + "'"
													 + (configuration.statisticsInsertMethod.startsWith("smf") ? " AND `" + configuration.statisticsThemeIDColumn + "` = '" + configuration.statisticsThemeID + "'" : "");

				ResultSet result = environment.getSql().sqlQuery(selectQuery);
				while (result.next())
				{
					foundFields.add(result.getString(configuration.statisticsKeyColumn));
				}
			}

			FieldBuilder builder = new FieldBuilder(foundFields);

			if (configuration.onlineStatusEnabled)
			{
				builder.add(playerStatistics.getUserID(), configuration.onlineStatusColumnOrKey, playerStatistics.getOnlineStatus());
			}

			if (configuration.lastonlineEnabled)
			{
				builder.add(playerStatistics.getUserID(), configuration.lastonlineColumnOrKey, playerStatistics.getLastOnlineTimeInSeconds());
				if (!configuration.lastonlineFormattedColumnOrKey.isEmpty())
				{
					builder.add(playerStatistics.getUserID(), configuration.lastonlineFormattedColumnOrKey, playerStatistics.getLastOnlineTimeFormatted());
				}
			}

			// Gametime actually relies on the prior lastonlineTime...
			if (configuration.gametimeEnabled && configuration.lastonlineEnabled)
			{
				builder.add(playerStatistics.getUserID(), configuration.gametimeColumnOrKey, playerStatistics.getGameTime());
				if (!configuration.gametimeFormattedColumnOrKey.isEmpty())
				{
					builder.add(playerStatistics.getUserID(), configuration.gametimeFormattedColumnOrKey, playerStatistics.getGameTimeFormatted());
				}
			}

			if (configuration.levelEnabled)
			{
				builder.add(playerStatistics.getUserID(), configuration.levelColumnOrKey, playerStatistics.getLevel());
			}

			if (configuration.totalxpEnabled)
			{
				builder.add(playerStatistics.getUserID(), configuration.totalxpColumnOrKey, playerStatistics.getTotalXP());
			}

			if (configuration.currentxpEnabled)
			{
				builder.add(playerStatistics.getUserID(), configuration.currentxpColumnOrKey, playerStatistics.getCurrentXP());
				if (!configuration.currentxpFormattedColumnOrKey.isEmpty())
				{
					builder.add(playerStatistics.getUserID(), configuration.currentxpFormattedColumnOrKey, playerStatistics.getCurrentXPFormatted());
				}
			}

			if (configuration.healthEnabled)
			{
				builder.add(playerStatistics.getUserID(), configuration.healthColumnOrKey, (int)playerStatistics.getHealth());
			}

			if (configuration.lifeticksEnabled)
			{
				builder.add(playerStatistics.getUserID(), configuration.lifeticksColumnOrKey, playerStatistics.getLifeTicks());
				if (!configuration.lifeticksFormattedColumnOrKey.isEmpty())
				{
					builder.add(playerStatistics.getUserID(), configuration.lifeticksFormattedColumnOrKey, playerStatistics.getLifeTicksFormatted());
				}
			}

			if (configuration.walletEnabled)
			{
				builder.add(playerStatistics.getUserID(), configuration.walletColumnOrKey, playerStatistics.getWallet());
			}

			if (builder.insertFields.size() > 0)
			{
				insertQuery = insertQuery + StringUtilities.joinStrings(builder.insertFields, ", ") + ";";
				environment.getSql().insertQuery(insertQuery);
			}

			if (builder.updateFields.size() > 0)
			{
				updateQuery = updateQuery + StringUtilities.joinStrings(builder.updateFields, " ")
										+ " END"
										+ " WHERE `" + configuration.statisticsUserIDColumn + "` = '"
										+ playerStatistics.getUserID() + "'"
										+ " AND `" + configuration.statisticsKeyColumn + "`"
										+ " IN (" + StringUtilities.joinStrings(builder.inFields, ", ") + ");";

				environment.getSql().updateQuery(updateQuery);
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

	private void synchronizeGroupsPrimary(String direction, PlayerState previous, PlayerState current, PlayerState result, String playerName, Player player, String userID)
	{
		if (isValidDirection(direction, "web") && !previous.getWebappPrimaryGroupID().equals(current.getWebappPrimaryGroupID()))
		{
			synchronizeGroupsPrimaryWebToGame(player, previous, current, result);
		}

		if (isValidDirection(direction, "min") && !previous.getPermissionsSystemPrimaryGroupName().equals(current.getPermissionsSystemPrimaryGroupName()))
		{
			synchronizeGroupsPrimaryGameToWeb(userID, playerName, previous, current, result);
		}
	}

	private void synchronizeGroupsSecondary(String direction, PlayerState previous, PlayerState current, PlayerState result, String userID, Player player)
	{
		if (isValidDirection(direction, "min"))
		{
			synchronizeGroupsSecondaryGameToWeb(userID, previous, current, result);
		}

		if (isValidDirection(direction, "web"))
		{
			synchronizeGroupsSecondaryWebToGame(player, previous, current, result);
		}
	}

	private void synchronizeGroupsPrimaryWebToGame(Player player, PlayerState previous, PlayerState current, PlayerState result)
	{
		if (previous.getWebappPrimaryGroupID().equalsIgnoreCase(current.getWebappPrimaryGroupID()))
		{
			return;
		}

		String formerGroupName = configuration.getGroupNameByGroupID(previous.getWebappPrimaryGroupID());
		String newGroupName = configuration.getGroupNameByGroupID(current.getWebappPrimaryGroupID());
		String playerName = player.getName();

		if (newGroupName == null)
		{
			log.warning("Not changing permissions group due to permissions system group name lookup failure for web application group ID: " + current.getWebappPrimaryGroupID() + ". Player '" + playerName + "' primary group state unchanged.");
			result.setWebappPrimaryGroupID(previous.getWebappPrimaryGroupID());
			return;
		}

		if (configuration.simpleSynchronizationPrimaryGroupNotify)
		{
			String message = ChatColor.YELLOW + configuration.messages.get("group-synchronization-primary-notify-player");
			message = message.replace("~GROUPNAME~", newGroupName);
			player.sendMessage(message);
		}
		setPermissionHandlerPrimaryGroup(player, newGroupName, formerGroupName, result);
	}

	private void synchronizeGroupsPrimaryGameToWeb(String userID, String playerName, PlayerState previous, PlayerState current, PlayerState result)
	{
		String groupID = configuration.getWebappGroupIDbyGroupName(current.getPermissionsSystemPrimaryGroupName());

		if (groupID == null)
		{
			log.warning("Not changing web application group due to web application group ID lookup failure for: " + current.getPermissionsSystemPrimaryGroupName() + ". Player '" + playerName + "' primary group state unchanged.");
			result.setPermissionsSystemPrimaryGroupName(previous.getPermissionsSystemPrimaryGroupName());
		}
		else
		{
			result.setWebappPrimaryGroupID(groupID);
			setPrimaryGroup(userID, groupID);
			log.fine("Moved player '" + playerName + "' to web application group ID '" + groupID + "'.");
		}
	}

	private void synchronizeGroupsSecondaryGameToWeb(String userID, PlayerState previous, PlayerState current, PlayerState result)
	{
		for (String groupName : previous.getPermissionsSystemGroupNames())
		{
			if (!current.getPermissionsSystemGroupNames().contains(groupName) && !configuration.simpleSynchronizationGroupsTreatedAsPrimary.contains(groupName))
			{
				String groupID = configuration.getWebappGroupIDbyGroupName(groupName);
				removeGroup(userID, groupName);
				result.getWebappGroupIDs().remove(groupID);
			}
		}

		int addedCount = 0;
		for (Iterator<String> iterator = current.getPermissionsSystemGroupNames().iterator(); iterator.hasNext();)
		{
			String groupName = iterator.next();

			if (!previous.getPermissionsSystemGroupNames().contains(groupName))
			{
				String groupID = configuration.getWebappGroupIDbyGroupName(groupName);

				// Since the group is not in the mapping, we'll NOT record it as
				// part of the current state. That way, if the group is added to
				// the mapping later, we'll see it as a 'new' group and synchronize.
				if (groupID == null)
				{
					result.getPermissionsSystemGroupNames().remove(groupName);
				}
				else if (current.getWebappGroupIDs().contains(groupID))
				{
					log.warning("We thought we needed to add a secondary group ID " + groupID + "...but we didn't?");
				}
				else
				{
					addGroup(userID, groupID, result.getWebappGroupIDs().size() + addedCount);
					result.getWebappGroupIDs().add(groupID);
					addedCount++;
				}
			}
		}
	}

	private void synchronizeGroupsSecondaryWebToGame(Player player, PlayerState previous, PlayerState current, PlayerState result)
	{
		for (String groupID : previous.getWebappGroupIDs())
		{
			if (!current.getWebappGroupIDs().contains(groupID))
			{
				String groupName = configuration.getGroupNameByGroupID(groupID);
				environment.getPermissionHandler().removeFromGroup(player, groupName);
				result.getPermissionsSystemGroupNames().remove(groupName);
			}
		}

		for (Iterator<String> iterator = current.getWebappGroupIDs().iterator(); iterator.hasNext();)
		{
			String groupID = iterator.next();

			if (!previous.getWebappGroupIDs().contains(groupID))
			{
				String groupName = configuration.getGroupNameByGroupID(groupID);

				// Since this group is not in the mapping, we shouldn't record it
				// This way, if the group is later added, it will be 'new' to us
				// and we will synchronize.
				if (groupName == null)
				{
					result.getWebappGroupIDs().remove(groupID);
				}
				else if (configuration.simpleSynchronizationWebappSecondaryGroupsTreatedAsPrimary.contains(groupName))
				{
					setPermissionHandlerPrimaryGroup(player, groupName, current.getPermissionsSystemPrimaryGroupName(), result);
				}
				else if (!current.getPermissionsSystemPrimaryGroupName().equals(groupName) && !current.getPermissionsSystemGroupNames().contains(groupName))
				{
					environment.getPermissionHandler().addToGroup(player, groupName);
					result.getPermissionsSystemGroupNames().add(groupName);
				}			} // if previousState contains group ID
		} // for each group ID in currentState
	}

	private void rewardAchievements(Player player)
	{
		PlayerAchievementState state = new PlayerAchievementState(player.getName(), new File(plugin.getDataFolder(), "Players"));
		state.load();
		for (Achievement achievement : configuration.achievements)
		{
			if (achievement.playerQualifies(player, state))
			{
				achievement.rewardPlayer(player, state);
			}
		}
		try
		{
			state.save();
		}
		catch (IOException exception)
		{
			log.severe("Exception while saving " + player.getName() + " achievement state: " + exception.getMessage());
		}
	}

	private void configureDao()
	{
		if (configuration.webappSecondaryGroupStorageMethod.startsWith("sin"))
		{
			webGroupDao = new SingleWebGroupDao(environment);
		}
		else if (configuration.webappSecondaryGroupStorageMethod.startsWith("jun"))
		{
			webGroupDao = new JunctionWebGroupDao(environment);
		}
		else if (configuration.webappSecondaryGroupStorageMethod.startsWith("key"))
		{
			webGroupDao = new KeyValueWebGroupDao(environment);
		}
		else if (configuration.webappSecondaryGroupStorageMethod.startsWith("mul"))
		{
			webGroupDao = new MultipleKeyValueWebGroupDao(environment);
		}
		else
		{
			log.severe("Invalid storage method for secondary groups, disabling secondary synchronization.");
			configuration.webappSecondaryGroupEnabled = false;
		}
	}

	private void setPermissionHandlerPrimaryGroup(Player player, String newGroupName, String formerGroupName, PlayerState result)
	{
		String pseudo = "";
		if (environment.getPermissionHandler().supportsPrimaryGroups())
		{
			environment.getPermissionHandler().setPrimaryGroup(player, newGroupName, formerGroupName);
		}
		else
		{
			environment.getPermissionHandler().switchGroup(player, formerGroupName, newGroupName);
			pseudo = "pseudo-primary ";
		}
		result.setPermissionsSystemPrimaryGroupName(newGroupName);
		if (formerGroupName == null)
		{
			log.fine("Placed player '" + player.getName() + "' in " + pseudo + "permissions group '" + newGroupName + "'.");
		}
		else
		{
			log.fine("Moved player '" + player.getName() + "' to " + pseudo + "permissions group '" + newGroupName + "' from '" + formerGroupName + "'.");
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
			if (configuration.statisticsUsesInsert && !foundFields.contains(key))
			{
				if (configuration.statisticsInsertMethod.startsWith("gen"))
				{
					insertFields.add("('" + userID + "', '" + key + "', '" + data + "')");
				}
				else if (configuration.statisticsInsertMethod.startsWith("smf"))
				{
					insertFields.add("('" + userID + "', '" + configuration.statisticsThemeID + "', '" + key + "', '" + data + "')");
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

	private void updateStatisticsKeylessStyle(PlayerStatistics playerStatistics)
	{
		String query;
		List<String> fields = new ArrayList<String>();
		query = "UPDATE `" + configuration.statisticsTableName + "` "
					+ "SET ";

		if (configuration.onlineStatusEnabled)
		{
			fields.add("`" + configuration.onlineStatusColumnOrKey + "` = '" + playerStatistics.getOnlineStatus() +  "'");
		}

		if (configuration.lastonlineEnabled)
		{
			fields.add("`" + configuration.lastonlineColumnOrKey + "` = '" + playerStatistics.getLastOnlineTimeInSeconds() + "'");
			if (!configuration.lastonlineFormattedColumnOrKey.isEmpty())
			{
				fields.add("`" + configuration.lastonlineFormattedColumnOrKey + "` = '" + playerStatistics.getLastOnlineTimeFormatted() + "'");
			}
		}

		if (configuration.gametimeEnabled)
		{
			fields.add("`" + configuration.gametimeColumnOrKey + "` = '" + playerStatistics.getGameTime() + "'");
			if (!configuration.gametimeFormattedColumnOrKey.isEmpty())
			{
				fields.add("`" + configuration.gametimeFormattedColumnOrKey + "` = '" + playerStatistics.getGameTimeFormatted() + "'");
			}
		}

		if (configuration.levelEnabled)
		{
			fields.add("`" + configuration.levelColumnOrKey + "` = '" + playerStatistics.getLevel() + "'");
		}

		if (configuration.totalxpEnabled)
		{
			fields.add("`" + configuration.totalxpColumnOrKey + "` = '" + playerStatistics.getTotalXP() + "'");
		}

		if (configuration.currentxpEnabled)
		{
			fields.add("`" + configuration.currentxpColumnOrKey + "` = '" + playerStatistics.getCurrentXP() + "'");
			if (!configuration.currentxpFormattedColumnOrKey.isEmpty())
			{
				fields.add("`" + configuration.currentxpFormattedColumnOrKey + "` = '" + playerStatistics.getCurrentXPFormatted() + "'");
			}
		}

		if (configuration.healthEnabled)
		{
			fields.add("`" + configuration.healthColumnOrKey + "` = '" + (int)playerStatistics.getHealth() + "'");
		}

		if (configuration.lifeticksEnabled)
		{
			fields.add("`" + configuration.lifeticksColumnOrKey + "` = '" + playerStatistics.getLifeTicks() + "'");
			if (!configuration.lifeticksFormattedColumnOrKey.isEmpty())
			{
				fields.add("`" + configuration.lifeticksFormattedColumnOrKey + "` = '" + playerStatistics.getLifeTicksFormatted() + "'");
			}
		}

		if (configuration.walletEnabled)
		{
			fields.add("`" + configuration.walletColumnOrKey + "` = '" + playerStatistics.getWallet() + "'");
		}

		query = query + StringUtilities.joinStrings(fields, ", ") + " WHERE `" + configuration.statisticsUserIDColumn + "` = '" + playerStatistics.getUserID() + "'";

		String exceptionBase = "Exception during updateStatisticsKeylessStyle(): ";

		try
		{
			environment.getSql().updateQuery(query);
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

	public WebGroupDao getWebGroupDao()
	{
		return webGroupDao;
	}
} // WebApplication class
