/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ruhlendavis.mc.communitybridge;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.netmanagers.community.Main;

/**
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

	public boolean groupSynchronizationPrimaryEnabled;
	public List<String> primaryGroupIDsToIgnore;

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

	public Configuration(Main plugin)
	{
		loadConfig(plugin);
		reportConfig();
	}

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

		groupSynchronizationPrimaryEnabled = config.getBoolean("group-synchronization.primary-group.enabled", false);
		if (groupSynchronizationPrimaryEnabled)
		{
			List<String> defaultList = new ArrayList<String>();
			config.addDefault("group-synchronization.primary-group.group-ids-to-ignore", defaultList);
			primaryGroupIDsToIgnore = config.getStringList("group-synchronization.primary-group.group-ids-to-ignore");
		}

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

	private void reportConfig()
	{
		Main.log.config(  "Log level:                             " + logLevel);
		Main.log.config(  "Plugin metrics enabled:                " + usePluginMetrics);

		Main.log.config(  "Primary Group Synchronization Enabled: "
									+ groupSynchronizationPrimaryEnabled);

		if (groupSynchronizationPrimaryEnabled)
		{
			Main.log.config("Primary Group IDs to Ignore:           " + primaryGroupIDsToIgnore);
		}
	}
}
