package org.communitybridge.main;

import java.io.IOException;
import java.util.logging.Level;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.communitybridge.linker.UserPlayerLinker;
import org.communitybridge.main.CBMetrics.Graph;
import org.communitybridge.utility.Log;
import org.communitybridge.permissionhandlers.*;
import org.communitybridge.utility.MinecraftUtilities;

/**
 * Main plugin class
 *
 * During a normal startup, first CraftBukkit calls the onEnable method,
 * which in turn calls activate(). If, however, the configuration has a
 * problem, instead of disabling the plugin, which would disable the
 * configuration reload command, we "deactivate" instead. This leaves the
 * plugin "enabled" in the eyes of CraftBukkit so that the reload can be
 * used, but "disabled" in reality to prevent things from going wrong when
 * the configuration is broken. Correspondingly, during a configuration
 * reload, first deactivate() is called if necessary, the new configuration
 * is loaded, and then activate() is called.
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */

public final class CommunityBridge extends JavaPlugin
{
	private Environment environment = new Environment();

	public static Configuration config;
	public static WebApplication webapp;
	public static PermissionHandler permissionHandler;
	@SuppressWarnings("NonConstantLogger")
	public static Log log;
	public static SQL sql;
	public static Economy economy;

	@SuppressWarnings("PMD.UnusedPrivateField")
	private static CommunityBridge instance;
	private static boolean active;
	private static CBMetrics metrics;

	/**
	 * Handles all the set up for the plugin.
	 *
	 */
	@Override
	public void onEnable()
  {
		instance = this;
		log = new Log(this.getLogger(), Level.CONFIG);
		config = new Configuration(this, log);

		CBCommandExecutor command = new CBCommandExecutor(config, log);
		getCommand("cbreload").setExecutor(command);
		getCommand("cbsync").setExecutor(command);
		getCommand("cbsyncall").setExecutor(command);

//		getCommand("cbban").setExecutor(new CBCommandExecutor(config, log));
//		getCommand("cbunban").setExecutor(new CBCommandExecutor(config, log));
//		getCommand("cbrank").setExecutor(new CBCommandExecutor(config, log));

		activate();

		environment.setConfiguration(config);
		environment.setLog(log);
		environment.setPermissionHandler(permissionHandler);
		environment.setSql(sql);
		environment.setUserPlayerLinker(new UserPlayerLinker(environment, Bukkit.getMaxPlayers() * 4));

		if (CommunityBridge.isActive())
		{
			log.info("CommunityBridge is now active.");
		}
	}

	/**
	 * Handles all the setup to be done during activation.
	 */
	public void activate()
	{
		if (config.databaseUsername.equals("username")
		 && config.databasePassword.equals("password"))
		{
			log.severe("You need to set configuration options in the config.yml.");
			deactivate();
			return;
		}

		// If a feature requires a permissions system we load it up here.
		if (config.permissionsSystemRequired)
		{
			selectPermissionsHandler();
		}

		if (enableSQL(false) == false)
		{
			deactivate();
			return;
		}

		webapp = new WebApplication(this, environment);
		webapp.loadOnlineUserIDsFromDatabase();
		getServer().getPluginManager().registerEvents(new PlayerListener(environment, webapp), this);

		if (config.economyEnabled || config.statisticsEnabled && config.walletEnabled)
		{
	    if (getServer().getPluginManager().getPlugin("Vault") == null)
			{
				log.warning("Vault not present. Temporarily disabling economy based features.");
				config.economyEnabled = false;
				config.walletEnabled = false;
			}
			else
			{
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
				{
					log.warning("Failure getting economy service registration. Is an economy plugin installed? Temporarily disabling economy based features.");
					config.economyEnabled = false;
					config.walletEnabled = false;
	      }
				else
				{
	        economy = rsp.getProvider();
					if (economy == null)
					{
						log.warning("Failure getting economy provider. Temporarily disabling economy based features.");
						config.economyEnabled = false;
						config.walletEnabled = false;
					}
				}
			}
		}

		activateMetrics();

		if (config.linkingAutoRemind)
		{
			reminderStart();
		}

		if (config.autoSync)
		{
			autosyncStart();
		}

		active = true;
		log.finest("CommunityBridge activated.");
	}

	/**
	 * Handles any clean up that needs done when the plugin is disabled.
	 */
	@Override
	public void onDisable()
  {
		deactivate();

//		getCommand("cbban").setExecutor(null);
//		getCommand("cbunban").setExecutor(null);
//		getCommand("cbrank").setExecutor(null);
		getCommand("cbreload").setExecutor(null);
		getCommand("cbsync").setExecutor(null);
		getCommand("cbsyncall").setExecutor(null);

		config = null;

		log.config("Disabled...");
		log = null;
		environment = null;
		instance = null;
	}

	/**
	 * Handles any clean up that needs to be done when the plugin is deactivated.
	 */
	public void deactivate()
	{
		active = false;

		// Cancel the tasks: autoRemind and autoSync
		Bukkit.getServer().getScheduler().cancelTasks(this);

		permissionHandler = null;

		// Drop all of our listeners
		HandlerList.unregisterAll(this);

		webapp = null;

		if (metrics != null)
		{
			try
			{
				metrics.cancelTask();
			}
			catch (NoSuchMethodError exception)
			{
				log.warning("Metrics cancelTask() method unavailable: " + exception.getMessage());
			}
			metrics = null;
		}

		if (sql != null)
    {
			sql.close();
			sql = null;
		}

		if (economy != null)
		{
			economy = null;
		}

		log.finest("CommunityBridge deactivated.");
	}

	/**
	 * Returns true if the plugin is active.
	 */
	public static boolean isActive()
	{
		return active;
	}

	/**
	 * Called by activate() if the auto reminder to register is turned on, this
	 * method starts up the reminder task.
	 */
	private void reminderStart()
  {
		MinecraftUtilities.startTaskTimer(this,
																			calculateTaskTicks(config.linkingAutoEvery),
																			new Runnable()
																			{
																				@Override
																				public void run()
																				{
																					remindUnregisteredPlayers();
																				}
																			}
																		 );
		log.fine("Auto reminder started.");
  }

	/**
	 * Called by activate() if the auto sync is turned on, this starts up the
	 * auto synchronization task runner.
	 */
	private void autosyncStart()
  {
		MinecraftUtilities.startTaskTimer(this,
																			calculateTaskTicks(config.autoSyncEvery),
																			new Runnable()
																			{
																				@Override
																				public void run()
																				{
																					webapp.synchronizeAll();
																				}
																			}
																		 );
		log.fine("Auto synchronization started.");
  }

	/**
	 * Reminds a single player to register if they are not registered.
	 * If linking-kick-unregistered is turned on, an unregistered player will
	 * be kicked.
	 */
  private void remindPlayer(Player player)
  {
		String userID = environment.getUserPlayerLinker().getUserID(player);
    if (userID == null || userID.isEmpty())
    {
			String playerName = player.getName();
      if (config.linkingKickUnregistered)
      {
        player.kickPlayer(config.messages.get("link-unregistered-player"));
				log.info(playerName + " kicked because they are not registered.");
      }
      else
      {
        player.sendMessage(ChatColor.RED + config.messages.get("link-unregistered-reminder"));
        log.fine(playerName + " issued unregistered reminder notice");
      }
    }
  }

	/**
	 * Calls remindPlayer() for all connected players. Called by the reminder
	 * task.
	 */
  private void remindUnregisteredPlayers()
  {
    log.fine("Running unRegistered auto reminder");

    for (Player player : Bukkit.getOnlinePlayers())
    {
      remindPlayer(player);
    }

  }

	/**
	 * (Re)Starts up the SQL connection to the web application.
	 *
	 * @return boolean False if the connection fails for any reason.
	 */
	public boolean enableSQL(boolean reload)
	{
		if (reload)
		{
			sql.close();
		}

		sql = new SQL(config.databaseHost + ":" + config.databasePort,
									config.databaseName + "",
									config.databaseUsername + "",
									config.databasePassword + "",
									config.databaseBindingAddress);
		sql.initialize();

		if (sql.checkConnection() == false)
		{
			log.severe("Disabling CommunityBridge due to previous error.");
			return false;
		}

		if (reload)
		{
			environment.setSql(sql);
			webapp.setSQL(sql);
		}

		if (config.analyze(sql) == false)
		{
			return false;
		}

		return true;
	}

	private void activateMetrics()
	{
		if (config.usePluginMetrics)
		{
			try
			{
				metrics = new CBMetrics(this);
				Graph permsGraph = metrics.createGraph("Permissions Plugin Used");

				if (permissionHandler == null)
				{
					permsGraph.addPlotter(new CBMetrics.Plotter("None")
					{
						@Override
						public int getValue()
						{
							return 1;
						}
					});
				}
				else if (config.permissionsSystem.equalsIgnoreCase("bPerms"))
				{
					permsGraph.addPlotter(new CBMetrics.Plotter("bPermissions")
					{
						@Override
						public int getValue()
						{
							return 1;
						}
					});
				}
				else if (config.permissionsSystem.equalsIgnoreCase("GroupManager"))
				{
					permsGraph.addPlotter(new CBMetrics.Plotter("GroupManager")
					{
						@Override
						public int getValue()
						{
							return 1;
						}
					});
				}
				else if (config.permissionsSystem.equalsIgnoreCase("PermsBukkit"))
				{
					permsGraph.addPlotter(new CBMetrics.Plotter("PermissionsBukkit")
					{
						@Override
						public int getValue()
						{
							return 1;
						}
					});
				}
				else if (config.permissionsSystem.equalsIgnoreCase("PEX"))
				{
					permsGraph.addPlotter(new CBMetrics.Plotter("PermissionsEx")
					{
						@Override
						public int getValue()
						{
							return 1;
						}
					});
				}
				else if (config.permissionsSystem.equalsIgnoreCase("Vault"))
				{
					permsGraph.addPlotter(new CBMetrics.Plotter("Vault")
					{
						@Override
						public int getValue()
						{
							return 1;
						}
					});
				}
				metrics.start();
				log.fine("Plugin Metrics activated.");
			}
			catch (IOException e)
			{
				log.warning("Plugin Metrics activation failed.");
			}
		}
	}

	private void selectPermissionsHandler()
	{
		try
		{
			if (config.permissionsSystem.equalsIgnoreCase("PEX"))
			{
				permissionHandler = new PermissionHandlerPermissionsEx();
				log.config("Permissions System: PermissionsEx (PEX)");
			}
			else if (config.permissionsSystem.equalsIgnoreCase("bPerms"))
			{
				permissionHandler = new PermissionHandlerBPermissions();
				log.config("Permissions System: bPermissions (bPerms)");
			}
			else if (config.permissionsSystem.equalsIgnoreCase("GroupManager"))
			{
				permissionHandler = new PermissionHandlerGroupManager();
				log.config("Permissions System: GroupManager");
			}
			else if (config.permissionsSystem.equalsIgnoreCase("PermsBukkit"))
			{
				permissionHandler = new PermissionHandlerPermissionsBukkit();
				log.config("Permissions System: PermissionsBukkit (PermsBukkit)");
			}
			else if (config.permissionsSystem.equalsIgnoreCase("Vault"))
			{
				permissionHandler = new PermissionHandlerVault();
				log.config("Permissions System: Vault");
			}
			else if (config.permissionsSystem.equalsIgnoreCase("zPermissions"))
			{
				permissionHandler = new PermissionHandlerZPermissions();
			}
			else
			{
				log.severe("Unknown permissions system in config.yml. Features dependent on a permissions system disabled.");
				config.disableFeaturesDependentOnPermissions();
			}
		}
		catch (IllegalStateException e)
		{
			log.severe(e.getMessage());
			log.severe("Disabling features dependent on a permissions system.");
			config.disableFeaturesDependentOnPermissions();
		}
	}

	private long calculateTaskTicks(final long every)
	{
		if (config.autoEveryUnit.startsWith("sec"))
		{
			return every * 20; // 20 ticks per second.
		}
		else if (config.autoEveryUnit.startsWith("min"))
		{
			return every * 1200; // 20 ticks per second, 60 sec/minute
		}
		else if (config.autoEveryUnit.startsWith("hou"))
		{
			return every * 72000; // 20 ticks/s 60s/m, 60m/h
		}
		else if (config.autoEveryUnit.startsWith("day"))
		{
			return every * 1728000; // 20 ticks/s 60s/m, 60m/h, 24h/day
		}
		else
		{
			// Effectively defaulting to ticks.
			return every;
		}
	}
}
