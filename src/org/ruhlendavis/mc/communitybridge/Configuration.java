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
	public boolean lastonlineEnabled;
	public boolean gametimeEnabled;
	public boolean totalxpEnabled;
	public boolean currentxpEnabled;
	public boolean levelEnabled;
	public boolean healthEnabled;
	public boolean lifeticksEnabled;
	public boolean walletEnabled;

	public boolean usePluginMetrics;

	public Configuration(Main plugin)
	{
		loadConfig(plugin);
		reportConfig();
	}

	private void loadConfig(Main plugin)
	{
		plugin.saveDefaultConfig();

		// EXPIRABLE: We'll remove the deprecated setting in six months. Remove On: 2013/May/13
		// We do this first so that if log-level is set, it will override the
		// deprecated setting 'show-config'.
		if (plugin.getConfig().getBoolean("show-config", false))
		{
			Main.log.warning("The setting 'show-config' in config.yml is deprecated. Use log-level: config instead.");
			Main.log.setLevel(Level.CONFIG);
		}

		// Either way, we should set the log level before doing anything else.
		logLevel = plugin.getConfig().getString("log-level", "config");
		Main.log.setLevel(logLevel);

		usePluginMetrics = plugin.getConfig().getBoolean("plugin-metrics", true);

		// Database Section
		databaseHost = plugin.getConfig().getString("db-host", "database hostname unknown");
		databasePort = plugin.getConfig().getString("db-port", "database port unknown");
		databaseName = plugin.getConfig().getString("db-database", "database name unknown");
		databaseUsername = plugin.getConfig().getString("db-username", "database username unknown");
    databasePassword = plugin.getConfig().getString("db-password", "database password unknown");

		permissionsSystem = plugin.getConfig().getString("permissions-system", "unknown");

		groupSynchronizationPrimaryEnabled = plugin.getConfig().getBoolean("group-synchronization.primary-group.enabled", false);
		if (groupSynchronizationPrimaryEnabled)
		{
			List<String> defaultList = new ArrayList<String>();
			plugin.getConfig().addDefault("group-synchronization.primary-group.group-ids-to-ignore", defaultList);
			primaryGroupIDsToIgnore = plugin.getConfig().getStringList("group-synchronization.primary-group.group-ids-to-ignore");
		}

		statisticsTrackingEnabled = plugin.getConfig().getBoolean("enable-basic-tracking", false);

		onlinestatusEnabled = plugin.getConfig().getBoolean("basic-tracking.field-onlinestatus-enabled", false);
		lastonlineEnabled = plugin.getConfig().getBoolean("basic-tracking.field-lastonline-enabled", false);
		gametimeEnabled = plugin.getConfig().getBoolean("basic-tracking.field-gametime-enabled", false);
		totalxpEnabled = plugin.getConfig().getBoolean("basic-tracking.field-totalxp-enabled", false);
		currentxpEnabled = plugin.getConfig().getBoolean("basic-tracking.field-currentxp-enabled", false);
		levelEnabled = plugin.getConfig().getBoolean("basic-tracking.field-level-enabled", false);
		healthEnabled = plugin.getConfig().getBoolean("basic-tracking.field-health-enabled", false);
		lifeticksEnabled = plugin.getConfig().getBoolean("basic-tracking.field-lifeticks-enabled", false);
		walletEnabled = plugin.getConfig().getBoolean("basic-tracking.field-wallet-enabled", false);
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
