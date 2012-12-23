package org.ruhlendavis.mc.communitybridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.netmanagers.community.Main;

/**
 * Class for storing configuration information loaded from the yaml files.
 *
 * @author Feaelin (Iain E. Davis) <iain@ruhlendavis.org>
 */
public class Configuration
{
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
		reportConfig();
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
	 * Method for printing the configuration out to the logging system.
	 *
	 */
	private void reportConfig()
	{
		Main.log.config(  "Log level:                             " + logLevel);
		Main.log.config(  "Plugin metrics enabled:                " + usePluginMetrics);
		Main.log.config(  "Auto Sync   :                          " + auto_sync);
		Main.log.config(  "Auto Remind :                          " + auto_remind);
		Main.log.config(  "Multi Tables :                         " + multiTables);
		Main.log.config(  "Primary Group Synchronization Enabled: "	+ groupSynchronizationPrimaryEnabled);
		Main.log.config(  "Kick Unregistered :                    " + kick_unregistered);
		Main.log.config(  "Require Avatar :                       " + require_avatar);
		Main.log.config(  "Min Posts :                            " + require_minposts);

		// The new group synchronization section is handled here.
		// Beginning with primary group.



		if (groupSynchronizationPrimaryEnabled)
		{
			Main.log.config("Primary Group IDs to Ignore:           " + primaryGroupIDsToIgnore);
		}
		Main.log.config(  "Basic Tracking :                       " + statisticsTrackingEnabled);

		if (statisticsTrackingEnabled)
		{
			Main.log.config("Tracking Online Status :               " + onlinestatusEnabled);
			Main.log.config("Tracking Last Online   :               " + lastonlineEnabled);
			Main.log.config("Tracking Game Time     :               " + gametimeEnabled);
			Main.log.config("Tracking Total XP      :               " + totalxpEnabled);
			Main.log.config("Tracking Current XP    :               " + currentxpEnabled);
			Main.log.config("Tracking Level         :               " + levelEnabled);
			Main.log.config("Tracking Health        :               " + healthEnabled);
			Main.log.config("Tracking Life Ticks    :               " + lifeticksEnabled);
			Main.log.config("Tracking Wallet        :               " + walletEnabled);
		}
	}
}
