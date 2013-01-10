package org.ruhlendavis.mc.communitybridge;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.netmanagers.api.SQL;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ruhlendavis.mc.utility.Log;

/**
 * Class for storing configuration information loaded from the yaml files.
 *
 * @author Feaelin (Iain E. Davis) <iain@ruhlendavis.org>
 */
public class Configuration
{
	private CommunityBridge plugin;
	private Log log;
	public Map<String, String> messages = new HashMap();

	// General Section
	public String logLevel;
	public boolean usePluginMetrics;

	public String permissionsSystem;

  public String autoEveryUnit;
  public boolean autoSync;
	public long autoSyncEvery;

	public String applicationURL;

	// Database Section
	public String databaseHost;
	public String databasePort;
	public String databaseName;
	public String databaseUsername;
	public String databasePassword;

	// Linking Section
  public boolean linkingAutoRemind;
	public long linkingAutoEvery;
	public boolean linkingNotifyRegistered;
	public boolean linkingNotifyUnregistered;
	public boolean linkingKickUnregistered;

	public boolean linkingUsesKey;
	public String linkingTableName;
	public String linkingUserIDColumn;
	public String linkingPlayerNameColumn;
	public String linkingKeyName;
	public String linkingKeyColumn;
	public String linkingValueColumn;

	// Requirements Section
	public boolean requireAvatar;
	public String	requireAvatarTableName;
	public String	requireAvatarUserIDColumn;
	public String	requireAvatarAvatarColumn;

	public boolean requireMinimumPosts;
	public String	requirePostsTableName;
	public String	requirePostsUserIDColumn;
	public String requirePostsPostCountColumn;
	public int requirePostsPostCount;

	// Group Synchronization: Primary
	public boolean groupSyncPrimaryEnabled;
	public boolean groupSyncPrimaryNotifyPlayer;
	public boolean groupSyncPrimaryUsesKey;
	public String groupSyncPrimaryTableName;
	public String groupSyncPrimaryUserIDColumn;
	public String groupSyncPrimaryGroupIDColumn;
	public String groupSyncPrimaryKeyName;
	public String groupSyncPrimaryKeyColumn;
	public String groupSyncPrimaryValueColumn;
	public Map<String, GroupRule> groupSyncPrimaryWebappRules = new HashMap();
	public Map<String, GroupRule> groupSyncPrimaryMinecraftRules = new HashMap();

	// Group Synchronization: Secondary
	public boolean groupSyncSecondaryEnabled;

	// Statistics Tracking Settings
	public boolean statisticsEnabled;
	public String statisticsTableName;
	public String statisticsUserIDColumn;
	public boolean statisticsUsesKey;
	public String statisticsKeyColumn;
	public String statisticsValueColumn;

	public boolean onlineStatusEnabled;
	public String onlineStatusColumnOrKey;
	public String onlineStatusValueOffline;
	public String onlineStatusValueOnline;

	// These are not in the config.yml. They are calculated.
	public boolean permissionsSystemRequired;
	public boolean groupSyncEnabled;

	// Instance variables associated with the old configuration
	public List<String> primaryGroupIDsToIgnore;
	public Map<String, Object> groups;
	public String defaultGroup;

	public boolean multiTables;
	public boolean multiTablesUseKey;
	public boolean useBanned;
	public boolean banlistTableEnabled;
	public boolean groups_table_enabled;

  public boolean secondary_groups = false;
	public boolean show_primary_group = false;

	public String users_table;
	public String banlist_table;
	public String groups_table;
	public String multi_table;

	public String banlist_user_id_field;
	public String banlist_banned_id_field;
	public String groups_user_id_field;
	public String groups_group_id_field;

	public String user_id_field;
	public String user_name_field;
	public String groups_id_field;
	public String secondary_groups_id_field;

	public String is_banned_field;

	public String banned_users_group;
	public int minposts_required;

	public String multi_table_key_field;
	public String multi_table_key_value;
	public String multi_table_value_field;
	public String multi_table_user_id_field;

	public boolean lastonlineEnabled;
	public String lastonlineColumn;
	public String lastonlineFormattedColumn;
  public String lastonlineKeyValue;
  public String lastonlineFormattedKeyValue;

	public boolean gametimeEnabled;
	public String gametimeColumn;
  public String gametimeFormattedColumn;
  public String gametimeKeyValue;
  public String gametimeFormattedKeyValue;

	public boolean totalxpEnabled;
	public String totalxpKeyValue;
	public String totalxpColumn;

	public boolean currentxpEnabled;
  public String currentxpKeyValue;
  public String currentxpFormattedKeyValue;
	public String currentxpColumn;
	public String currentxpFormattedColumn;

	public boolean levelEnabled;
	public String levelColumn;
  public String levelKeyValue;

	public boolean healthEnabled;
	public String healthColumn;
  public String healthKeyValue;

	public boolean lifeticksEnabled;
	public String lifeticksColumn;
	public String lifeticksFormattedColumn;
  public String lifeticksKeyValue;
	public String lifeticksFormattedKeyValue;

	public boolean walletEnabled;
  public String walletKeyValue;
	public String walletColumn;

	/**
	 * Constructor for the configuration class.
	 *
	 * @param CommunityBridge The plugin object of this plugin.
	 */
	public Configuration(CommunityBridge plugin, Log log)
	{
		this.plugin = plugin;
		this.log = log;
		load();
		loadOldConfig();
		loadMessages();
		report();
	}

	/**
	 * Analyze the configuration for potential problems.
	 *
	 * Checks for the existence of the specified tables and columns within those
	 * tables.
	 *
	 * @param SQL SQL query object.
	 * @return boolean True if the configuration is okay.
	 */
	public boolean analyzeConfiguration(SQL sql)
	{
		boolean status;
		boolean temp;

		// Linking table section.
		status = checkTable(sql, "player-user-linking.table-name", linkingTableName);
		if (status)
		{
			status = status & checkColumn(sql, "player-user-linking.user-id-column", linkingTableName, linkingUserIDColumn);
			if (linkingUsesKey)
			{
				temp = checkColumn(sql, "player-user-linking.key-column", linkingTableName , linkingKeyColumn);
				status = status & temp;
				if (temp)
				{
					checkKeyColumnForKey(sql, "player-user-linking.key-name", linkingTableName, linkingKeyColumn,	linkingKeyName);
				}

				status = status & checkColumn(sql, "player-user-linking.value-column", linkingTableName, linkingValueColumn);
			}
			else
			{
				status = status & checkColumn(sql, "player-user-linking.playername-column", linkingTableName, linkingPlayerNameColumn);
			}
		}

		if (requireAvatar)
		{
			temp = checkTable(sql, "requirement.avatar.table-name", requireAvatarTableName);
			status = status & temp;
			if (temp)
			{
				status = status & checkColumn(sql, "requirement.avatar.user-id-column", requireAvatarTableName, requireAvatarUserIDColumn);
				status = status & checkColumn(sql, "requirement.avatar.avatar-column", requireAvatarTableName, requireAvatarAvatarColumn);
			}
		}

		if (requireMinimumPosts)
		{
			temp = checkTable(sql, "requirement.minimum-posts.table-name", requirePostsTableName);
			status = status & temp;
			if (temp)
			{
				status = status & checkColumn(sql, "requirement.minimum-posts.user-id-column", requirePostsTableName, requirePostsUserIDColumn);
				status = status & checkColumn(sql, "requirement.minimum-posts.post-count-column", requirePostsTableName, requirePostsPostCountColumn);
			}

		}

		if (statisticsEnabled)
		{
			temp = checkTable(sql, "statistics.table-name", statisticsTableName);
			status = status & temp;
			if (temp)
			{
				status = status & checkColumn(sql, "statistics.user-id-column", statisticsTableName, statisticsUserIDColumn);
				if (statisticsUsesKey)
				{
					temp = checkColumn(sql, "statistics.key-column", statisticsTableName, statisticsKeyColumn);
					temp = temp & checkColumn(sql, "statistics.value-column", statisticsTableName, statisticsValueColumn);
					status = status & temp;
					if (temp)
					{
						checkKeyColumnForKey(sql, "statistics.trackers.online-status.column-or-key-name", statisticsTableName, statisticsKeyColumn,	onlineStatusColumnOrKey);
					}
				}
				else
				{
					status = status & checkColumn(sql, "statistics.trackers.online-status.column-or-key-name", statisticsTableName,	onlineStatusColumnOrKey);
				}
			}
		}

		return status;
	}

	/**
	 * Analyze the configuration for potential problems.
	 *
	 * Checks for the existence of the specified tables and columns within those
	 * tables.
	 *
	 * @param SQL SQL query object.
	 * @return boolean True if the configuration is okay.
	 */
	public boolean analyzeConfigurationOld(SQL sql)
	{
		boolean status;
		boolean userTableStatus;
		boolean multiTableStatus = true;
		boolean tempStatus;

		status = checkTable(sql, "users-table.table", users_table);
		userTableStatus = status;

		if (status)
		{
			status = status & checkColumn(sql, "users-table.username",
																		users_table,
							                      user_name_field);
			status = status & checkColumn(sql, "users-table.user-id-field",
																		users_table,
							                      user_id_field);
			if (secondary_groups)
			{
				status = status & checkColumn(sql, "user-table.secondary-groups-id-field",
								                      users_table,
																			secondary_groups_id_field);
			}

			if (useBanned)
			{
				status = status & checkColumn(sql, "user-table.banned-field",
								                      users_table,
																			is_banned_field);
			}
		}

		if (groups_table_enabled)
		{
			tempStatus = checkTable(sql, "groups-table.table", groups_table);

			status = status & tempStatus;

			if (tempStatus)
			{
				status = status & checkColumn(sql, "groups-table.user-id-field",
								                      groups_table,
																			groups_user_id_field);
				status = status & checkColumn(sql, "groups-table.group-id-field",
								                      groups_table,
																			groups_group_id_field);
			}
		}
		else
		{
			// We're not using groups table, so we check the group id designated
			// by user-table keys.
			if (status && groupSyncPrimaryEnabled)
			{
				status = status & checkColumn(sql, "users-table.groups-id-field",
								                      users_table,
																			groups_id_field);
			}
		}

		if (banlistTableEnabled)
		{
			tempStatus = checkTable(sql, "banlist-table.table", banlist_table);
			status = status & tempStatus;

			if (tempStatus)
			{
				status = status & checkColumn(sql, "banlist-table.user-id-field",
								                      banlist_table,
																			banlist_user_id_field);
				//status = status & checkColumn(sql, "banlist-table.reason-field",
				//				                      banlist_table, banlist_reason_field);
			}
		}

		if (multiTables)
		{
			multiTableStatus = checkTable(sql, "multi-table.table", multi_table);
			status = status & multiTableStatus;

			if (multiTableStatus)
			{
				status = status & checkColumn(sql, "multi-table.field-user-id-field",
								                      multi_table,
																			multi_table_user_id_field);
				if (multiTablesUseKey)
				{
					status = status & checkColumn(sql, "multi-table.field-key-field",
									                      multi_table,
																				multi_table_key_field);
				}
				else
				{
					status = status & checkColumn(sql, "multi-table.field-value-field",
									                      multi_table,
																				multi_table_value_field);
				}
			}
		}

		return status;
	}

	/**
	 * Check to see if a given column exists on a specific table.
	 *
	 * @param SQL SQL query object.
	 * @param keyName
	 * @param String containing the name of the table.
	 * @param String containing the name of the column.
	 * @return boolean True if the column exists on the table.
	 */
	private boolean checkColumn(SQL sql, String keyName,
														 String tableName, String columnName)
	{
		ResultSet result;
		String errorBase;
		errorBase = "Error while checking '" + keyName
						  + "' set to '" + columnName + "': ";

		try
		{
			result = sql.sqlQuery("SHOW COLUMNS FROM `" + tableName
							              + "` LIKE '" + columnName + "'");

			if (result == null)
			{}
			else
			{

				if (result.next())
				{
					return true;
				}
				log.severe(errorBase + "Column does not exist.");
			}
			return false;
		}
		catch (SQLException e)
		{
			log.severe(errorBase + e.getMessage());
			return false;
		}
		catch (MalformedURLException e)
		{
			log.severe(errorBase + e.getMessage());
			return false;
		}
		catch (InstantiationException e)
		{
			log.severe(errorBase + e.getMessage());
			return false;
		}
		catch (IllegalAccessException e)
		{
			log.severe(errorBase + e.getMessage());
			return false;
		}
	}

	private void checkKeyColumnForKey(SQL sql, String yamlKeyName,
																		String tableName,	String keyColumn,
																		String keyName)
	{
		String errorBase = "Error while checking " + yamlKeyName + ": ";
		String query = "SELECT COUNT(*) FROM `" + tableName + "` "
						     + "WHERE `" + keyColumn + "` = '" + keyName + "'";

		try
		{
			ResultSet result = sql.sqlQuery(query);

			if (result.getInt(1) == 0)
			{
				log.warning("There are no rows containing " + keyName
								       + " in the " + keyColumn + " column, on the "
								       + tableName + " table.");
			}
		}
		catch (SQLException e)
		{
			log.severe(errorBase + e.getMessage());
		}
		catch (MalformedURLException e)
		{
			log.severe(errorBase + e.getMessage());
		}
		catch (InstantiationException e)
		{
			log.severe(errorBase + e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			log.severe(errorBase + e.getMessage());
		}
	}

	/**
  * Check to see if a table exists.
  *
	* @param tableName Name of the table to check
	* @return Empty string if the check succeeds otherwise an error string
  */
	/**
	 * Check to see if a table exists.
	 *
	 * @param SQL An SQL query object.
	 * @param String containing the category label.
	 * @param String containing the name of the table to check.
	 * @return boolean True if the table exists.
	 */
	private boolean checkTable(SQL sql, String keyName, String tableName)
	{
		ResultSet result;
		String errorBase;
		errorBase = "Error while checking '" + keyName
						  + "' set to '" + tableName + "': ";

		try
		{
			result = sql.sqlQuery("SHOW TABLES LIKE '" + tableName + "'");

			if (result == null)
			{}
			else
			{

				if (result.next())
				{
					return true;
}
				log.severe(errorBase + "Table does not exist.");
			}
			return false;
		}
		catch (SQLException e)
		{
			log.severe(errorBase + e.getMessage());
			return false;
		}
		catch (MalformedURLException e)
		{
			log.severe(errorBase + e.getMessage());
			return false;
		}
		catch (InstantiationException e)
		{
			log.severe(errorBase + e.getMessage());
			return false;
		}
		catch (IllegalAccessException e)
		{
			log.severe(errorBase + e.getMessage());
			return false;
		}
	}

	/**
	 * Loads the configuration information from the yaml file.
	 *
	 * @param CommunityBridge The plugin object for this plugin.
	 */
	private void load()
	{
		plugin.saveDefaultConfig();
		loadSettings(plugin.getConfig());
	}

	private void loadSettings(FileConfiguration config)
	{
		logLevel = config.getString("general.log-level", "config");
		// We do this here so that the rest of the config methods can use the
		// logger with the level set as the user likes it.
		log.setLevel(logLevel);

		usePluginMetrics = config.getBoolean("general.plugin-metrics", true);

		permissionsSystem = config.getString("general.permissions-system", "");

		autoEveryUnit = config.getString("general.auto-every-unit", "ticks");
		autoSync = config.getBoolean("general.auto-sync", false);
		autoSyncEvery = config.getLong("general.auto-sync-every", 24000L);

		applicationURL = config.getString("general.application-url", "http://www.example.org/");

		// Database Section
		databaseHost = config.getString("database.hostname", "");
		databasePort = config.getString("database.port", "");
		databaseName = config.getString("database.name", "");
		databaseUsername = config.getString("database.username", "");
    databasePassword = config.getString("database.password", "");

		// Linking Section
		linkingKickUnregistered = config.getBoolean("player-user-linking.kick-unregistered", false);
		linkingAutoRemind = config.getBoolean("player-user-linking.auto-remind", false);
		linkingAutoEvery = config.getLong("player-user-linking.auto-remind-every", 12000L);
		linkingNotifyRegistered = config.getBoolean("player-user-linking.notify-registered-player", true);
		linkingNotifyUnregistered = config.getBoolean("player-user-linking.notify-unregistered-player", true);

		linkingUsesKey = config.getBoolean("player-user-linking.uses-key", false);
		linkingTableName = config.getString("player-user-linking.table-name", "");
		linkingUserIDColumn = config.getString("player-user-linking.user-id-column", "");
		linkingPlayerNameColumn = config.getString("player-user-linking.playername-column", "");

		linkingKeyName = config.getString("player-user-linking.key-name", "");
		linkingKeyColumn = config.getString("player-user-linking.key-column", "");
		linkingValueColumn = config.getString("player-user-linking.value-column", "");

		// Requirements Section
		requireAvatar = config.getBoolean("requirement.avatar.enabled", false);
		if (requireAvatar)
		{
			requireAvatarTableName = config.getString("requirement.avatar.table-name", "");
			requireAvatarUserIDColumn = config.getString("requirement.avatar.user-id-column", "");
			requireAvatarAvatarColumn = config.getString("requirement.avatar.avatar-column", "");
		}

		requireMinimumPosts = config.getBoolean("requirement.minimum-posts.enabled", false);
		if (requireMinimumPosts)
		{
			requirePostsTableName = config.getString("requirement.minimum-posts.table-name", "");
			requirePostsUserIDColumn = config.getString("requirement.minimum-posts.user-id-column", "");
			requirePostsPostCountColumn = config.getString("requirement.minimum-posts.post-count-column", "");
			requirePostsPostCount = config.getInt("requirement.minimum-posts.post-count", 0);
		}

		// Group Synchronization: Primary
		groupSyncPrimaryEnabled = config.getBoolean("group-synchronization.primary.enabled", false);
		if (groupSyncPrimaryEnabled)
		{
			groupSyncPrimaryNotifyPlayer = config.getBoolean("group-synchronization.primary.notify-player", false);
			groupSyncPrimaryTableName = config.getString("group-synchronization.primary.table-name", "");
			groupSyncPrimaryUserIDColumn = config.getString("group-synchronization.primary.user-id-column", "");

			groupSyncPrimaryUsesKey = config.getBoolean("group-synchronization.primary.uses-key", false);
			if (groupSyncPrimaryUsesKey)
			{
				groupSyncPrimaryKeyName = config.getString("group-synchronization.primary.key-name", "");
				groupSyncPrimaryKeyColumn = config.getString("group-synchronization.primary.key-column", "");
				groupSyncPrimaryValueColumn = config.getString("group-synchronization.primary.value-column", "");
			}
			else
			{
				groupSyncPrimaryGroupIDColumn = config.getString("group-synchronization.primary.group-id-column", "");
			}

			ConfigurationSection groupRules = config.getConfigurationSection("group-synchronization.primary.group-rules");
			if (groupRules == null)
			{
				log.warning("Primary group synchronization is turned on, but there are no rules defined.");
			}
			else
			{
				Set<String> rules = groupRules.getKeys(false);

				for (String ruleNumber : rules)
				{
					String ruleSectionPath = "group-synchronization.primary.group-rules." + ruleNumber + ".";

					GroupRule rule = new GroupRule();
					rule.groupID = config.getString(ruleSectionPath + "webapp-id", "");
					if (rule.groupID.isEmpty())
					{
						log.warning("Ignoring primary group rule #" + ruleNumber + ": missing web application group ID.");
						continue;
					}
					rule.groupName = config.getString(ruleSectionPath + "permissions-group", "");
					if (rule.groupName.isEmpty())
					{
						log.warning("Ignoring primary group rule #" + ruleNumber + ": missing permissions group name.");
						continue;
					}

					rule.allWorlds = config.getBoolean(ruleSectionPath + "all-worlds", false);
					if (rule.allWorlds)
					{}
					else
					{
						rule.world = config.getString(ruleSectionPath + "world", "");
					}

					if (config.getString(ruleSectionPath + "direction", "").equalsIgnoreCase("minecraft"))
					{
						rule.direction = GroupRuleDirection.MINECRAFT;
						groupSyncPrimaryMinecraftRules.put(rule.groupID, rule);
					}
					else if (config.getString(ruleSectionPath + "direction", "").equalsIgnoreCase("webapp"))
					{
						rule.direction = GroupRuleDirection.WEBAPP;
						groupSyncPrimaryWebappRules.put(rule.groupName, rule);
					}
					else
					{
						log.warning("Ignoring primary group rule #" + ruleNumber + ": invalid direction.");
						continue;
					}
				}
			}
		}

		// Group Synchronization: Secondary
		groupSyncSecondaryEnabled = config.getBoolean("group-synchronization.secondary.enabled", false);

		// Statistics Tracking Settings
		statisticsEnabled = config.getBoolean("statistics.enabled", false);

		statisticsTableName = config.getString("statistics.table-name", "");
		statisticsUserIDColumn = config.getString("statistics.user-id-column", "");
		statisticsUsesKey = config.getBoolean("statistics.uses-key", false);

		if (statisticsUsesKey)
		{
			statisticsKeyColumn = config.getString("statistics.key-column", "");
			statisticsValueColumn = config.getString("statistics.value-column", "");
		}

		onlineStatusEnabled = config.getBoolean("statistics.trackers.online-status.enabled", false);
		onlineStatusColumnOrKey = config.getString("statistics.trackers.online-status.column-or-key-name", "");
		onlineStatusValueOnline = config.getString("statistics.trackers.online-status.online-value", "");
		onlineStatusValueOffline = config.getString("statistics.trackers.online-status.offline-value", "");

		// These are calculated from settings above.
		groupSyncEnabled = groupSyncPrimaryEnabled && groupSyncSecondaryEnabled;
		permissionsSystemRequired = groupSyncEnabled;
	}

	/**
	 * Loads the configuration information from the yaml file.
	 *
	 * @param CommunityBridge The plugin object for this plugin.
	 */
	private void loadOldConfig()
	{
		plugin.saveDefaultConfig();

		FileConfiguration config = plugin.getConfig();

		// TODO: Remove this chunk after primary group synchronization is implemented.
		if (groupSyncPrimaryEnabled)
		{
			List<String> defaultList = new ArrayList<String>();
			config.addDefault("group-synchronization.primary-group.group-ids-to-ignore", defaultList);
			primaryGroupIDsToIgnore = config.getStringList("group-synchronization.primary-group.group-ids-to-ignore");
			groups = config.getConfigurationSection("groups").getValues(true);

			// Note: groups is a map <String, Object> so we need the cast.
			defaultGroup = (String)groups.get(config.getString("users-table.default-group"));

		}

		multiTables = config.getBoolean("multi-tables", false);
		multiTablesUseKey = config.getBoolean("multi-tables-use-key", false);
		useBanned = config.getBoolean("use-banned-field", false);

		banlistTableEnabled = config.getBoolean("banlist-table.enabled", false);

		groups_table_enabled = config.getBoolean("groups-table.enabled", false);

		show_primary_group = config.getBoolean("show-primary-group", false);
		secondary_groups = config.getBoolean("secondary-groups", false);

		banlist_table = config.getString("banlist-table.table", "");
		banlist_user_id_field = config.getString("banlist-table.user-id-field", "");
		banlist_banned_id_field = config.getString("banlist-table.user-id-field", "");

		if (useBanned)
		{
			is_banned_field = config.getString("users-table.banned-field", "");
		}
		else
		{
			banned_users_group = config.getString("users-table.banned-users-group", "");
		}

		groups_table = config.getString("groups-table.table", "");
		groups_user_id_field = config.getString("groups-table.user-id-field", "");
		groups_group_id_field = config.getString("groups-table.group-id-field", "");

		users_table = config.getString("users-table.table", "");
		user_id_field = config.getString("users-table.user-id-field", "");
		user_name_field = config.getString("users-table.user-name-field", "");

		groups_id_field = config.getString("users-table.groups-id-field", "");
		secondary_groups_id_field = config.getString("users-table.secondary-groups-id-field", "");

		multi_table = config.getString("multi-table.table", "");
		multi_table_user_id_field = config.getString("multi-table.field-user-id-field", "");
		multi_table_key_field = config.getString("multi-table.field-key-field", "");
		multi_table_key_value = config.getString("multi-table.field-key-value", "");
		multi_table_value_field = config.getString("multi-table.field-value-field", "");

		statisticsEnabled = config.getBoolean("enable-basic-tracking", false);

		lastonlineEnabled = config.getBoolean("basic-tracking.field-lastonline-enabled", false);
		lastonlineColumn = config.getString("basic-tracking.field-lastonline-field", "");
		lastonlineFormattedColumn = config.getString("basic-tracking.field-lastonline-formatted-field", "");
		lastonlineKeyValue = config.getString("basic-tracking.field-lastonline-key-value", "");
		lastonlineFormattedKeyValue = config.getString("basic-tracking.field-lastonline-formatted-key-value", "");

		gametimeEnabled = config.getBoolean("basic-tracking.field-gametime-enabled", false);
		gametimeColumn = config.getString("basic-tracking.field-gametime-field", "");
		gametimeFormattedColumn = config.getString("basic-tracking.field-gametime-formatted-field", "");
		gametimeKeyValue = config.getString("basic-tracking.field-gametime-key-value", "");
		gametimeFormattedKeyValue = config.getString("basic-tracking.field-gametime-formatted-key-value", "");

		totalxpEnabled = config.getBoolean("basic-tracking.field-totalxp-enabled", false);
		totalxpKeyValue = config.getString("basic-tracking.field-totalxp-key-value", "");
		totalxpColumn = config.getString("basic-tracking.field-totalxp-field", "");

		currentxpEnabled = config.getBoolean("basic-tracking.field-currentxp-enabled", false);
		currentxpColumn = config.getString("basic-tracking.field-currentxp-field", "");
		currentxpFormattedColumn = config.getString("basic-tracking.field-currentxp-formatted-field", "");
		currentxpKeyValue = config.getString("basic-tracking.field-currentxp-key-value", "");
		currentxpFormattedKeyValue = config.getString("basic-tracking.field-currentxp-formatted-key-value", "");

		levelEnabled = config.getBoolean("basic-tracking.field-level-enabled", false);
		levelColumn = config.getString("basic-tracking.field-level-field", "");
		levelKeyValue = config.getString("basic-tracking.field-level-key-value", "");

		healthEnabled = config.getBoolean("basic-tracking.field-health-enabled", false);
		healthColumn = config.getString("basic-tracking.field-health-field", "");
		healthKeyValue = config.getString("basic-tracking.field-health-key-value", "");

		lifeticksEnabled = config.getBoolean("basic-tracking.field-lifeticks-enabled", false);
		lifeticksColumn = config.getString("basic-tracking.field-lifeticks-field", "");
		lifeticksFormattedColumn = config.getString("basic-tracking.field-lifeticks-formatted-field", "");
		lifeticksKeyValue = config.getString("basic-tracking.field-lifeticks-key-value", "");
		lifeticksFormattedKeyValue = config.getString("basic-tracking.field-lifeticks-formatted-key-value", "");

		walletEnabled = config.getBoolean("basic-tracking.field-wallet-enabled", false);
		walletColumn = config.getString("basic-tracking.field-wallet-field", "");
		walletKeyValue = config.getString("basic-tracking.field-wallet-key-value", "");
	}

	/**
	 * Loads the messages from the message file.
	 *
	 * @param CommunityBridge This plugin's plugin object.
	 */
	private void loadMessages()
	{
		final String messageFilename = "messages.yml";
		File messagesFile;
		FileConfiguration messagesConfig;
		Map<String, Object> values;

		messagesFile = new File(plugin.getDataFolder(), messageFilename);

		// Make sure the file is there, if not copy the default one.
		if (messagesFile.exists())
		{}
		else
		{
			plugin.saveResource("messages.yml", false);
			messagesFile = new File(plugin.getDataFolder(), messageFilename);
		}

		messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

		// Read the key-value pairs from the configuration
		values = messagesConfig.getValues(false);

		messages.clear();
		// Store them in our own HashMap.
		for (Map.Entry<String, Object> entry : values.entrySet())
		{
			String message = (String)entry.getValue();
			message = message.replace("~APPURL~", applicationURL);
			message = message.replace("~MINIMUMPOSTCOUNT~", Integer.toString(requirePostsPostCount));

			messages.put(entry.getKey(), message);
		}
	}

	public String reload(String filename)
	{
		if (filename == null || filename.isEmpty() || filename.equals("config.yml"))
		{
			plugin.deactivate();
			load();
			plugin.activate();
			return null;
		}

		File configFile = new File(plugin.getDataFolder(), filename);

		if (configFile.exists())
		{
			plugin.deactivate();
			loadSettings(YamlConfiguration.loadConfiguration(configFile));
			plugin.activate();
			return null;
		}
		else
		{
			return "Specified file does not exist. Reload canceled.";
		}
	}

	/**
	 * Method for printing the configuration out to the logging system.
	 *
	 */
	public final void report()
	{
		// General Section
		log.config(    "Log level                            : " + logLevel);
		log.config(    "Plugin metrics enabled               : " + usePluginMetrics);
		log.config(    "Permissions System                   : " + permissionsSystem);
		log.config(    "Auto Sync                            : " + autoSync);
		if (autoSync)
		{
			log.config(  "Autosync every                       : " + autoSyncEvery + " " + autoEveryUnit);
		}

		log.config(    "Application url                      : " + applicationURL);

		// Database Section
		log.config(    "Database hostname                    : " + databaseHost);
		log.config(    "Database port                        : " + databasePort);
		log.config(    "Database name                        : " + databaseName);
		log.config(    "Database username                    : " + databaseUsername);

		// Linking Section
		log.config(    "Linking auto reminder                : " + linkingAutoRemind);
		if (linkingAutoRemind)
		{
			log.config(  "Linking auto reminder every          : " + linkingAutoEvery + " " + autoEveryUnit);
		}
		log.config(    "Linking notify registered            : " + linkingNotifyRegistered);
		log.config(    "Linking notify unregistered          : " + linkingNotifyUnregistered);
		log.config(    "Linking kick unregistered            : " + linkingKickUnregistered);
		log.config(    "Linking uses key-value pair          : " + linkingUsesKey);
		log.config(    "Linking table name                   : " + linkingTableName);
		log.config(    "Linking user ID column               : " + linkingUserIDColumn);
		if (linkingUsesKey)
		{
			log.config(  "Linking key-value pair key name      : " + linkingKeyName);
			log.config(  "Linking key-value pair key column    : " + linkingKeyColumn);
			log.config(  "Linking key-value pair value column  : " + linkingValueColumn);
		}
		else
		{
			log.config(  "Linking player name column           : " + linkingPlayerNameColumn);
		}

		log.config(    "Require avatars                      : " + requireAvatar);
		if (requireAvatar)
		{
			log.config(  "Require avatar table name            : " + requireAvatarTableName);
			log.config(  "Require avatar user ID column        : " + requireAvatarUserIDColumn);
			log.config(  "Require avatar avatar column         : " + requireAvatarAvatarColumn);
		}

		log.config(    "Require minimum posts                : " + requireMinimumPosts);
		if (requireMinimumPosts)
		{
			log.config(  "Require minimum posts table name     : " + requirePostsTableName);
			log.config(  "Require minimum posts user ID column : " + requirePostsUserIDColumn);
			log.config(  "Require minimum posts avatar column  : " + requirePostsPostCountColumn);
			log.config(  "Require minimum post count           : " + requirePostsPostCount);
		}

		log.config(    "Primary group synchronization        : " + groupSyncPrimaryEnabled);
		if (groupSyncPrimaryEnabled)
		{
			log.config(  "Primary group sync notify player     : " + groupSyncPrimaryNotifyPlayer);
			log.config(  "Primary group sync table name        : " + groupSyncPrimaryTableName);
			log.config(  "Primary group sync user ID column    : " + groupSyncPrimaryUserIDColumn);
			log.config(  "Primary group sync uses key          : " + groupSyncPrimaryUsesKey);
			if (groupSyncPrimaryUsesKey)
			{
				log.config("Primary group sync key name          : " + groupSyncPrimaryKeyName);
				log.config("Primary group sync key column        : " + groupSyncPrimaryKeyColumn);
				log.config("Primary group sync value column      : " + groupSyncPrimaryValueColumn);
			}
			else
			{
				log.config("Primary group sync group ID column   : " + groupSyncPrimaryGroupIDColumn);
			}
			log.config(  "Primary group sync rule count        : " + (groupSyncPrimaryWebappRules.size() + groupSyncPrimaryMinecraftRules.size()));
		}

		// Old System
		log.config(  "Multi Tables                         : " + multiTables);

		log.config(  "Statistics Tracking                  : " + statisticsEnabled);

		if (statisticsEnabled)
		{
			log.config("Tracking Online Status               : " + onlineStatusEnabled);
			log.config("Tracking Last Online                 : " + lastonlineEnabled);
			log.config("Tracking Game Time                   : " + gametimeEnabled);
			log.config("Tracking Total XP                    : " + totalxpEnabled);
			log.config("Tracking Current XP                  : " + currentxpEnabled);
			log.config("Tracking Level                       : " + levelEnabled);
			log.config("Tracking Health                      : " + healthEnabled);
			log.config("Tracking Life Ticks                  : " + lifeticksEnabled);
			log.config("Tracking Wallet                      : " + walletEnabled);
		}
	}
}
