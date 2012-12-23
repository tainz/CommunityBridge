/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ruhlendavis.mc.communitybridge;

import java.util.logging.Level;
import net.netmanagers.community.Main;

/**
 *
 * @author Feaelin (Iain E. Davis) <iain@ruhlendavis.org>
 */
public class Configuration
{
	public String databaseHost;
	public String databasePort;
	public String databaseName;
	public String databaseUsername;
	public String databasePassword;
	public String permissionsSystem;
	public boolean usePluginMetrics;

	public Configuration(Main plugin)
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
		Main.log.setLevel(plugin.getConfig().getString("log-level", "config"));

		usePluginMetrics = plugin.getConfig().getBoolean("plugin-metrics", true);

		// Database Section
		databaseHost = plugin.getConfig().getString("db-host");
		databasePort = plugin.getConfig().getString("db-port");
		databaseName = plugin.getConfig().getString("db-database");
		databaseUsername = plugin.getConfig().getString("db-username");
    databasePassword = plugin.getConfig().getString("db-password");

		permissionsSystem = plugin.getConfig().getString("permissions-system");
	}
}
