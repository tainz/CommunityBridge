package org.ruhlendavis.mc.communitybridge;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.netmanagers.api.SQL;
import net.netmanagers.community.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Class for storing configuration information loaded from the yaml files.
 *
 * @author Feaelin (Iain E. Davis) <iain@ruhlendavis.org>
 */
public class Configuration
{
	public Map<String, String> messages = new HashMap();
	public String logLevel;
	public boolean usePluginMetrics;

	public String databaseHost;
	public String databasePort;
	public String databaseName;
	public String databaseUsername;
	public String databasePassword;
	public String permissionsSystem;

  public boolean auto_sync;
  public boolean auto_remind;
  public String auto_every_unit;
	public long auto_sync_every;
	public long auto_remind_every;

	public boolean groupSynchronizationPrimaryEnabled;
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
	public boolean kick_unregistered = false;
	public boolean require_avatar = false;
	public boolean require_minposts = false;

	public String users_table;
	public String banlist_table;
	public String groups_table;
	public String multi_table;
	public String avatar_table;
	public String minposts_table;

	public String avatar_user_field;
	public String avatar_field;
	public String avatar_message;
	public String minposts_user_field;
	public String minposts_field;
	public String minposts_message;

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

	public String registered_message;
	public String unregistered_message;
	public String unregistered_messagereminder;

	public boolean statisticsTrackingEnabled;

	public boolean onlinestatusEnabled;
	public String onlinestatusColumn;
	public String onlinestatusKeyValue;
	public String onlinestatusValueOffline;
	public String onlinestatusValueOnline;

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
	 * @param Main The plugin object of this plugin.
	 */
	public Configuration(Main plugin)
	{
		loadConfig(plugin);
		loadMessages(plugin);
		reportConfig();
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
			if (status && groupSynchronizationPrimaryEnabled)
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

		if (require_avatar)
		{
			tempStatus = checkTable(sql, "profile-requirements.require-avatar-table",
							                avatar_table);
			status = status & tempStatus;

			if (tempStatus)
			{
				status = status
							 & checkColumn(sql, "profile-requirements.require-avatar-users-id-field",
								             avatar_table, avatar_user_field);
				status = status
							 & checkColumn(sql, "profile-requirements.require-avatar-field",
								             avatar_table, avatar_field);
			}
		}

		if (require_minposts)
		{
			tempStatus = checkTable(sql, "profile-requirements.require-minposts-table",
							                minposts_table);
			status = status & tempStatus;

			if (tempStatus)
			{
				status = status
							 & checkColumn(sql, "profile-requirements.require-minposts-user-id-field",
								             minposts_table, minposts_user_field);
				status = status
							 & checkColumn(sql, "profile-requirements.require-minposts-user-id-field",
								             minposts_table, minposts_field);
			}
		}

		if (statisticsTrackingEnabled)
		{
			if (multiTables && multiTableStatus)
			{
				checkTrackingColumns(sql, multi_table);
			}
			else if (userTableStatus)
			{
				checkTrackingColumns(sql, users_table);
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
				Main.log.severe(errorBase + "Column does not exist.");
			}
			return false;
		}
		catch (SQLException e)
		{
			Main.log.severe(errorBase + e.getMessage());
			return false;
		}
		catch (MalformedURLException e)
		{
			Main.log.severe(errorBase + e.getMessage());
			return false;
		}
		catch (InstantiationException e)
		{
			Main.log.severe(errorBase + e.getMessage());
			return false;
		}
		catch (IllegalAccessException e)
		{
			Main.log.severe(errorBase + e.getMessage());
			return false;
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
				Main.log.severe(errorBase + "Table does not exist.");
			}
			return false;
		}
		catch (SQLException e)
		{
			Main.log.severe(errorBase + e.getMessage());
			return false;
		}
		catch (MalformedURLException e)
		{
			Main.log.severe(errorBase + e.getMessage());
			return false;
		}
		catch (InstantiationException e)
		{
			Main.log.severe(errorBase + e.getMessage());
			return false;
		}
		catch (IllegalAccessException e)
		{
			Main.log.severe(errorBase + e.getMessage());
			return false;
		}
	}

	/**
	 * Check the statistics tracking columns.
	 *
	 * @param SQL An SQL query object.
	 * @param String containing the name of the statistics tracking table.
	 */
	private void checkTrackingColumns(SQL sql, String trackingTable)
	{
		if (onlinestatusEnabled)
		{
			if (checkColumn(sql, "basic-tracking.field-onlinestatus-field", trackingTable,
							        onlinestatusColumn))
			{}
			else
			{
				onlinestatusEnabled = false;
				Main.log.severe("'online status' tracking disabled due to previous error.");
			}
		}

		if (lastonlineEnabled)
		{
			if (checkColumn(sql, "basic-tracking.field-lastonline-field", trackingTable,
				              lastonlineColumn)
			 && checkColumn(sql, "basic-tracking.field-lastonline-formatted-field",
							        trackingTable, lastonlineFormattedColumn))
			{}
			else
			{
				lastonlineEnabled = false;
				Main.log.severe("'last online' tracking disabled due to previous error(s).");
			}
		}

		if (gametimeEnabled)
		{
			if (checkColumn(sql, "basic-tracking.field-gametime-field", trackingTable,
						          gametimeColumn)
			 && checkColumn(sql, "basic-tracking.field-gametime-formatted-field",
							        trackingTable, gametimeFormattedColumn))
			{}
			else
			{
				gametimeEnabled = false;
				Main.log.severe("'game time' tracking disabled due to previous error(s).");
			}
		}

		if (totalxpEnabled)
		{
			if (checkColumn(sql, "basic-tracking.field-totalxp-field", trackingTable,
						          totalxpColumn))
			{}
			else
			{
				totalxpEnabled = false;
				Main.log.severe("'total xp' tracking disabled due to previous error(s).");
			}
		}

		if (currentxpEnabled)
		{
			if (checkColumn(sql, "basic-tracking.field-currentxp-field", trackingTable,
						          currentxpColumn)
			 && checkColumn(sql, "basic-tracking.field-currentxp-formatted-field",
							        trackingTable, currentxpFormattedColumn))
			{}
			else
			{
				currentxpEnabled = false;
				Main.log.severe("'current xp' tracking disabled due to previous error(s).");
			}
		}

		if (levelEnabled)
		{
			if (checkColumn(sql, "basic-tracking.field-level-field", trackingTable,
						          levelColumn))
			{}
			else
			{
				levelEnabled = false;
				Main.log.severe("'level' tracking disabled due to previous error(s).");
			}
		}

		if (healthEnabled)
		{
			if (checkColumn(sql, "basic-tracking.field-health-field", trackingTable,
						          healthColumn))
			{}
			else
			{
				healthEnabled = false;
				Main.log.severe("'health' tracking disabled due to previous error(s).");
			}
		}

		if (lifeticksEnabled)
		{
			if (checkColumn(sql, "basic-tracking.field-lifeticks-field", trackingTable,
						          lifeticksColumn)
			 && checkColumn(sql, "basic-tracking.field-lifeticks-formatted-field",
							        trackingTable, lifeticksFormattedColumn))
			{}
			else
			{
				lifeticksEnabled = false;
				Main.log.severe("'lifeticks' tracking disabled due to previous error(s).");
			}
		}

		if (walletEnabled)
		{
			if (checkColumn(sql, "basic-tracking.field-wallet-field", trackingTable,
						          walletColumn))
			{}
			else
			{
				walletEnabled = false;
				Main.log.severe("'wallet' tracking disabled due to previous error(s).");
			}
		}

    if ((onlinestatusEnabled || lastonlineEnabled	|| gametimeEnabled
			|| totalxpEnabled			 || currentxpEnabled	|| levelEnabled
			|| healthEnabled       || lifeticksEnabled	|| walletEnabled))
		{}
		else
    {
      statisticsTrackingEnabled = false;
      Main.log.severe("Basic tracking is enabled, but all individual trackers are"
                +" disabled. Basic tracking is now turned off.");
    }
	}

	/**
	 * Loads the configuration information from the yaml file.
	 *
	 * @param Main The plugin object for this plugin.
	 */
	private void loadConfig(Main plugin)
	{
		plugin.saveDefaultConfig();

		org.bukkit.configuration.file.FileConfiguration config;
		config = plugin.getConfig();

		// EXPIRABLE: We'll remove the deprecated setting in six months. Remove On: 2013/May/13
		// We do this first so that if log-level is set, it will override the
		// deprecated setting 'show-config'.
		if (config.getBoolean("show-config", false))
		{
			Main.log.warning("The setting 'show-config' in config.yml is deprecated. Use log-level: config instead.");
			Main.log.setLevel(Level.CONFIG);
		}

		// Either way, we should set the log level before doing anything else.
		logLevel = config.getString("log-level", "config");
		Main.log.setLevel(logLevel);

		usePluginMetrics = config.getBoolean("plugin-metrics", true);

		// Database Section
		databaseHost = config.getString("db-host", "");
		databasePort = config.getString("db-port", "");
		databaseName = config.getString("db-database", "");
		databaseUsername = config.getString("db-username", "");
    databasePassword = config.getString("db-password", "");

		permissionsSystem = config.getString("permissions-system", "");

		auto_sync = config.getBoolean("auto-sync", false);
		auto_remind = config.getBoolean("auto-remind", false);
		auto_every_unit = config.getString("auto-every-unit", "ticks");

		auto_sync_every = config.getLong("auto-sync-every", 24000L);
		auto_remind_every = config.getLong("auto-remind-every", 12000L);

		groupSynchronizationPrimaryEnabled = config.getBoolean("group-synchronization.primary-group.enabled", false);

		if (groupSynchronizationPrimaryEnabled)
		{
			List<String> defaultList = new ArrayList<String>();
			config.addDefault("group-synchronization.primary-group.group-ids-to-ignore", defaultList);
			primaryGroupIDsToIgnore = config.getStringList("group-synchronization.primary-group.group-ids-to-ignore");
		}
		groups = config.getConfigurationSection("groups").getValues(true);

		// Note: groups is a map <String, Object> so we need the cast.
		defaultGroup = (String)groups.get(config.getString("users-table.default-group"));

		multiTables = config.getBoolean("multi-tables", false);
		multiTablesUseKey = config.getBoolean("multi-tables-use-key", false);
		useBanned = config.getBoolean("use-banned-field", false);

		banlistTableEnabled = config.getBoolean("banlist-table.enabled", false);

		groups_table_enabled = config.getBoolean("groups-table.enabled", false);

		show_primary_group = config.getBoolean("show-primary-group", false);
		secondary_groups = config.getBoolean("secondary-groups", false);
		kick_unregistered = config.getBoolean("kick-unregistered", false);

		require_avatar = config.getBoolean("profile-requirements.require-avatar", false);
		avatar_table = config.getString("profile-requirements.require-avatar-table", "");
		avatar_user_field = config.getString("profile-requirements.require-avatar-user-id-field", "");
		avatar_field = config.getString("profile-requirements.require-avatar-field", "");
		avatar_message = config.getString("profile-requirements.require-avatar-message", "");

		require_minposts = config.getBoolean("profile-requirements.require-minposts", false);
		minposts_required =  config.getInt("profile-requirements.require-minposts-count", 0);
		minposts_table = config.getString("profile-requirements.require-minposts-table", "");
		minposts_user_field = config.getString("profile-requirements.require-minposts-user-id-field", "");
		minposts_field = config.getString("profile-requirements.require-minposts-field", "");
		minposts_message = config.getString("profile-requirements.require-minposts-message", "");

		registered_message = config.getString("registered-message", "");
		unregistered_message = config.getString("unregistered-message", "");
		unregistered_messagereminder = config.getString("unregistered-messagereminder", "");

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

		statisticsTrackingEnabled = config.getBoolean("enable-basic-tracking", false);

		onlinestatusEnabled = config.getBoolean("basic-tracking.field-onlinestatus-enabled", false);
		onlinestatusKeyValue = config.getString("basic-tracking.field-onlinestatus-key-value", "");
		onlinestatusColumn = config.getString("basic-tracking.field-onlinestatus-field", "");
		onlinestatusValueOnline = config.getString("basic-tracking.field-onlinestatus-valueonline", "");
		onlinestatusValueOffline = config.getString("basic-tracking.field-onlinestatus-valueoffline", "");

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
	 * @param Main This plugin's plugin object.
	 */
	private void loadMessages(Main plugin)
	{
		final String messageFilename = "messages.yml";
		File messagesFile;
		FileConfiguration messagesConfig;
		InputStream defaultMessagesStream;
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

		// Look for defaults in the jar
		defaultMessagesStream = plugin.getResource(messageFilename);
		if (defaultMessagesStream == null)
		{}
		else
		{
			YamlConfiguration defaultMessagesConfig;
			defaultMessagesConfig = YamlConfiguration.loadConfiguration(defaultMessagesStream);
		}

		// Read the key-value pairs from the configuration
		values = messagesConfig.getValues(false);

		messages.clear();
		// Store them in our own HashMap.
		for (Map.Entry<String, Object> entry : values.entrySet())
		{
			// TODO: Perform any non-situation specific string replacements here.
			messages.put(entry.getKey(), (String)entry.getValue());
		}
	}

	/**
	 * Method for printing the configuration out to the logging system.
	 *
	 */
	private void reportConfig()
	{
		Main.log.config(  "Log level                            : " + logLevel);
		Main.log.config(  "Plugin metrics enabled               : " + usePluginMetrics);
		Main.log.config(  "Auto Sync                            : " + auto_sync);
		Main.log.config(  "Auto Remind                          : " + auto_remind);
		Main.log.config(  "Multi Tables                         : " + multiTables);
		Main.log.config(  "Primary Group Synchronization Enabled: "	+ groupSynchronizationPrimaryEnabled);
		Main.log.config(  "Kick Unregistered                    : " + kick_unregistered);
		Main.log.config(  "Require Avatar                       : " + require_avatar);
		Main.log.config(  "Min Posts                            : " + require_minposts);

		if (groupSynchronizationPrimaryEnabled)
		{
			Main.log.config("Primary Group IDs to Ignore          : " + primaryGroupIDsToIgnore);
		}
		Main.log.config(  "Statistics Tracking                  : " + statisticsTrackingEnabled);

		if (statisticsTrackingEnabled)
		{
			Main.log.config("Tracking Online Status               : " + onlinestatusEnabled);
			Main.log.config("Tracking Last Online                 : " + lastonlineEnabled);
			Main.log.config("Tracking Game Time                   : " + gametimeEnabled);
			Main.log.config("Tracking Total XP                    : " + totalxpEnabled);
			Main.log.config("Tracking Current XP                  : " + currentxpEnabled);
			Main.log.config("Tracking Level                       : " + levelEnabled);
			Main.log.config("Tracking Health                      : " + healthEnabled);
			Main.log.config("Tracking Life Ticks                  : " + lifeticksEnabled);
			Main.log.config("Tracking Wallet                      : " + walletEnabled);
		}
	}
}
